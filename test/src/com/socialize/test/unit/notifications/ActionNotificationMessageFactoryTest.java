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

import com.socialize.api.action.ActionType;
import com.socialize.notifications.ActionNotificationMessageFactory;
import com.socialize.notifications.NotificationMessage;
import com.socialize.notifications.NotificationType;
import com.socialize.test.SocializeUnitTest;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public class ActionNotificationMessageFactoryTest extends SocializeUnitTest {

	public void testFromJSON() throws JSONException {
		
		JSONObject from = new JSONObject();
		
		final String text = "foo";
		final String user = "foo_user";
		final String entity = "foo_entity";
		final long activity_id = 69L;
		final Long entity_id = 3142L;
		final String activity_type = ActionType.COMMENT.name();
		final String notification_type = NotificationType.DEVELOPER_NOTIFICATION.name();
		
		from.put("text", text);
		from.put("user", user);
		from.put("entity", entity);
		from.put("activity_id", activity_id);
		from.put("entity_id", entity_id);
		from.put("activity_type", activity_type);
		from.put("notification_type", notification_type);
		
		ActionNotificationMessageFactory factory = new ActionNotificationMessageFactory();
		
		NotificationMessage to = factory.fromJSON(from);
		
		assertNotNull(to);
		assertNotNull(to.getText());
		assertNotNull(to.getUser());
		assertNotNull(to.getEntity());
		assertNotNull(to.getEntityId());
		assertNotNull(to.getActionId());
		assertNotNull(to.getActionType());
		assertNotNull(to.getNotificationType());
		
		assertEquals(text, to.getText());
		assertEquals(user, to.getUser());
		assertEquals(entity, to.getEntity());
		assertEquals(entity_id, to.getEntityId());
		assertEquals(activity_id, to.getActionId());
		assertEquals(ActionType.COMMENT, to.getActionType());
		assertEquals(NotificationType.DEVELOPER_NOTIFICATION, to.getNotificationType());
	}
}
