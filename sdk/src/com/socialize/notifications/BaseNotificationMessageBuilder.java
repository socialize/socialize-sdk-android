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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchAction;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.util.AppUtils;
import com.socialize.util.DefaultAppUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public abstract class BaseNotificationMessageBuilder<M extends NotificationMessageData> implements NotificationMessageBuilder {

	private MessageTranslator<M> messageTranslator;
	private AppUtils appUtils;
	private SocializeLogger logger;
	
	@Override
	public Notification build(Context context, Bundle messageData, NotificationMessage message, int icon) throws SocializeException {
		
		Notification notification = newNotification(icon, message.getText(), System.currentTimeMillis());
		
		Intent notificationIntent = null;
		
		if(appUtils.isActivityAvailable(context, SocializeLaunchActivity.class)) {
			notificationIntent = newIntent(context, SocializeLaunchActivity.class);
			
			switch (message.getNotificationType()) {
				case NEW_COMMENTS:
					notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.ACTION.name());
					break;
					
				case ENTITY_NOTIFICATION:
					notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.ENTITY.name());
					break;	
					
				case DEVELOPER_NOTIFICATION:
					notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.HOME.name());
					break;
					
				case DEVELOPER_DIRECT_ENTITY:
					notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.ENTITY.name());
					break;
					
				case DEVELOPER_DIRECT_URL:
					notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.URL.name());
					break;
			}

			notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_TASK, "notificationLaunchTask"); // bean name
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		else {
			if(logger != null) {
				logger.warn("Could not locate activity [" +
						SocializeLaunchActivity.class +
						"].  Make sure you have added this to your AndroidManifest.xml");
			}
			notificationIntent = DefaultAppUtils.getMainAppIntent(context);
		}

		// This will add anything we need to the bundle
		M translated = messageTranslator.translate(context, messageData, message);

		// Set the bundle AFTER the translation (messageData is changed)
		notificationIntent.putExtras(messageData);
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		PendingIntent contentIntent = getPendingIntent(context, message, notificationIntent);
		
		RemoteViews notificationView = getNotificationView(context, notification, message, translated);
		
		if(notificationView != null) {
			notification.contentIntent = contentIntent;
			notification.contentView = notificationView;
		}
		else {
			// Just set defaults
			String title = translated.getTitle();
			String text = translated.getText();
			
			if(!StringUtils.isEmpty(title)) {
				if(text == null) text = "";
				notification.setLatestEventInfo(context, title, text, contentIntent);
				notification.tickerText = title;
			}
			else {
				throw new SocializeException("Notification message has empty title");
			}
		}
		
		return notification;
	}
	
	// So we can mock
	protected PendingIntent getPendingIntent(Context context, NotificationMessage message, Intent notificationIntent) {
		return PendingIntent.getActivity(context, getNotificationId(message), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public void setMessageTranslator(MessageTranslator<M> messageTranslator) {
		this.messageTranslator = messageTranslator;
	}
	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	// So we can mock
	protected Notification newNotification(int icon, String text, long time) {
		return new Notification(icon, text, time);
	}
	
	// So we can mock
	protected Intent newIntent(Context context, Class<?> cls) {
		 return new Intent(context, cls);
	}
	
	protected int getNotificationId(NotificationMessage message) {
		Long entityId = message.getEntityId();
		if(entityId != null) {
			return entityId.intValue();
		}
		return 0;
	}

	/**
	 * Returns the remote views object for this notification (if available).
	 * @param context
	 * @param notification
	 * @param message
	 * @param data
	 * @return
	 */
	public abstract RemoteViews getNotificationView(Context context, Notification notification, NotificationMessage message, M data);
}
