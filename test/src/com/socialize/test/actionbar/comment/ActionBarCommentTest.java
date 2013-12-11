package com.socialize.test.actionbar.comment;

import android.app.Activity;
import android.app.Instrumentation;
import com.socialize.test.actionbar.ActionBarTest;
import com.socialize.test.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.comment.CommentActivity;


public class ActionBarCommentTest extends ActionBarTest {
	
	@Override
	public boolean isManual() {
		return false;
	}
	
	public void testCommentButtonOpensCommentView() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);

        Instrumentation.ActivityMonitor monitor = TestUtils.setUpActivityMonitor(this, CommentActivity.class);

        final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 5000);
		
		assertNotNull(actionBar);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getCommentButton().performClick());
			}
		});
		
		Activity commentActivity = monitor.waitForActivityWithTimeout(5000);
		
		assertNotNull(commentActivity);
		assertTrue(commentActivity instanceof CommentActivity);
        commentActivity.finish();
	}
}
