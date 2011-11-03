package com.socialize.ui.test.integrationtest.actionbar;

import java.util.ArrayList;

import android.app.Activity;

import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.sample.ActionBarAutoActivity;
import com.socialize.ui.sample.ActionBarManualActivity;
import com.socialize.ui.test.integrationtest.SocializeUIRobotiumTest;
import com.socialize.ui.test.util.TestUtils;

// Tests that the actionbar loads
public class ActionBarUITest extends SocializeUIRobotiumTest {

	
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
		assertTrue(robotium.waitForActivity("ActionBarAutoActivity", 5000));
		assertTrue(robotium.waitForView(ActionBarView.class, 1, 5000));
		assertLoaded(findActivity(ActionBarAutoActivity.class));
	}
	
	protected void startManual() {
		showActionBarManual();
		robotium.waitForActivity("ActionBarManualActivity", 5000);
		robotium.waitForView(ActionBarView.class, 1, 5000);
		assertLoaded(findActivity(ActionBarManualActivity.class));
	}
		
	protected void assertLoaded(Activity activity) {
		assertNotNull(activity);
		assertTrue("Failed to find Comment text", TestUtils.lookForText(activity, "Comment", 5000));
		assertTrue("Failed to find Share text", TestUtils.lookForText(activity, "Share", 5000));
		assertTrue("Failed to find Like text",  TestUtils.lookForText(activity, "Like", 5000));
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

	public void testActionBarLoadAuto() {
		startWithoutFacebook();
		startAuto();
	}
	
	public void testActionBarLoadManual() {
		startWithoutFacebook();
		startManual();
	}
}
