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

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.test.mock.MockContext;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.entity.JSONFactory;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.notifications.*;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.profile.UserSettings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Jason Polites
 *
 */
public class SocializeC2DMCallbackTest extends SocializeUnitTest {

	@UsesMocks ({NotificationRegistrationState.class, NotificationRegistrationSystem.class})
	public void test_onRegister() {
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		NotificationRegistrationSystem notificationRegistrationSystem = AndroidMock.createMock(NotificationRegistrationSystem.class);
		
		final String registrationId = "foobar";
		
		notificationRegistrationState.setC2DMRegistrationId(registrationId);
		notificationRegistrationState.save(getContext());
		notificationRegistrationSystem.registerSocialize(getContext(), registrationId);
		
		AndroidMock.replay(notificationRegistrationState, notificationRegistrationSystem);
		
		SocializeC2DMCallback callback = new SocializeC2DMCallback();
		callback.setNotificationRegistrationState(notificationRegistrationState);
		callback.setNotificationRegistrationSystem(notificationRegistrationSystem);
		
		callback.onRegister(getContext(), registrationId);
		
		AndroidMock.verify(notificationRegistrationState, notificationRegistrationSystem);
		
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		User.class, 
		UserSettings.class,
		JSONObject.class, 
		Map.class, 
		JSONFactory.class, 
		NotificationMessageBuilder.class, 
		NotificationMessage.class, 
		Notification.class, 
		MockContext.class,
		SocializeSession.class})
	public void test_handleNotification() throws JSONException, SocializeException {
		
		Bundle data = new Bundle();
		data.putString(C2DMCallback.MESSAGE_KEY, "foobar");
		
		final int icon = 69;
		final int notificatioId = 1337;
		final long entityId = 55378008;
		
		final Context context = AndroidMock.createMock(MockContext.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final User user = AndroidMock.createMock(User.class);
		final UserSettings userSettings = AndroidMock.createMock(UserSettings.class);
		final JSONObject message = AndroidMock.createMock(JSONObject.class);
		final NotificationType notificationType = NotificationType.NEW_COMMENTS;
		final Map<String, JSONFactory<NotificationMessage>> messageFactories = AndroidMock.createMock(Map.class);
		final Map<String, NotificationMessageBuilder> messageBuilders = AndroidMock.createMock(Map.class);
		final JSONFactory<NotificationMessage> factory = AndroidMock.createMock(JSONFactory.class);
		final NotificationMessageBuilder builder = AndroidMock.createMock(NotificationMessageBuilder.class);
		final NotificationMessage notificationMessage = AndroidMock.createMock(NotificationMessage.class);
		final Notification notification = AndroidMock.createMock(Notification.class);
		
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(session.getUserSettings()).andReturn(userSettings);
		AndroidMock.expect(userSettings.isNotificationsEnabled()).andReturn(true);
		AndroidMock.expect(message.has("notification_type")).andReturn(true);
		AndroidMock.expect(message.isNull("notification_type")).andReturn(false);
		AndroidMock.expect(message.getString("notification_type")).andReturn(notificationType.name().toLowerCase());
		AndroidMock.expect(factory.fromJSON(message)).andReturn(notificationMessage);
		AndroidMock.expect(messageBuilders.get(NotificationType.NEW_COMMENTS.name())).andReturn(builder);
		AndroidMock.expect(messageFactories.get(NotificationType.NEW_COMMENTS.name())).andReturn(factory);
		AndroidMock.expect(builder.build(context, data, notificationMessage, icon)).andReturn(notification);
		AndroidMock.expect(notificationMessage.getEntityId()).andReturn(entityId).anyTimes();
	
		AndroidMock.replay(session, user, userSettings, message, factory, messageBuilders, messageFactories, builder, context, notificationMessage);
		
		PublicSocializeC2DMCallback callback = new PublicSocializeC2DMCallback() {
			@Override
			protected JSONObject newJSONObject(String json) throws JSONException {
				addResult(3, json);
				return message;
			}

			@Override
			public int getNotificationIcon(Context context) {
				return icon;
			}

			@Override
			public int getNotificationId(NotificationMessage message) {
				return notificatioId;
			}

			@Override
			protected void doNotify(Context context, String tag, int id, Notification notification) {
				addResult(0, tag);
				addResult(1, id);
				addResult(2, notification);
			}
		};
		
		
		callback.setMessageBuilders(messageBuilders);
		callback.setMessageFactories(messageFactories);
		callback.handleNotification(context, data, session);
		
		AndroidMock.verify(session, user, userSettings, message, factory, messageBuilders, messageFactories, builder, context, notificationMessage);

		String tag = getResult(0);
		Integer id = getResult(1);
		Notification n = getResult(2);
		
		assertNotNull(tag);
		assertNotNull(id);
		assertNotNull(n);
		
		assertEquals("55378008", tag);
		assertEquals(notificatioId, id.intValue());
		assertSame(notification, n);
		
		String jsonAfter = getResult(3);
		assertNotNull(jsonAfter);
		assertEquals("foobar", jsonAfter);
		
	}
	
//	public void test_onMessage() {
//		
//	}
}
