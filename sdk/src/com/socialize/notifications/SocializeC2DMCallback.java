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

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.user.UserSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.JSONFactory;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.AppUtils;
import com.socialize.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Jason Polites
 */
public class SocializeC2DMCallback implements C2DMCallback {
	
	private int notificationIcon = android.R.drawable.sym_action_chat;
	
	private SocializeLogger logger;
	private SocializeConfig config;
	private AppUtils appUtils;
	private UserSystem userSystem;
	
	private Map<String, JSONFactory<NotificationMessage>> messageFactories;
	private Map<String, NotificationMessageBuilder> messageBuilders;
	private NotificationRegistrationState notificationRegistrationState;
	private NotificationRegistrationSystem notificationRegistrationSystem;
	private NotificationManagerFacade notificationManagerFacade;

	@Override
	public void onRegister(Context context, String registrationId)  {
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("Registration with GCM successful: " + registrationId);
			
			if(logger.isDebugEnabled()) {
				logger.debug("GCM ID [" +
						registrationId +
						"]");
			}
		}
		notificationRegistrationState.setC2DMRegistrationId(registrationId);
		notificationRegistrationState.save(context);
		
		// Trigger socialize registration
		notificationRegistrationSystem.registerSocialize(context, registrationId);
	}
	
	@Override
	public void onError(Context context, String errorId) {
		if(logger != null && logger.isWarnEnabled()) {
			logger.warn("Registration with GCM failed: " + errorId);
		}
	}

	@Override
	public void onUnregister(Context context) {
		// Do nothing
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.NotificationSystem#sendNotification(android.os.Bundle)
	 */
	@Override
	public void onMessage(final Context context, final Bundle data) {
		// DON'T Use Socialize instance here, we are using a different container!
		try {
			// This should be synchronous.  We don't want to launch an async task off the main UI thread.
			SocializeSession session = userSystem.authenticateSynchronous(context, config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY), config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET));
			handleNotification(context, data, session);
		}
		catch (SocializeException e) {
			handleError("Failed to authenticate user for notification receipt", e);
		}
	}
	
	protected void handleNotification(final Context context, final Bundle data, final SocializeSession session) {
		
		final UserSettings settings = session.getUserSettings();
		final User user = session.getUser();
		
		if(settings != null && settings.isNotificationsEnabled()) {

			String json = data.getString(MESSAGE_KEY);
			
			if(!StringUtils.isEmpty(json)) {
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Received notification [" +
							json +
							"]");
				}
				
				try {
					JSONObject message = newJSONObject(json);
					String notification_type = "notification_type";
					
					if(message.has(notification_type) && !message.isNull(notification_type)) {
						
						String type = message.getString(notification_type).trim().toUpperCase();
						
						NotificationType notificationType = null;
						
						try {
							notificationType = NotificationType.valueOf(type);
						} 
						catch (Exception e) {
							handleError("Invalid notification_type [" +
									type +
									"] defined in notification message [" +
									json +
									"]", e);
						}		
						
						if(notificationType != null) {
							
							JSONFactory<NotificationMessage> factory = messageFactories.get(notificationType.name());
							
							if(factory != null) {
								NotificationMessage notificationMessage = factory.fromJSON(message);
								NotificationMessageBuilder builder = messageBuilders.get(notificationType.name());
								
								if(builder != null) {
									int icon = getNotificationIcon(context);
									Notification notification = builder.build(context, data, notificationMessage, icon);
									doNotify(context, getNotificationTag(notificationMessage),  getNotificationId(notificationMessage), notification);
								}
								else {
									handleError("No message builder defined for notification type [" +
											notificationType +
											"]");
								}	
							}
							else {
								handleError("No message factory defined for notification type [" +
										notificationType +
										"]");
								
							}
						}
					}
					else {
						handleError("No notification_type defined in notification message [" +
								json +
								"]");
					}
				} 
				catch (JSONException e) {
					handleError("Notification system received an invalid JSON message [" +
							json +
							"]", e);
				} 
				catch (Exception e) {
					handleError("Error building notification message", e);
				}
			}
			else {
				handleError("No data found in message bundle under key [" +
						MESSAGE_KEY +
						"]");
			}	
		}
		else {
			if(settings == null) {
				handleError("No user settings found in session!");
			}
			else {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Notification for user [" +
							user.getId() +
							"] ignored.  Notifications are disabled for this user");
				}
			}
		}
	}
	
	protected void doNotify(Context context, String tag, int id, Notification notification) {
		notificationManagerFacade.notify(context, tag, id, notification);
	}
	
	protected String getNotificationTag(NotificationMessage message) {
		if(message.getEntityId() != null) {
			return String.valueOf(message.getEntityId().longValue());
		}
		else if(message.getUrl() != null) {
			return message.getUrl();
		}
		else {
			return String.valueOf(message.getActionId());
		}
	}
	
	protected int getNotificationId(NotificationMessage message) {
		if(message.getEntityId() != null) {
			return message.getEntityId().intValue();
		}
		return 0;
	}
	
	protected JSONObject newJSONObject(String json) throws JSONException {
		return new JSONObject(json);
	}

	protected int getNotificationIcon(Context context) {
		int icon = notificationIcon;
		
		if(config.getBooleanProperty(SocializeConfig.SOCIALIZE_NOTIFICATION_APP_ICON, true)) {
			icon = appUtils.getAppIconId(context);
			
			if(icon <= 0) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Could not locate ID for application icon.  Using default icon for notification");
					icon = notificationIcon;
				}
			}
		}
		
		return icon;
	}
	
	protected void handleError(String msg) {
		if(logger != null) {
			logger.error(msg);
		}
		else {
			System.err.println(msg);
		}	
	}
	
	protected void handleError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			SocializeLogger.e(msg, e);
		}	
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

	public int getNotificationIcon() {
		return notificationIcon;
	}

	public void setNotificationIcon(int notificationIcon) {
		this.notificationIcon = notificationIcon;
	}
	
	public void setMessageFactories(Map<String, JSONFactory<NotificationMessage>> messageFactories) {
		this.messageFactories = messageFactories;
	}

	public void setMessageBuilders(Map<String, NotificationMessageBuilder> messageBuilders) {
		this.messageBuilders = messageBuilders;
	}

	public void setNotificationRegistrationState(NotificationRegistrationState notificationRegistrationState) {
		this.notificationRegistrationState = notificationRegistrationState;
	}

	public void setNotificationRegistrationSystem(NotificationRegistrationSystem notificationRegistrationSystem) {
		this.notificationRegistrationSystem = notificationRegistrationSystem;
	}

	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
	
	public void setNotificationManagerFacade(NotificationManagerFacade notificationManagerFacade) {
		this.notificationManagerFacade = notificationManagerFacade;
	}
	
}
