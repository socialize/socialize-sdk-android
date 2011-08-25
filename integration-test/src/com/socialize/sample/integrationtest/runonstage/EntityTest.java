package com.socialize.sample.integrationtest.runonstage;

import com.socialize.sample.integrationtest.SocializeRobotiumTest;

public class EntityTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		
		robotium.clickOnButton("Entity");
		robotium.waitForActivity("EntityActivity", DEFAULT_TIMEOUT_SECONDS);
	}

	public void testCreateEntity() {
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.enterText(1, "RobotiumEntityTest");
		robotium.clickOnButton("Add Entity");
		waitForSuccess();
	}
}