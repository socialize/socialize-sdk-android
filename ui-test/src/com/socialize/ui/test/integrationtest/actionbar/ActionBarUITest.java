package com.socialize.ui.test.integrationtest.actionbar;

import java.util.ArrayList;

import android.app.Activity;
import android.location.Location;

import com.socialize.SocializeBeanAccess;
import com.socialize.api.ApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.listener.like.LikeListener;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.sample.ActionBarAutoActivity;
import com.socialize.ui.sample.ActionBarManualActivity;
import com.socialize.ui.sample.mock.MockSocializeApiHost;
import com.socialize.ui.test.integrationtest.DefaultTestApiHost;
import com.socialize.ui.test.integrationtest.SocializeUIRobotiumTest;
import com.socialize.ui.test.util.TestUtils;

// Tests that the actionbar loads
public class ActionBarUITest extends SocializeUIRobotiumTest {
	
	protected ActionBarLayoutView actionBar = null;

	@Override
	protected void startWithFacebook(boolean sso) {
		super.startWithFacebook(sso);
		toggleMockedSocialize(true);
		sleep(2000);
	}

	@Override
	protected void startWithoutFacebook() {
		super.startWithoutFacebook();
		toggleMockedSocialize(true);
		sleep(2000);
	}
	
	protected void startAuto() {
		showActionBarAuto();
		assertTrue(robotium.waitForView(ActionBarView.class, 1, 5000));
		Activity activity = findActivity(ActionBarAutoActivity.class);
		assertLoaded(activity);
		
		actionBar =  TestUtils.findView(activity, ActionBarLayoutView.class, 20000);
	}
	
	protected void startManual() {
		showActionBarManual();
		robotium.waitForView(ActionBarView.class, 1, 5000);
		Activity activity = findActivity(ActionBarManualActivity.class);
		assertLoaded(activity);
		
		actionBar =  TestUtils.findView(activity, ActionBarLayoutView.class, 20000);	
	}
		
	protected void assertLoaded(Activity activity) {
		assertNotNull(activity);
		assertTrue("Failed to find Comment text", TestUtils.lookForText(activity, "Comment", 20000));
		assertTrue("Failed to find Share text", TestUtils.lookForText(activity, "Share", 20000));
		assertTrue("Failed to find Like text",  TestUtils.lookForText(activity, "Like", 20000));
	}
	
	protected Activity findActivity(Class<?> cls) {
		ArrayList<Activity> allOpenedActivities = robotium.getAllOpenedActivities();

		Activity found = null;

		for (Activity activity : allOpenedActivities) {
			if(cls.isAssignableFrom(activity.getClass())) {
				found = activity;
				break;
			}
		}

		return found;
	}
	
	protected void setApiHostDelegate(ApiHost delegate) {
		SocializeBeanAccess accessor = new SocializeBeanAccess();
		Object bean = accessor.getBean("socializeApiHost");
		if(bean instanceof MockSocializeApiHost) {
			MockSocializeApiHost host = (MockSocializeApiHost) bean;
			host.setDelegate(delegate);
		}
		else {
			fail("Can not set ApiHostDelegate on non MockSocializeApiHost");
		}
	}

	public void testActionBarLoadAuto() {
		startWithoutFacebook();
		startAuto();
	}
	
	public void testActionBarLoadManual() {
		startWithoutFacebook();
		startManual();
	}
	
	public void testActionBarAutoLike() throws Throwable {
		startWithoutFacebook();
		startAuto();
		actionBarLike();
	}
	
	public void testActionBarAutoComment() throws Throwable {
		startWithoutFacebook();
		startAuto();
		actionBarComment();
	}
	
	public void testActionBarManualLike() throws Throwable {
		startWithoutFacebook();
		startManual();
		actionBarLike();
	}
	
	public void testActionBarManualComment() throws Throwable {
		startWithoutFacebook();
		startManual();
		actionBarComment();
	}
	
	public void actionBarLike() throws Throwable {
		setApiHostDelegate(new DefaultTestApiHost() {
			@Override
			public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
				addResult("success");
			}
		});
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				sleep(1000);
				String result = getNextResult();
				assertNotNull(result);
				assertEquals("success", result);
			}
		});

	}	
	
	public void actionBarComment() throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getCommentButton().performClick());
			}
		});
		
		sleep(1000);
		assertTrue(robotium.waitForActivity("CommentActivity", 10));
	}
}
