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

import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.log.SocializeLogger;
import com.socialize.util.AppUtils;

/**
 * Checks for notification registrations.
 * @author Jason Polites
 *
 */
public class NotificationChecker {

	private NotificationRegistrationSystem notificationRegistrationSystem;
	private NotificationRegistrationState notificationRegistrationState;
	private SocializeLogger logger;
	private SocializeConfig config;
	private AppUtils appUtils;
	
	/**
	 * Called at application startup.
	 * @param context
	 */
	public void checkRegistrations(Context context, SocializeSession session) {
		
		if(appUtils.isNotificationsAvaiable(context)) {

			if(config.getBooleanProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, true)) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Checking C2DM registration state");
				}
				
				if(!notificationRegistrationSystem.isRegisteredC2DM() || !notificationRegistrationSystem.isRegisteredSocialize(session.getUser())) {
					
					// Reload
					notificationRegistrationState.load(context);
					
					if(!notificationRegistrationSystem.isRegisteredC2DM()) {
						
						if(notificationRegistrationSystem.isRegistrationPending()) {
							if(logger != null && logger.isDebugEnabled()) {
								logger.debug("C2DM already registration pending.");
							}
						}
						else {
							if(logger != null && logger.isInfoEnabled()) {
								logger.info("Not registered with C2DM, sending registration request...");
							}
							
							notificationRegistrationSystem.registerC2DM(context);
						}
					}
					else if(!notificationRegistrationSystem.isRegisteredSocialize(session.getUser())) {
						
						if(logger != null && logger.isInfoEnabled()) {
							logger.info("Not registered with Socialize for C2DM, registering...");
						}
						
						notificationRegistrationSystem.registerSocialize(context, session, notificationRegistrationState.getC2DMRegistrationId());
					}				
				}
				else {
					if(logger != null && logger.isDebugEnabled()) {
						logger.debug("C2DM registration OK");
					}
				}
			}
			else {
				if(logger != null && logger.isWarnEnabled()) {
					logger.warn("C2DM registration check skipped");
				}			
			}
		}
		else {
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("Notifications not enabled.  Check the AndroidManifest.xml for correct configuration.");
			}	
		}
		
	}

	public void setNotificationRegistrationSystem(NotificationRegistrationSystem notificationRegistrationSystem) {
		this.notificationRegistrationSystem = notificationRegistrationSystem;
	}

	public void setNotificationRegistrationState(NotificationRegistrationState notificationRegistrationState) {
		this.notificationRegistrationState = notificationRegistrationState;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
	
	
}
