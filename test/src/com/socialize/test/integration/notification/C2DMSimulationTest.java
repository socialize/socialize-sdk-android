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

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.OnFinished;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.launcher.BaseLauncher;
import com.socialize.launcher.LaunchAction;
import com.socialize.launcher.Launcher;
import com.socialize.listener.SocializeInitListener;
import com.socialize.log.SocializeLogger.LogLevel;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.NotificationManagerFacade;
import com.socialize.notifications.NotificationsAccess;
import com.socialize.notifications.SocializeC2DMReceiverHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.ui.SocializeLaunchActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Base class for c2dm integration tests.
 * @author Jason Polites
 */
public abstract class C2DMSimulationTest extends SocializeActivityTest {

	SocializeC2DMReceiverHandler receiver;
	protected long entityId = -1;
	protected long commentId = -1;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Socialize.DEFAULT_LOG_LEVEL = LogLevel.DEBUG;
		
		Socialize.getSocialize().destroy(true);
		NotificationsAccess.destroy(receiver);		
		
		receiver = new SocializeC2DMReceiverHandler();
		
		// Set override
		NotificationsAccess.setBeanOverrides(receiver, new String[]{SocializeConfig.SOCIALIZE_CORE_BEANS_PATH, SocializeConfig.SOCIALIZE_NOTIFICATION_BEANS_PATH, "socialize_notification_mock_beans.xml"});
		SocializeAccess.setBeanOverrides("socialize_notification_mock_beans.xml");
		
		// Create the receiver
		receiver.onCreate(getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.restart(this);
		super.tearDown();
	}

	public void testOnMessage() throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch launchLock = new CountDownLatch(1);
		
		// Override the behaviour of the notification manager so it doesn't actually post a status bar notification.
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
		
		if(disableLauncher()) {
			
			// Launcher is triggered in normal socialize context, not notification context.
			SocializeAccess.setInitListener(new SocializeInitListener() {
				
				@Override
				public void onError(SocializeException error) {
					error.printStackTrace();
					fail();
				}

				@Override
				public void onInit(Context context, IOCContainer container) {
					
					addResult(10, "init");
					
					ProxyObject<Launcher> launcherProxy = container.getProxy(getLauncherBeanName());

					launcherProxy.setDelegate(new BaseLauncher() {
						@Override
						public boolean shouldFinish(Activity context) {
							return true;
						}
						
						@Override
						public void onResult(Activity context, int requestCode, int resultCode, Intent returnedIntent, Intent originalIntent) {}
						
						@Override
						public boolean launch(Activity context, Bundle data) {
							addResult(4, "launch");
							launchLock.countDown();
							return true;
						}
					});
				}
			});
			
			// Make sure the init listener is triggered.
			Socialize.init(getContext());
			
			String init = getResult(10);
			assertNotNull(init);
			
		}
		
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
		
		final CountDownLatch intentLatch = new CountDownLatch(1);
		
		notification.contentIntent.send(0, new OnFinished() {
			
			@Override
			public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
				addResult(3, intent);
				intentLatch.countDown();
			}
		}, null);
		
		intentLatch.await(10, TimeUnit.SECONDS);
		
		Intent intentAfterNotify = getResult(3);
		
		assertNotNull(intentAfterNotify);
		
		Bundle extras = intentAfterNotify.getExtras();
		
		assertNotNull(extras);
		
		// Expect the launch activity to be launched.
		ComponentName expected = new ComponentName(getContext(), SocializeLaunchActivity.class);
		
		assertEquals(expected, intentAfterNotify.getComponent());
		
		String launchAction = extras.getString(SocializeLaunchActivity.LAUNCH_ACTION);
		
		assertNotNull(launchAction);
		LaunchAction action = LaunchAction.valueOf(launchAction.toUpperCase());
		
		assertEquals(getExpectedLaunchAction(), action);
		
		assertNotificationBundle(extras);
		
		if(disableLauncher()) {
			launchLock.await(10, TimeUnit.SECONDS);
			assertNotNull(getResult(4));
		}
	}

	
	protected Intent getMessageIntent(JSONObject json) {
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(C2DMCallback.MESSAGE_KEY, json.toString());
		extras.putString(C2DMCallback.SOURCE_KEY, "socialize");
		intent.putExtras(extras);
		return intent;
	}
	
	protected abstract String getLauncherBeanName();
	
	protected abstract JSONObject getNotificationMessagePacket() throws Exception;
	
	/**
	 * Asserts that the extras bundle sent to the launch activity contains what we need for the notification.
	 * @param extras
	 */
	protected abstract void assertNotificationBundle(Bundle extras) throws Exception;
	
	protected abstract LaunchAction getExpectedLaunchAction();
	
	protected boolean disableLauncher() {
		return true;
	}
	
	/**
	 * Loads an entity from the JSON file written to disk after the initial python setup script (sdk-cleanup.py)
	 * @return
	 * @throws java.io.IOException
	 * @throws org.json.JSONException
	 */
	protected long getEntityId() throws IOException, JSONException {
		if(entityId < 0) {
			InputStream in = null;
			try {
				in = getContext().getAssets().open("existing-data/entities.json");
				String json = TestUtils.loadStream(in);
				JSONObject obj = new JSONObject(json);
				JSONArray jsonArray = obj.getJSONArray("items");
				entityId = jsonArray.getJSONObject(0).getLong("id");
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
		}
		
		return entityId;
    }
	
	protected long getCommentId() throws IOException, JSONException {
		if(commentId < 0) {
			InputStream in = null;
			try {
				in = getContext().getAssets().open("existing-data/comments.json");
				String json = TestUtils.loadStream(in);
				JSONObject obj = new JSONObject(json);
				JSONArray jsonArray = obj.getJSONArray("items");
				commentId = jsonArray.getJSONObject(0).getLong("id");
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
		}
		
		return commentId;
	}

}
