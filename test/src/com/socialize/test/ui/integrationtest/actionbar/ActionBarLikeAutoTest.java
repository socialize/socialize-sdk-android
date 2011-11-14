package com.socialize.test.ui.integrationtest.actionbar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.socialize.api.SocializeSession;
import com.socialize.listener.like.LikeListener;
import com.socialize.sample.ui.ActionBarAutoActivity;
import com.socialize.test.ui.integrationtest.DefaultTestApiHost;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarLayoutView;

public class ActionBarLikeAutoTest extends ActivityInstrumentationTestCase2<ActionBarAutoActivity> {

	public ActionBarLikeAutoTest() {
		super("com.socialize.ui.sample", ActionBarAutoActivity.class);
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

	public void testLikeCallsApiHost() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putString(SocializeUI.ENTITY_KEY, "http://entity1.com");
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 5000);	
		
		assertNotNull(actionBar);
		
		ActionBarTestUtils.setApiHostDelegate(new DefaultTestApiHost() {
			@Override
			public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
				TestUtils.addResult("success");
			}
		});
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				String result = TestUtils.getNextResult();
				assertNotNull(result);
				assertEquals("success", result);
				ActionBarTestUtils.clearApiHostDelegate();
			}
		});		
	}
}
