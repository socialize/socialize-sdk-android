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
package com.socialize.test.unit.notifications;

import android.content.Context;
import android.content.Intent;

import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.ActivitySystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.notifications.NotificationAuthenticator;
import com.socialize.notifications.NotificationsAccess;
import com.socialize.sample.mocks.MockActivitySystem;
import com.socialize.sample.mocks.MockSocializeSession;
import com.socialize.test.SocializeActivityTest;

/**
 * @author Jason Polites
 *
 */
public class NotificationSystemTest extends SocializeActivityTest {

	public void testReceiveNotificationIntegrationTest() throws Exception {
		
		final Comment theAction = new Comment();
		final User user = new User();
		
		theAction.setId(0L);
		theAction.setText("Hello World");
		
		theAction.setEntity(Entity.newInstance("http://www.getsocialize.com", "Socialize"));
		
		user.setId(69L);
		user.setFirstName("John");
		theAction.setUser(user);
		
		PublicSocializeC2DMReceiver receiver = new PublicSocializeC2DMReceiver() {
			@Override
			public Context getContext() {
				return NotificationSystemTest.this.getContext();
			}
		};
		
		NotificationsAccess.setBeanOverrides(receiver, new String[]{SocializeConfig.SOCIALIZE_NOTIFICATION_BEANS_PATH, "socialize_notification_mock_beans.xml"});
		
		receiver.init();
		
		ProxyObject<ActivitySystem> proxy = NotificationsAccess.getProxy(receiver, "activitySystem");
		ProxyObject<NotificationAuthenticator> notificationAuthenticatorProxy = NotificationsAccess.getProxy(receiver, "notificationAuthenticator");
		
		proxy.setDelegate(new MockActivitySystem() {
			@Override
			public SocializeAction getAction(SocializeSession session, long id, ActionType type) throws SocializeException {
				return theAction;
			}
		});
		
		notificationAuthenticatorProxy.setDelegate(new NotificationAuthenticator() {
			@Override
			public SocializeSession authenticate(Context context) throws SocializeException {
				return new MockSocializeSession();
			}
		});
		
		Intent intent = new Intent();
		
		String message = "{  text: 'Sample message', activity_id : 123, activity_type : 'comment',  notification_type : 'new_comments' }";
		
		intent.putExtra("message", message);
		
		receiver.onMessage(getContext(), intent);
	}
}
