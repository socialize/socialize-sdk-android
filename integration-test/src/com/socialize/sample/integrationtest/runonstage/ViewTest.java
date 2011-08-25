package com.socialize.sample.integrationtest.runonstage;

import com.socialize.sample.integrationtest.SocializeRobotiumTest;

public class ViewTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		robotium.clickOnButton("View");
		robotium.waitForActivity("ViewActivity", DEFAULT_TIMEOUT_SECONDS);
	}
	public void testCreateView() {
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.clickOnButton("Add View");
		waitForSuccess();
	}
}