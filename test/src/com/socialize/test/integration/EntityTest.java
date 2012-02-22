package com.socialize.test.integration;

import android.widget.TextView;

import com.socialize.sample.R;


public class EntityTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		
		robotium.clickOnButton("Entity");
		robotium.waitForActivity("EntityActivity", DEFAULT_TIMEOUT_MILLISECONDS);
	}

	public void testCreateEntity() {
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.enterText(1, "RobotiumEntityTest");
		robotium.enterText(2, "RobotiumEntityMeta");
		robotium.clickOnButton("Add Entity");
		waitForSuccess();
		
		TextView key = (TextView) robotium.getCurrentActivity().findViewById(R.id.txtEntityKeyCreated);
		TextView name = (TextView) robotium.getCurrentActivity().findViewById(R.id.txtEntityNameCreated);
		TextView meta = (TextView) robotium.getCurrentActivity().findViewById(R.id.txtEntityMetaCreated);
		
		assertEquals(DEFAULT_ENTITY_URL, key.getText().toString());
		assertEquals("RobotiumEntityTest", name.getText().toString());
		assertEquals("RobotiumEntityMeta", meta.getText().toString());
		
	}
}