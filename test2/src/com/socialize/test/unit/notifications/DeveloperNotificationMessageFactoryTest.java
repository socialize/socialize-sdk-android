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
package com.socialize.test.unit.notifications;

import com.socialize.notifications.DeveloperNotificationMessageFactory;
import com.socialize.notifications.NotificationMessage;
import com.socialize.notifications.NotificationType;
import com.socialize.test.SocializeUnitTest;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public class DeveloperNotificationMessageFactoryTest extends SocializeUnitTest {

	public void testFromJSON() throws JSONException {
		
		JSONObject from = new JSONObject();
		
		final String message = "foo";
		final String notification_type = NotificationType.DEVELOPER_NOTIFICATION.name();
		
		from.put("message", message);
		from.put("notification_type", notification_type);
		
		DeveloperNotificationMessageFactory factory = new DeveloperNotificationMessageFactory();
		
		NotificationMessage to = factory.fromJSON(from);
		
		assertNotNull(to);
		assertNotNull(to.getText());
		assertNotNull(to.getNotificationType());
		
		assertEquals(message, to.getText());
		assertEquals(NotificationType.DEVELOPER_NOTIFICATION, to.getNotificationType());
	}
}
