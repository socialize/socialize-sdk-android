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
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActivitySystem;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;

/**
 * @author Jason Polites
 *
 */
public class SocializeActionMessageTranslator implements MessageTranslator<SocializeAction> {
	
	private ActivitySystem activitySystem;
	private NotificationAuthenticator notificationAuthenticator;

	/*
	 * (non-Javadoc)
	 * @see com.socialize.notifications.MessageTranslator#translateTo(android.content.Context, android.os.Bundle, com.socialize.notifications.NotificationMessage)
	 */
	@Override
	public SocializeAction translate(Context context, Bundle data, NotificationMessage message) throws SocializeException {
		// We need Socialize.USER_ID, Socialize.ACTION_ID
		// Socialize.ACTION_ID is already in the bundle.
		String actionId = String.valueOf(message.getActionId());
		
		SocializeSession session = notificationAuthenticator.authenticate(context);
		SocializeAction action = activitySystem.getAction(session, message.getActionId(), message.getActionType());
		
		if(action != null) {
			data.putString( Socialize.ACTION_ID , actionId);
			
			User user = action.getUser();
			
			if(user != null) {
				data.putString( Socialize.USER_ID , String.valueOf(user.getId()));
			}
			else {
				data.putString( Socialize.USER_ID , "-1");
			}
		}
		else {
			throw new SocializeException("No action object found of type [" +
					message.getActionType() +
					"] with id [" +
					actionId +
					"]");
		}
		return action;
	}

	public void setActivitySystem(ActivitySystem activitySystem) {
		this.activitySystem = activitySystem;
	}

	public void setNotificationAuthenticator(NotificationAuthenticator notificationAuthenticator) {
		this.notificationAuthenticator = notificationAuthenticator;
	}
}
