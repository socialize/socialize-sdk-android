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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchAction;
import com.socialize.notifications.*;
import com.socialize.test.SocializeUnitTest;
import com.socialize.testapp.EmptyActivity;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.util.AppUtils;


/**
 * @author Jason Polites
 *
 */
@UsesMocks ({AppUtils.class, Notification.class})
public class BaseNotificationMessageBuilderTest extends SocializeUnitTest {

	private AppUtils appUtils;
	private Notification notification;
	private Context context;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		appUtils = AndroidMock.createMock(AppUtils.class);
		notification = AndroidMock.createMock(Notification.class);
		context = getContext();
	}

	public void testBuild_NEW_COMMENTS() throws SocializeException {
		doTestBuild(NotificationType.NEW_COMMENTS, LaunchAction.ACTION);
	}
	
	public void testBuild_DEVELOPER_NOTIFICATION() throws SocializeException {
		doTestBuild(NotificationType.DEVELOPER_NOTIFICATION, LaunchAction.HOME);
	}
	
	protected void doTestBuild(NotificationType type, LaunchAction launchAction) throws SocializeException {

		final String text = "foobar_text";
		final String ntext = "foobar_ntext";
		final String title = "foobar_title";
		final int icon = 69;
		
		final NotificationMessage message = new NotificationMessage();
		message.setNotificationType(type);
		message.setText(ntext);
		
		final SimpleNotificationMessage messageData = new SimpleNotificationMessage();
		messageData.setText(text);
		messageData.setTitle(title);
		
		final Bundle bundle = new Bundle();
		bundle.putBoolean("test_value", true);
		
        final PendingIntent mockIntent = PendingIntent.getActivity(context, 0, new Intent(context, EmptyActivity.class), 0);
		
		AndroidMock.expect(appUtils.isActivityAvailable(context, SocializeLaunchActivity.class)).andReturn(true);
		
		notification.setLatestEventInfo(context, title, text, mockIntent);
		
		AndroidMock.replay(appUtils, notification);
		
		final MessageTranslator<SimpleNotificationMessage> messageTranslator = new MessageTranslator<SimpleNotificationMessage>() {
			@Override
			public SimpleNotificationMessage translate(Context context, Bundle data, NotificationMessage message) throws SocializeException {
				// Add a test value for assertion
				data.putBoolean("translate", true);
				addResult(0, message);
				return messageData;
			}
		};
		
		BaseNotificationMessageBuilder<SimpleNotificationMessage> builder = new BaseNotificationMessageBuilder<SimpleNotificationMessage>() {
			@Override
			public RemoteViews getNotificationView(Context context, Notification notification, NotificationMessage message, SimpleNotificationMessage data) {
				return null;
			}

			@Override
			protected PendingIntent getPendingIntent(Context context, NotificationMessage message, Intent notificationIntent) {
				PendingIntent intent = mockIntent;
				addResult(1, intent);
				addResult(2, notificationIntent);
				return intent;
			}

			@Override
			protected Notification newNotification(int icon, String text, long time) {
				addResult(3, text);
				addResult(4, icon);
				return notification;
			}
		};
		
		// Setup mocked dependencies
		builder.setAppUtils(appUtils);
		builder.setMessageTranslator(messageTranslator);
		
		Notification notification = builder.build(context, bundle, message, icon);
		
		AndroidMock.verify(appUtils, notification);
		
		assertNotNull(notification);
		
		assertEquals(Notification.FLAG_AUTO_CANCEL, (notification.flags & Notification.FLAG_AUTO_CANCEL));
		
		NotificationMessage notificationMessage = getResult(0);
		PendingIntent pendingIntent = getResult(1);
		Intent notificationIntent = getResult(2);
		String notifyText = getResult(3);
		Integer notifyIcon = getResult(4);
		
		assertNotNull(pendingIntent);
		assertNotNull(notificationMessage);
		assertNotNull(notificationIntent);
		assertNotNull(notifyText);
		assertNotNull(notifyIcon);
		
		Bundle extras = notificationIntent.getExtras();
		
		assertNotNull(extras);
		
		assertNotNull(extras.get(SocializeLaunchActivity.LAUNCH_ACTION));
		assertEquals(launchAction.name(), extras.getString(SocializeLaunchActivity.LAUNCH_ACTION));
		
		assertNotNull(extras.get(SocializeLaunchActivity.LAUNCH_TASK));
		assertEquals("notificationLaunchTask", extras.getString(SocializeLaunchActivity.LAUNCH_TASK));
		
		assertEquals((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY), notificationIntent.getFlags());
		
		assertTrue(extras.getBoolean("test_value"));
		assertTrue(extras.getBoolean("translate"));
		
		assertEquals(title, notification.tickerText);
		
	}
	

}
