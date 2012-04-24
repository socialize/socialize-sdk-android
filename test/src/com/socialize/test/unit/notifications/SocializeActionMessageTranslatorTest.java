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

import android.content.Context;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.activity.ActivitySystem;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.notifications.NotificationAuthenticator;
import com.socialize.notifications.NotificationMessage;
import com.socialize.notifications.SocializeActionMessageTranslator;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
@Deprecated
public class SocializeActionMessageTranslatorTest extends SocializeUnitTest{

	@UsesMocks ({
		NotificationAuthenticator.class, 
		ActivitySystem.class, 
		SocializeSession.class, 
		SocializeAction.class, 
		User.class,
		NotificationMessage.class})
	public void test_translate() throws SocializeException {
		
		NotificationAuthenticator notificationAuthenticator = AndroidMock.createMock(NotificationAuthenticator.class);
		ActivitySystem activitySystem = AndroidMock.createMock(ActivitySystem.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeAction action = AndroidMock.createMock(SocializeAction.class);
		User user = AndroidMock.createMock(User.class);
		NotificationMessage message = AndroidMock.createMock(NotificationMessage.class);
		
		final long actionId = 69L;
		final long userId = 1001L;
		final ActionType type = ActionType.COMMENT;
		final Context context = getContext();
		
		AndroidMock.expect(message.getActionId()).andReturn(actionId).anyTimes();
		AndroidMock.expect(message.getActionType()).andReturn(type).anyTimes();
		AndroidMock.expect(notificationAuthenticator.authenticate(context)).andReturn(session);
		AndroidMock.expect(activitySystem.getAction(session, actionId, type)).andReturn(action);
		AndroidMock.expect(action.getUser()).andReturn(user);
		AndroidMock.expect(user.getId()).andReturn(userId).anyTimes();
		
		AndroidMock.replay(notificationAuthenticator, activitySystem, session, action, user, message);
		
		SocializeActionMessageTranslator translator = new SocializeActionMessageTranslator();
		Bundle data = new Bundle();
		
		translator.setActivitySystem(activitySystem);
		translator.setNotificationAuthenticator(notificationAuthenticator);
		
		SocializeAction translated = translator.translate(context, data, message);
		
		AndroidMock.verify(notificationAuthenticator, activitySystem, session, action, user, message);
		
		assertNotNull(translated);
		assertSame(action, translated);
		assertNotNull(data.getString(Socialize.ACTION_ID));
		assertNotNull(data.getString(Socialize.USER_ID));
		
		assertEquals(String.valueOf(actionId), data.getString(Socialize.ACTION_ID));
		assertEquals(String.valueOf(userId), data.getString(Socialize.USER_ID));
	}
}
