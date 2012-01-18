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

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.socialize.entity.JSONFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeC2DMCallback implements C2DMCallback {
	
	private int notificationIcon = R.drawable.sym_action_chat;
	
	public static final int NOTIFICATION_ID = 1337;
	
	private SocializeLogger logger;

	private Map<String, JSONFactory<NotificationMessage>> messageFactories;
	private Map<String, NotificationMessageBuilder> messageBuilders;
	private NotificationRegistrationState notificationRegistrationState;

	@Override
	public void onRegister(Context context, String registrationId)  {
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("Registration with C2DM successful: " + registrationId);
			
			if(logger.isDebugEnabled()) {
				logger.debug("C2DM ID [" +
						registrationId +
						"]");
			}
		}
		notificationRegistrationState.setC2DMRegistrationId(registrationId);
		notificationRegistrationState.save(context);
	}
	
	@Override
	public void onError(Context context, String errorId) {
		if(logger != null && logger.isWarnEnabled()) {
			logger.warn("Registration with C2DM failed: " + errorId);
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
	public void onMessage(Context context, Bundle data) {
		
		String json = data.getString(MESSAGE_KEY);
		
		if(!StringUtils.isEmpty(json)) {
			
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Received notification [" +
						json +
						"]");
			}
			
			try {
				JSONObject message = new JSONObject(json);
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
								Notification notification = builder.build(context, data, notificationMessage, notificationIcon);
								NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
								mNotificationManager.notify(String.valueOf(notificationMessage.getActionId()), NOTIFICATION_ID, notification);		
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
			System.err.println(msg);
			e.printStackTrace();
		}	
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
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
}
