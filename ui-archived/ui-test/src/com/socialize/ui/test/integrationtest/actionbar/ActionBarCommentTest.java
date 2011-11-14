package com.socialize.ui.test.integrationtest.actionbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.sample.ActionBarManualActivity;
import com.socialize.ui.test.util.TestUtils;

public class ActionBarCommentTest extends ActivityInstrumentationTestCase2<ActionBarManualActivity> {

	public ActionBarCommentTest() {
		super("com.socialize.ui.sample", ActionBarManualActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown();
		super.tearDown();
	}

	public void testCommentButtonOpensCommentView() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(SocializeUI.ENTITY_KEY, "http://entity1.com");
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 5000);
		
		assertNotNull(actionBar);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getCommentButton().performClick());
			}
		});
		
		Activity waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		waitForActivity.finish();
	}
}
