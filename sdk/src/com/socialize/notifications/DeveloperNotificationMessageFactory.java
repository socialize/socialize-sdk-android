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

import com.socialize.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jason Polites
 */
public class DeveloperNotificationMessageFactory extends BaseNotificationMessageFactory {
	
	@Override
	protected void fromJSON(JSONObject from, NotificationMessage to) throws JSONException {
		
		to.setText(getString(from, "message"));
		to.setUrl(getString(from, "url"));
		to.setEntityId(getLongObject(from, "entity_id"));
		
		String notificationType = getString(from, "notification_type");
		
		if(!StringUtils.isEmpty(notificationType)) {
			try {
				to.setNotificationType(NotificationType.valueOf(notificationType.trim().toUpperCase()));
			} catch (Exception e) {
				String msg = "Invalid notification type [" +
						notificationType +
						"]";
				handleError(msg, e);
				to.setNotificationType(NotificationType.DEVELOPER_NOTIFICATION);
			}
		}
	}
}
