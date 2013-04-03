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
package com.socialize.launcher.task;

import android.content.Context;
import android.os.Bundle;
import com.socialize.api.SocializeSession;
import com.socialize.api.event.EventListener;
import com.socialize.api.event.EventSystem;
import com.socialize.api.event.SocializeEvent;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchTask;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.NotificationAuthenticator;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public class NotificationOpenEventTask implements LaunchTask {
	
	private EventSystem eventSystem;
	private SocializeLogger logger;
	private NotificationAuthenticator notificationAuthenticator;

	/* (non-Javadoc)
	 * @see com.socialize.launcher.LaunchTask#execute(android.content.Context, android.os.Bundle)
	 */
	@Override
	public void execute(Context context, Bundle extras) throws SocializeException {

		try {
			
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Recording notification open event...");
			}
			
			final SocializeEvent event = new SocializeEvent();
			event.setBucket("NOTIFICATION_OPEN");
			String json = extras.getString(C2DMCallback.MESSAGE_KEY);
			JSONObject obj = new JSONObject(json);
			event.setData(obj);
			
			notificationAuthenticator.authenticateAsync(context, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					logError(error);
				}
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					eventSystem.addEvent(session, event, new EventListener() {
						
						@Override
						public void onError(SocializeException error) {
							logError(error);
						}

						@Override
						public void onPost() {
							if(logger != null && logger.isDebugEnabled()) {
								logger.debug("Notification open event recorded.");
							}							
						}
					});
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					logError(error);
				}
			});
			
			
		}
		catch (Exception e) {
			throw SocializeException.wrap(e);
		}
	}
	
	protected void logError(Exception e) {
		if(logger != null) {
			logger.error("Error recording notification open event", e);
		}
		else {
			SocializeLogger.e(e.getMessage(), e);
		}
	}
	
	public void setEventSystem(EventSystem eventSystem) {
		this.eventSystem = eventSystem;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setNotificationAuthenticator(NotificationAuthenticator notificationAuthenticator) {
		this.notificationAuthenticator = notificationAuthenticator;
	}
}
