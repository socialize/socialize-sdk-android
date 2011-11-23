package com.socialize.test.integration.runonprod;
import android.widget.TextView;

import com.socialize.test.integration.SocializeRobotiumTest;

public class EntityTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		
		robotium.clickOnButton("Entity");
		robotium.waitForActivity("EntityActivity", DEFAULT_TIMEOUT_SECONDS);
	}
	
	public void testGetEntity() {
		// We need create first
		robotium.enterText(0, DEFAULT_GET_ENTITY);
		
		robotium.clickOnButton("Get Entity");
		
		waitForSuccess();
		
		String entityKey2 = getEntityKey();
		
		assertEquals(DEFAULT_GET_ENTITY, entityKey2);
	}
	
	private String getEntityKey() {
		
		sleep(2000); // Wait for text field to be updated
		
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtEntityKeyCreated);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		
		return txt.getText().toString();
	}
}