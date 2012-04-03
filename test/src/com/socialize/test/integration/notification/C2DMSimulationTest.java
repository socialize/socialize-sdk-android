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
package com.socialize.test.integration.notification;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Notification;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.config.SocializeConfig;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.NotificationManagerFacade;
import com.socialize.notifications.NotificationsAccess;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.test.SocializeUnitTest;


/**
 * Base class for c2dm integration tests.
 * @author Jason Polites
 */
public abstract class C2DMSimulationTest extends SocializeUnitTest {

	SocializeC2DMReceiver receiver;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		receiver = new SocializeC2DMReceiver() {
			@Override
			protected Context getContext() {
				return C2DMSimulationTest.this.getContext();
			}
		};
		
		// Set override
		NotificationsAccess.setBeanOverrides(receiver, new String[]{SocializeConfig.SOCIALIZE_CORE_BEANS_PATH, SocializeConfig.SOCIALIZE_NOTIFICATION_BEANS_PATH, "socialize_notification_mock_beans.xml"});
		
		// Create the receiver
		receiver.onCreate();
	}
	
	public void testOnMessage() throws JSONException, InterruptedException, CanceledException {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ProxyObject<NotificationManagerFacade> proxy = NotificationsAccess.getProxy(receiver, "notificationManagerFacade");
		
		proxy.setDelegate(new NotificationManagerFacade() {
			
			@Override
			public void notify(Context context, String tag, int id, Notification notification) {
				addResult(0, tag);
				addResult(1, Integer.valueOf(id));
				addResult(2, notification);
				latch.countDown();
			}
		});
		
		JSONObject json = getNotificationMessagePacket();
		Intent intent = getMessageIntent(json);
		receiver.onMessage(getContext(), intent);
		
		latch.await(10, TimeUnit.SECONDS);
		
		String tag = getResult(0);
		Integer id = getResult(1);
		Notification notification = getResult(2);
		
		assertNotNull(tag);
		assertNotNull(id);
		assertNotNull(notification);
		assertNotNull(notification.contentIntent);
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected Intent getMessageIntent(JSONObject json) {
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(C2DMCallback.MESSAGE_KEY, json.toString());
		extras.putString(C2DMCallback.SOURCE_KEY, "socialize");
		intent.putExtras(extras);
		return intent;
	}
	
	protected abstract JSONObject getNotificationMessagePacket() throws JSONException;
}
