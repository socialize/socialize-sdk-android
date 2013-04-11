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

import android.content.Context;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.AppUtils;
import com.socialize.util.StringUtils;

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
	private boolean checked = false;
	private boolean checking = false;
	
	public void checkRegistrations(final Context context) {
		if(!checked) {
			Socialize.getSocialize().authenticate(context, new SocializeAuthListener() {
				@Override
				public void onError(SocializeException error) {
					logError(error);
				}
				
				@Override
				public void onCancel() {
					// Ignore
				}
				
				@Override
				public void onAuthSuccess(final SocializeSession session) {
					new Thread() {
						@Override
						public void run() {
							checked = checkRegistrations(context, session);
						}
					}.start();
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					logError(error);
				}
			});
		}
	}
	
	protected void logError(Exception e) {
		if(logger != null) {
			logger.error("Error checking notification state", e);
		}
		else {
			SocializeLogger.e(e.getMessage(), e);
		}
	}
	
	/**
	 * Called at application startup.
	 * @param context
	 */
	public boolean checkRegistrations(Context context, SocializeSession session) {
		
		boolean checked = false;

		if(!checking) {
			checking = true;

			try {

				if(appUtils.isNotificationsAvailable(context)) {

					if(config.getBooleanProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, true)) {
						if(logger != null && logger.isDebugEnabled()) {
							logger.debug("Checking GCM registration state");
						}

						boolean c2DMRegistered = notificationRegistrationSystem.isRegisteredC2DM(context);
						boolean socRegistered = notificationRegistrationSystem.isRegisteredSocialize(context, session.getUser());

						if(!c2DMRegistered || !socRegistered) {

							// Reload
							notificationRegistrationState.reload(context);

							c2DMRegistered = notificationRegistrationSystem.isRegisteredC2DM(context);
							socRegistered = notificationRegistrationSystem.isRegisteredSocialize(context, session.getUser());

							if(!c2DMRegistered || !socRegistered) {
								if(!c2DMRegistered && config.getBooleanProperty(SocializeConfig.GCM_REGISTRATION_ENABLED, true)) {

									if(notificationRegistrationSystem.isRegistrationPending()) {
										if(logger != null && logger.isDebugEnabled()) {
											logger.debug("GCM Registration already pending");
										}
									}
									else {
										if(logger != null && logger.isInfoEnabled()) {
											logger.info("Not registered with GCM, sending registration request...");
										}

										notificationRegistrationSystem.registerC2DMAsync(context);
									}
								}
								else if(!socRegistered && !StringUtils.isEmpty(notificationRegistrationState.getC2DMRegistrationId())) {

									if(notificationRegistrationSystem.isSocializeRegistrationPending()) {
										if(logger != null && logger.isDebugEnabled()) {
											logger.debug("Registration already pending with Socialize for GCM");
										}
									}
									else {
										if(logger != null && logger.isInfoEnabled()) {
											logger.info("Not registered with Socialize for GCM, registering...");
										}

										notificationRegistrationSystem.registerSocialize(context, notificationRegistrationState.getC2DMRegistrationId());
									}
								}
							}
							else {
								if(logger != null && logger.isDebugEnabled()) {
									logger.debug("GCM registration OK");
								}
							}
						}
						else {
							if(logger != null && logger.isDebugEnabled()) {
								logger.debug("GCM registration OK");
							}
						}

						checked = true;
					}
					else {
						if(logger != null && logger.isWarnEnabled()) {
							logger.warn("GCM registration check skipped");
						}
					}
				}
				else {
					if(logger != null && logger.isInfoEnabled()) {
						logger.info("Notifications not enabled.  Check the AndroidManifest.xml for correct configuration.");
					}

					checked = true;
				}
			}
			finally {
				checking = false;
			}
		}

		return checked;
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
