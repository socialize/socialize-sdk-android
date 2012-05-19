package com.socialize.test.ui.integrationtest.actionbar;

import android.test.ActivityInstrumentationTestCase2;
import com.socialize.sample.ui.ActionBarManualActivity2;
import com.socialize.test.ui.util.TestUtils;

public class ActionBarLikeManualTest extends ActivityInstrumentationTestCase2<ActionBarManualActivity2> {

	public ActionBarLikeManualTest() {
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
	
	public void testLikeCallsApiHost() throws Throwable {
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikeCallsApiHost(this);
	}
	
	public void testLikePromptsForAuth() throws Throwable {
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikePromptsForAuth(this);
	}
	
	public void testLikeDoesNotPromptForAuthWhenNetworksNotSupported() throws Throwable {
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikeDoesNotPromptForAuthWhenNetworksNotSupported(this);
	}	
}
