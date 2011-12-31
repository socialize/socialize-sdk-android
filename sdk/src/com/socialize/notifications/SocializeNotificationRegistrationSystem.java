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

import com.socialize.api.DeviceRegistrationSystem;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.DeviceRegistration;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.util.AppUtils;
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
	private NotificationAuthenticator notificationAuthenticator;
	private NotificationRegistrationState notificationRegistrationState;
	private AppUtils appUtils;
	
	@Override
	public boolean isRegisteredC2DM() {
		return !StringUtils.isEmpty(notificationRegistrationState.getC2DMRegistrationId());
	}

	@Override
	public boolean isRegisteredSocialize() {
		return notificationRegistrationState.isRegisteredSocialize();
	}

	@Override
	public void registerC2DM(final Context context) {
		if(!isRegisteredC2DM()) {
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("Registering with C2DM");
			}
			
			String senderId = config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID);
			Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
			
//			if(appUtils.isIntentAvailable(context, registrationIntent)) {
				registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
				registrationIntent.putExtra(EXTRA_SENDER, senderId);
				context.startService(registrationIntent);
//			}
//			else {
//				if(logger != null) {
//					logger.warn("Intent [" +
//							REQUEST_REGISTRATION_INTENT +
//							"] is not available.  Make sure the notification system is correctly configured in your AndroidManifest.xml");
//				}
//			}
		}		
	}

	@Override
	public void registerSocialize(Context context, String registrationId) {
		
		String currentValue = notificationRegistrationState.getC2DMRegistrationId();
		
		if(currentValue == null || !currentValue.equals(registrationId) || !isRegisteredSocialize()) {
			
			notificationRegistrationState.setC2DMRegistrationId(registrationId);
			
			try {
				// Record the registration with Socialize
				DeviceRegistration registration = new DeviceRegistration();
				registration.setRegistrationId(registrationId);
				SocializeSession session = notificationAuthenticator.authenticate(context);
				deviceRegistrationSystem.registerDevice(session, registration);
				notificationRegistrationState.setRegisteredSocialize(true);
				
				if(logger != null && logger.isInfoEnabled()) {
					logger.info("Registration with Socialize for C2DM successful.");
				}
			} 
			catch (SocializeException e) {
				if(logger != null) {
					logger.error("Error during device registration", e);
				}
				else {
					e.printStackTrace();
				}
			}
			finally {
				notificationRegistrationState.save(context);
			}
		}
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

	public void setNotificationAuthenticator(NotificationAuthenticator notificationAuthenticator) {
		this.notificationAuthenticator = notificationAuthenticator;
	}

	public void setNotificationRegistrationState(NotificationRegistrationState notificationRegistrationState) {
		this.notificationRegistrationState = notificationRegistrationState;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
}
