package com.socialize.test.ui.integrationtest.actionbar;


public class ActionBarLikeAutoTest extends ActionBarAutoTest {

	public void testLikeCallsApiHost() throws Throwable {
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikeCallsApiHost(this);
	}
	
	public void testLikePromotsForAuth() throws Throwable {
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikePromptsForAuth(this);
	}	
	
	public void testLikeDoesNotPromptForAuthWhenNetworksNotSupported() throws Throwable {
		ActionBarTestUtils2 utils = new ActionBarTestUtils2();
		utils.testLikeDoesNotPromptForAuthWhenNetworksNotSupported(this);
	}	
}
