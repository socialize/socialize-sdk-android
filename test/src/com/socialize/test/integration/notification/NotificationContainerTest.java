package com.socialize.test.integration.notification;

import android.content.Context;
import android.content.Intent;
import com.socialize.notifications.NotificationContainer;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.test.SocializeManagedActivityTest;
import com.socialize.testapp.EmptyActivity;

public class NotificationContainerTest extends SocializeManagedActivityTest<EmptyActivity> {

	public NotificationContainerTest() {
		super("com.socialize.testapp", EmptyActivity.class);
	}

//	public void testNotificationContainerInit() throws Exception {
//		NotificationContainer container = new NotificationContainer();
//		container.onCreate(this.getInstrumentation().getTargetContext());
//	}

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
