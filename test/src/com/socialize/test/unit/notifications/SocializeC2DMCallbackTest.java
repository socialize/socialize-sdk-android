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
import com.socialize.api.SocializeSession;
import com.socialize.entity.JSONFactory;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.notifications.*;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.profile.UserSettings;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.util.Map;

/**
 * @author Jason Polites
 *
 */
public class SocializeC2DMCallbackTest extends SocializeUnitTest {

	public void test_onRegister() {
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		NotificationRegistrationSystem notificationRegistrationSystem = Mockito.mock(NotificationRegistrationSystem.class);
		
		final String registrationId = "foobar";

		SocializeC2DMCallback callback = new SocializeC2DMCallback();
		callback.setNotificationRegistrationState(notificationRegistrationState);
		callback.setNotificationRegistrationSystem(notificationRegistrationSystem);
		
		callback.onRegister(getContext(), registrationId);
		
		Mockito.verify(notificationRegistrationState).setC2DMRegistrationId(registrationId);
        Mockito.verify(notificationRegistrationState).save(getContext());
        Mockito.verify(notificationRegistrationSystem).registerSocialize(getContext(), registrationId);
	}
	
	@SuppressWarnings("unchecked")
	public void test_handleNotification() throws JSONException, SocializeException {
		
		Bundle data = new Bundle();
		data.putString(C2DMCallback.MESSAGE_KEY, "foobar");
		
		final int icon = 69;
		final int notificatioId = 1337;
		final long entityId = 55378008;
		
		final Context context = Mockito.mock(MockContext.class);
		final SocializeSession session = Mockito.mock(SocializeSession.class);
		final User user = Mockito.mock(User.class);
		final UserSettings userSettings = Mockito.mock(UserSettings.class);
		final JSONObject message = Mockito.mock(JSONObject.class);
		final NotificationType notificationType = NotificationType.NEW_COMMENTS;
		final Map<String, JSONFactory<NotificationMessage>> messageFactories = Mockito.mock(Map.class);
		final Map<String, NotificationMessageBuilder> messageBuilders = Mockito.mock(Map.class);
		final JSONFactory<NotificationMessage> factory = Mockito.mock(JSONFactory.class);
		final NotificationMessageBuilder builder = Mockito.mock(NotificationMessageBuilder.class);
		final NotificationMessage notificationMessage = Mockito.mock(NotificationMessage.class);
		final Notification notification = Mockito.mock(Notification.class);
		
		Mockito.when(session.getUser()).thenReturn(user);
		Mockito.when(session.getUserSettings()).thenReturn(userSettings);
		Mockito.when(userSettings.isNotificationsEnabled()).thenReturn(true);
		Mockito.when(message.has("notification_type")).thenReturn(true);
		Mockito.when(message.isNull("notification_type")).thenReturn(false);
		Mockito.when(message.getString("notification_type")).thenReturn(notificationType.name().toLowerCase());
		Mockito.when(factory.fromJSON(message)).thenReturn(notificationMessage);
		Mockito.when(messageBuilders.get(NotificationType.NEW_COMMENTS.name())).thenReturn(builder);
		Mockito.when(messageFactories.get(NotificationType.NEW_COMMENTS.name())).thenReturn(factory);
		Mockito.when(builder.build(context, data, notificationMessage, icon)).thenReturn(notification);
		Mockito.when(notificationMessage.getEntityId()).thenReturn(entityId);
	
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
}
