package com.socialize.sample.integrationtest;

import android.widget.TextView;


public class LikeTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticate();
		
		robotium.clickOnButton("Like");
		robotium.waitForActivity("LikeActivity", DEFAULT_TIMEOUT_SECONDS);
	}

	public void testCreateLike() {
		
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.clickOnButton("Add Like");
		
		robotium.waitForText("SUCCESS", 1, DEFAULT_TIMEOUT_SECONDS);
		
		sleep(5000);
		
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtLikeIdCreated);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		
		String value = txt.getText().toString();
		Integer.parseInt(value);
		
		// Check the date
		txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtLikeDateCreated);
		value = txt.getText().toString();
		
		assertNotNull(value);
		assertTrue(value.trim().length() > 0);
	}
}