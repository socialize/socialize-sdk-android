package com.socialize.test.ui.integrationtest.actionbar;

import android.test.ActivityInstrumentationTestCase2;

import com.socialize.sample.ui.ActionBarAutoActivity2;
import com.socialize.test.ui.util.TestUtils;

public class ActionBarLikeAutoTest extends ActivityInstrumentationTestCase2<ActionBarAutoActivity2> {

	public ActionBarLikeAutoTest() {
		super("com.socialize.sample.ui", ActionBarAutoActivity2.class);
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
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikeCallsApiHost(this);
	}
}
