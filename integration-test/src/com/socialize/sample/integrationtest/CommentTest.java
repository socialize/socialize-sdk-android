package com.socialize.sample.integrationtest;

import android.widget.TextView;


public class CommentTest extends SocializeRobotiumTest {

	private int userId = -1;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		userId = authenticate();
		
		robotium.clickOnButton("Comment");
		robotium.waitForActivity("CommentActivity", DEFAULT_TIMEOUT_SECONDS);
	}

	public void testCreateComment() {
		
		String comment = "Test Comment";
		
		robotium.clickOnButton("Create Comment");
		robotium.waitForActivity("CommentCreateActivity", DEFAULT_TIMEOUT_SECONDS);
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.enterText(1, comment);
		robotium.clickOnButton("Create");
		
		robotium.waitForText("SUCCESS", 1, DEFAULT_TIMEOUT_SECONDS);
		
		sleep(5000);
		
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtCommentIdCreated);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		
		String value = txt.getText().toString();
		Integer.parseInt(value);
		
		// Assert the comment itself
		txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtCommentTextCreated);
		
		value = txt.getText().toString();
		
		assertEquals(comment, value);
		
		// Check the date
		txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtCommentDateCreated);
		value = txt.getText().toString();
		
		assertNotNull(value);
		assertTrue(value.trim().length() > 0);
		
		// Assert the application itself
		txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtCommentApplicationCreated);
		
		value = txt.getText().toString();
		
		assertEquals(DEFAULT_APPLICATION_NAME, value);
		
		// Assert the user id
		txt = (TextView) robotium.getView(com.socialize.sample.R.id.txtCommentUserCreated);
		
		value = txt.getText().toString();
		
		assertEquals(userId, Integer.parseInt(value));
	}
}