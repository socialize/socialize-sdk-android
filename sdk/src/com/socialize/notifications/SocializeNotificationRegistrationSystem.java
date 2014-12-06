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
package com.socialize.notifications;

import java.util.List;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.Logger;
import com.socialize.api.DeviceRegistrationSystem;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.user.UserSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.DeviceRegistration;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeNotificationRegistrationSystem implements NotificationRegistrationSystem {
	
	public static final String EXTRA_SENDER = "sender";
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	
	private SocializeConfig config;
	private SocializeLogger logger;
	private DeviceRegistrationSystem deviceRegistrationSystem;
	private UserSystem userSystem;
	private IBeanFactory<DeviceRegistration> deviceRegistrationFactory;
	private NotificationRegistrationState notificationRegistrationState;
	
	@Override
	public boolean isRegistrationPending() {
		return notificationRegistrationState.isC2dmPending();
	}

	@Override
	public boolean isRegisteredC2DM(Context context) {
		return notificationRegistrationState.isRegisteredC2DM(context);
	}
	
	@Override
	public boolean isSocializeRegistrationPending() {
		return notificationRegistrationState.isSocializeRegistrationPending();
	}

	@Override
	public boolean isRegisteredSocialize(Context context, User user) {
		return notificationRegistrationState.isRegisteredSocialize(context, user);
	}
	
	@Override
	public void registerC2DMFailed(Context context, String cause) {
		notificationRegistrationState.setC2dmPendingRequestTime(0);
	}

	@Override
	public void registerC2DMAsync(final Context context) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					registerC2DM(context);
				}
				catch (Exception e) {
					if(logger != null) {
						logger.error("Error during smart alert registration", e);
					}
					else {
						Log.e(Logger.LOG_KEY, "Error during smart alert registration", e);
					}
				}

				return null;
			}
		}.execute();
	}

	@Override
	public synchronized void registerC2DM(final Context context) {
		if(!isRegisteredC2DM(context) && !notificationRegistrationState.isC2dmPending() && config.getBooleanProperty(SocializeConfig.GCM_REGISTRATION_ENABLED, true)) {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Registering with GCM");
			}
			
			notificationRegistrationState.setC2dmPendingRequestTime(System.currentTimeMillis());
			
			@SuppressWarnings("deprecation")
			String senderId = config.getProperty(SocializeConfig.SOCIALIZE_GCM_SENDER_ID, config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID));
			String customSender = config.getProperty(SocializeConfig.SOCIALIZE_CUSTOM_GCM_SENDER_ID);
			
			// Only supported in GCM
			if(!StringUtils.isEmpty(senderId) && !StringUtils.isEmpty(customSender) && !senderId.equals(customSender)) {
				senderId = senderId + "," + customSender;
			}
			
			Intent implicitIntent = newIntent(REQUEST_REGISTRATION_INTENT);
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);
			if (resolveInfos != null
					&& resolveInfos.size() == 1) {
				ResolveInfo serviceInfo = resolveInfos.get(0);
				String packageName = serviceInfo.serviceInfo.packageName;
				String className = serviceInfo.serviceInfo.name;
				ComponentName component = new ComponentName(packageName, className);
				
				Intent registrationIntent = newIntent(implicitIntent);
				registrationIntent.setComponent(component);
				registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, newPendingIntent(context));
				registrationIntent.putExtra(EXTRA_SENDER, senderId);
				context.startService(registrationIntent);
			}
		}	
		else {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("GCM registration already in progress, complete or disabled");
			}
		}
	}
	
	@Override
	public void registerSocialize(final Context context, final String registrationId) {
		
		notificationRegistrationState.setPendingSocializeRequestTime(System.currentTimeMillis());
		notificationRegistrationState.save(context);
		
		if(!StringUtils.isEmpty(registrationId)) {
			try {
				// This should be synchronous.  We don't want to launch an async task off the main UI thread.
				SocializeSession session = userSystem.authenticateSynchronous(context, config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY), config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET));
				doRegistrationSocialize(context, session, registrationId);			
			}
			catch (SocializeException e) {
				logError(e);
			}	
		}
	}
	
	protected void doRegistrationSocialize(final Context context, final SocializeSession session, final String registrationId) {
		
		// Record the registration with Socialize
		DeviceRegistration registration = deviceRegistrationFactory.getBean();
		registration.setRegistrationId(registrationId);
		
		
		try {
			deviceRegistrationSystem.registerDeviceSynchronous(session, registration);
			
			notificationRegistrationState.setC2DMRegistrationId(registrationId);
			notificationRegistrationState.setRegisteredSocialize(session.getUser());
			notificationRegistrationState.save(context);
			
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("Registration with Socialize for GCM successful.");
			}			
		}
		catch (SocializeException e) {
			if(logger != null) {
				logger.error("Error registering device with Socialize.  Will retry on next start", e);
			}
		}
	}
	
	protected void logError(Exception e) {
		if(logger != null) {
			logger.error("Error during device registration", e);
		}
		else {
			SocializeLogger.e(e.getMessage(), e);
		}
	}
	
	// So we can mock
	protected Intent newIntent(String action) {
		return new Intent(action);
	}
	
	protected Intent newIntent(Intent intent) {
		return new Intent(intent);
	}
	
	// So we can mock
	protected PendingIntent newPendingIntent(Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(), 0);
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setDeviceRegistrationSystem(DeviceRegistrationSystem deviceRegistrationSystem) {
		this.deviceRegistrationSystem = deviceRegistrationSystem;
	}

	public void setNotificationRegistrationState(NotificationRegistrationState notificationRegistrationState) {
		this.notificationRegistrationState = notificationRegistrationState;
	}

	public void setDeviceRegistrationFactory(IBeanFactory<DeviceRegistration> deviceRegistrationFactory) {
		this.deviceRegistrationFactory = deviceRegistrationFactory;
	}
	
	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
}
