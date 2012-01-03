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

import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchAction;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.util.AppUtils;

/**
 * @author Jason Polites
 */
public abstract class BaseNotificationMessageBuilder<T> implements NotificationMessageBuilder {

	private MessageTranslator<T> messageTranslator;
	private AppUtils appUtils;
	
	@Override
	public Notification build(Context context, Bundle bundle, NotificationMessage message, int icon) throws SocializeException {
		
		if(appUtils != null && appUtils.isActivityAvailable(context, SocializeLaunchActivity.class)) {

			Notification notification = newNotification(icon, message.getText(), System.currentTimeMillis());
			
			Intent notificationIntent = newIntent(context, SocializeLaunchActivity.class);
			
			// This will add anything we need to the bundle
			T data = messageTranslator.translate(context, bundle, message);
			
			notificationIntent.putExtra(SocializeLaunchActivity.LAUNCH_ACTION, LaunchAction.ACTION.name());
			notificationIntent.putExtras(bundle);
			
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, notification.flags |= Notification.FLAG_AUTO_CANCEL);
			
			RemoteViews notificationView = getNotificationView(context, notification, message, data);
			
			if(notificationView != null) {
				notification.contentIntent = contentIntent;
				notification.contentView = notificationView;
			}
			else {
				// Just set defaults
				notification.setLatestEventInfo(context, message.getTitle(), message.getText(), contentIntent);
				notification.tickerText = message.getTitle();
			}
			
			return notification;
		}
		else {
			throw new SocializeException("Unable to locate activity [" +
					SocializeLaunchActivity.class.getName() +
					"].  Make sure this is added to your AndroidManifest.xml");
		}
	}

	public void setMessageTranslator(MessageTranslator<T> messageTranslator) {
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
	public abstract RemoteViews getNotificationView(Context context, Notification notification, NotificationMessage message, T data);
}
