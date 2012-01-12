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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchAction;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.util.DefaultAppUtils;
import com.socialize.util.AppUtils;

/**
 * @author Jason Polites
 */
public abstract class BaseNotificationMessageBuilder<T extends SocializeAction, M extends NotificationMessageData> implements NotificationMessageBuilder {

	private MessageTranslator<M> messageTranslator;
	private AppUtils appUtils;
	private SocializeLogger logger;
	
	@Override
	public Notification build(Context context, Bundle bundle, NotificationMessage message, int icon) throws SocializeException {
		
		Notification notification = newNotification(icon, message.getText(), System.currentTimeMillis());
		
		Intent notificationIntent = null;
		
		if(appUtils.isActivityAvailable(context, SocializeLaunchActivity.class)) {
			notificationIntent = newIntent(context, SocializeLaunchActivity.class);
			notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.ACTION.name());
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
		// TODO This should not load the entity, just needs to render the message received.
		M translated = messageTranslator.translate(context, bundle, message);

		// Set the bundle AFTER the translation
		notificationIntent.putExtras(bundle);
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		RemoteViews notificationView = getNotificationView(context, notification, message, translated);
		
		if(notificationView != null) {
			notification.contentIntent = contentIntent;
			notification.contentView = notificationView;
		}
		else {
			// Just set defaults
			notification.setLatestEventInfo(context, translated.getTitle(), translated.getText(), contentIntent);
			notification.tickerText = translated.getTitle();
		}
		
		return notification;			
	}

	public void setMessageTranslator(MessageTranslator<M> messageTranslator) {
		this.messageTranslator = messageTranslator;
	}
	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
	
	// So we can mock
	protected Notification newNotification(int icon, String text, long time) {
		return new Notification(icon, text, time);
	}
	
	// So we can mock
	protected Intent newIntent(Context context, Class<?> cls) {
		 return new Intent(context, cls);
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
