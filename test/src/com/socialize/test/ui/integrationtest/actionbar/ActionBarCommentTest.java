package com.socialize.test.ui.integrationtest.actionbar;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.sample.ui.ActionBarManualActivity2;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.comment.CommentActivity;

public class ActionBarCommentTest extends ActivityInstrumentationTestCase2<ActionBarManualActivity2> {

	public ActionBarCommentTest() {
		super("com.socialize.sample.ui", ActionBarManualActivity2.class);
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
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
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
