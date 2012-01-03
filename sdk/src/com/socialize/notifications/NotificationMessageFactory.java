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

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.api.action.ActionType;
import com.socialize.entity.JSONFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class NotificationMessageFactory extends JSONFactory<NotificationMessage> {
	
	private SocializeLogger logger;

	@Override
	public Object instantiateObject(JSONObject object) {
		return new NotificationMessage();
	}

	@Override
	protected void fromJSON(JSONObject from, NotificationMessage to) throws JSONException {
		
		to.setText(getString(from, "text"));
		to.setActionId(getLong(from, "activity_id"));
		
		String actionType = getString(from, "activity_type");
		String notificationType = getString(from, "notification_type");
		
		if(!StringUtils.isEmpty(actionType)) {
			try {
				to.setActionType(ActionType.valueOf(actionType.trim().toUpperCase()));
			} catch (Exception e) {
				String msg = "Invalid action type [" +
						actionType +
						"]";
				handleError(msg, e);
			}
		}
		
		if(!StringUtils.isEmpty(notificationType)) {
			try {
				to.setNotificationType(NotificationType.valueOf(notificationType.trim().toUpperCase()));
			} catch (Exception e) {
				String msg = "Invalid notification type [" +
						notificationType +
						"]";
				handleError(msg, e);
			}
		}
	}

	@Override
	protected void toJSON(NotificationMessage from, JSONObject to) throws JSONException {
		// Never done.
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
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
}