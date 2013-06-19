/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.api.action.user;

import android.content.Context;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.*;
import com.socialize.auth.*;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.NotificationRegistrationSystem;
import com.socialize.provider.SocializeProvider;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.BitmapUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeUserSystem extends SocializeApi<User, SocializeProvider<User>> implements UserSystem {

	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private SocializeSessionPersister sessionPersister;
	private DeviceUtils deviceUtils;
	private SocializeLogger logger;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	private SocializeAuthProviderInfoFactory socializeAuthProviderInfoFactory;
	private NotificationRegistrationSystem notificationRegistrationSystem;
	private BitmapUtils bitmapUtils;
	
	public SocializeUserSystem(SocializeProvider<User> provider) {
		super(provider);
	}
	
	@Override
	public void authenticate(Context context, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		authenticate(context, consumerKey, consumerSecret, listener, sessionConsumer);
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderInfo authProviderInfo, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData data = this.authProviderDataFactory.getBean();
		data.setAuthProviderInfo(authProviderInfo);
		authenticate(context, consumerKey, consumerSecret, data, listener, sessionConsumer, true);	
	}

	@Override
	public void authenticateKnownUser(Context context, UserProviderCredentials userProviderCredentials, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		AuthProviderData authProviderData = this.authProviderDataFactory.getBean();
		authProviderData.setAuthProviderInfo(userProviderCredentials.getAuthProviderInfo());
		authProviderData.setToken3rdParty(userProviderCredentials.getAccessToken());
		authProviderData.setSecret3rdParty(userProviderCredentials.getTokenSecret());
		authProviderData.setUserId3rdParty(userProviderCredentials.getUserId());
		authenticate(context, consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);	
	}
	
	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderInfo(socializeAuthProviderInfoFactory.getInstanceForRead());
		authenticate(context, consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);	
	}
	

	@Override
	public void authenticate(Context context, AuthProviderType authProviderType, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		AuthProviderInfo authProviderInfo = authProviderInfoBuilder.getFactory(authProviderType).getInstanceForRead();
		authenticate(context, consumerKey, consumerSecret, authProviderInfo, listener, sessionConsumer);
	}
	

	@Override
	public void authenticate(Context ctx, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		if(checkKeys(consumerKey, consumerSecret, listener)) {
			String udid = deviceUtils.getUDID(ctx);
			String advertiserId = deviceUtils.getAndroidID(ctx);
			
			if(StringUtils.isEmpty(udid)) {
				if(listener != null) {
					listener.onError(new SocializeException("No UDID provided"));
				}
			}
			else {
				// All Api instances have authenticate, so we can just use any old one
				authenticateAsync(ctx, consumerKey, consumerSecret, udid, advertiserId, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
			}	
		}
	}

	@Override
	public SocializeSession authenticateSynchronous(Context context) {
		String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);	
		try {
			if(checkKeys(consumerKey, consumerSecret)) {
				return authenticateSynchronous(context, consumerKey, consumerSecret);
			}
			else {
				throw new SocializeException("Consumer key and/or secret not provided");
			}
		}
		catch (Exception e) {
			logError("Error during synchronous authentication", e);
			return null;
		}
	}

	@Override
	public SocializeSession authenticateSynchronous(Context ctx, String consumerKey, String consumerSecret) throws SocializeException {
		String udid = deviceUtils.getUDID(ctx);
		String advertiserId = deviceUtils.getAndroidID(ctx);
		SocializeSession session = authenticate(ctx, "/authenticate/",consumerKey, consumerSecret, udid, advertiserId);
		return session;
	}


	@Override
	public void saveSession(Context context, SocializeSession session) {
		if(sessionPersister != null) {
			sessionPersister.save(context, session);
		}
	}

	// For mocking
	protected SocializeAuthProviderInfo newSocializeAuthProviderInfo() {
		return new SocializeAuthProviderInfo();
	}


	/* (non-Javadoc)
	 * @see com.socialize.api.action.UserSystem#getUser(com.socialize.api.SocializeSession, long, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.UserSystem#saveUserProfile(android.content.Context, com.socialize.api.SocializeSession, com.socialize.ui.profile.UserProfile, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void saveUserSettings(final Context context, final SocializeSession session, final UserSettings settingsToBeSaved, final UserListener listener) {
		User sessionUser = session.getUser();
		UserSettings sessionSettings = session.getUserSettings();

		boolean wasNotificationsEnabled = sessionSettings.isNotificationsEnabled();

		// Update the settings and the user in the session so they are saved
		sessionUser.setFirstName(settingsToBeSaved.getFirstName());
		sessionUser.setLastName(settingsToBeSaved.getLastName());

		sessionSettings.update(settingsToBeSaved);

		boolean isNotificationsEnabled = sessionSettings.isNotificationsEnabled();
		
		if(settingsToBeSaved.getImage() != null) {
			sessionUser.setProfilePicData(bitmapUtils.encode(settingsToBeSaved.getImage()));
		}

		// If notifications was not previously enabled, we may need to register.
		if(isNotificationsEnabled && !wasNotificationsEnabled && notificationRegistrationSystem != null) {
			notificationRegistrationSystem.registerC2DMAsync(context);
		}

		saveUserAsync(context, session, sessionUser, listener);
	}

	@Override
	public void saveUserAsync(final Context context, final SocializeSession session, final User userToBeSaved, final UserListener listener) {

		final UserSettings settings = session.getUserSettings();

		settings.update(userToBeSaved);

		String endpoint = ENDPOINT + userToBeSaved.getId() + "/";

		putAsPostAsync(session, endpoint, userToBeSaved, new UserSaveListener() {

			@Override
			public void onError(SocializeException error) {
				listener.onError(error);
			}

			@Override
			public void onUpdate(User savedUser) {
				handleUserUpdate(context, session, savedUser, settings, listener);
			}
		});
	}

	protected void handleUserUpdate(final Context context, final SocializeSession session, User savedUser, UserSettings userSettings, final UserListener listener) {
		
		try {
			SessionLock.lock();
			
			// Update local in-memory user
			User sessionUser = session.getUser();
			sessionUser.update(savedUser);

			// Save this user to the local session for next load
			if(sessionPersister != null) {
				sessionPersister.saveUser(context, sessionUser, userSettings);
			}
			
			if(listener != null) {
				listener.onUpdate(sessionUser);
			}
		}
		finally {
			SessionLock.unlock();
		}
	}
	
	protected boolean checkKeys(String consumerKey, String consumerSecret) {
		return checkKeys(consumerKey, consumerSecret, null);
	}
	
	protected boolean checkKeys(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
		return  checkKey("consumer key", consumerKey, authListener) &&
				checkKey("consumer secret", consumerSecret, authListener);
	}
	protected boolean checkKey(String name, String key, SocializeAuthListener authListener) {
		if(StringUtils.isEmpty(key)) {
			String msg = "No key specified in authenticate";
			if(authListener != null) {
				authListener.onError(new SocializeException(msg));
			}
			logErrorMessage(msg);
			return false;
		} else {
			return true;	
		}
	}
	protected void logError(String message, Throwable error) {
		if(logger != null) {
			logger.error(message, error);
		}
		else {
			SocializeLogger.e(error.getMessage(), error);
		}
	}
	
	protected void logErrorMessage(String message) {
		if(logger != null) {
			logger.error(message);
		}
		else {
			System.err.println(message);
		}
	}	
	
	public void setSessionPersister(SocializeSessionPersister sessionPersister) {
		this.sessionPersister = sessionPersister;
	}

	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}

	public void setSocializeAuthProviderInfoFactory(SocializeAuthProviderInfoFactory socializeAuthProviderInfoFactory) {
		this.socializeAuthProviderInfoFactory = socializeAuthProviderInfoFactory;
	}
	
	public void setNotificationRegistrationSystem(NotificationRegistrationSystem notificationRegistrationSystem) {
		this.notificationRegistrationSystem = notificationRegistrationSystem;
	}

	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.bitmapUtils = bitmapUtils;
	}
}
