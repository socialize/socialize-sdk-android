package com.socialize.test.integration;


public class ViewTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		robotium.clickOnButton("View");
		robotium.waitForActivity("ViewActivity", DEFAULT_TIMEOUT_MILLISECONDS);
	}
	public void testCreateView() {
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.clickOnButton("Add View");
		waitForSuccess();
	}
}