/*
 * Copyright (c) 2011 Socialize Inc.
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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.DeviceRegistrationSystem;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.DeviceRegistration;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeNotificationRegistrationSystem implements NotificationRegistrationSystem {
	
	public static final String EXTRA_SENDER = "sender";
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";
	
	private SocializeConfig config;
	private SocializeLogger logger;
	private DeviceRegistrationSystem deviceRegistrationSystem;
	private IBeanFactory<DeviceRegistration> deviceRegistrationFactory;
	private NotificationRegistrationState notificationRegistrationState;
	
	@Override
	public boolean isRegistrationPending() {
		return notificationRegistrationState.isC2dmPending();
	}

	@Override
	public boolean isRegisteredC2DM() {
		return notificationRegistrationState.isRegisteredC2DM();
	}
	
	@Override
	public boolean isSocializeRegistrationPending() {
		return notificationRegistrationState.isSocializeRegistrationPending();
	}

	@Override
	public boolean isRegisteredSocialize(User user) {
		return notificationRegistrationState.isRegisteredSocialize(user);
	}
	
	@Override
	public void registerC2DMFailed(Context context, String cause) {
		notificationRegistrationState.setC2dmPendingRequestTime(0);
		notificationRegistrationState.save(context);
	}

	@Override
	public synchronized void registerC2DM(final Context context) {
		if(!isRegisteredC2DM() && !notificationRegistrationState.isC2dmPending()) {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Registering with C2DM");
			}
			
			notificationRegistrationState.setC2dmPendingRequestTime(System.currentTimeMillis());
			notificationRegistrationState.save(context);
			
			String senderId = config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID);
			Intent registrationIntent = newIntent(REQUEST_REGISTRATION_INTENT);
			registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, newPendingIntent(context));
			registrationIntent.putExtra(EXTRA_SENDER, senderId);
			context.startService(registrationIntent);
		}	
		else {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("C2DM registration already in progress or complete");
			}
		}
	}
	
	@Deprecated
	@Override
	public void registerSocialize(Context context, SocializeSession session, String registrationId) {
		registerSocialize(context, registrationId);
	}

	@Override
	public void registerSocialize(final Context context, final String registrationId) {
		
		notificationRegistrationState.setPendingSocializeRequestTime(System.currentTimeMillis());
		notificationRegistrationState.save(context);
		
		if(!StringUtils.isEmpty(registrationId)) {
			
			Socialize.getSocialize().authenticate(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					logError(error);
				}
				
				@Override
				public void onCancel() {
					if(logger != null) {
						logger.error("Authentication was cancelled during notification registration.  This should not happen!");
					}
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					doRegistrationSocialize(context, session, registrationId);			
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					logError(error);
				}
			});
		}
	}
	
	protected void doRegistrationSocialize(Context context, SocializeSession session, String registrationId) {
		try {
			// Record the registration with Socialize
			DeviceRegistration registration = deviceRegistrationFactory.getBean();
			registration.setRegistrationId(registrationId);
			
			deviceRegistrationSystem.registerDevice(session, registration);
			
			notificationRegistrationState.setC2DMRegistrationId(registrationId);
			notificationRegistrationState.setRegisteredSocialize(session.getUser());
			notificationRegistrationState.save(context);
			
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("Registration with Socialize for C2DM successful.");
			}
		} 
		catch (SocializeException e) {
			logError(e);
		}
	}
	
	protected void logError(Exception e) {
		if(logger != null) {
			logger.error("Error during device registration", e);
		}
		else {
			e.printStackTrace();
		}
	}
	
	// So we can mock
	protected Intent newIntent(String action) {
		return new Intent(action);
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
}
