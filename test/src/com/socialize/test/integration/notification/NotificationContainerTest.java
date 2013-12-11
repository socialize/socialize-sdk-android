package com.socialize.test.integration.notification;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.testapp.EmptyActivity;

public class NotificationContainerTest extends ActivityInstrumentationTestCase2<EmptyActivity> {

	public NotificationContainerTest() {
		super(EmptyActivity.class);
	}

	public void testSocializeC2DMReceiver() throws Exception {
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected Context getContext() {
				return getInstrumentation().getTargetContext();
			}
		};
		Intent intent = new Intent();
		intent.putExtra("foo", "bar");
		receiver.onCreate();
		receiver.onMessage(getInstrumentation().getTargetContext(), intent);
	}
}
