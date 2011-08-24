package com.socialize.sample.integrationtest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.widget.ListView;
import android.widget.TextView;

public class CommentTest extends SocializeRobotiumTest {

	private int userId = -1;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		userId = authenticateSocialize();
		
		robotium.clickOnButton("Comment");
		robotium.waitForActivity("CommentActivity", DEFAULT_TIMEOUT_SECONDS);
	}
	public void testListComment() throws JSONException{
		robotium.clickOnButton("List Comments");
		robotium.waitForActivity("CommentListSelectActivity", DEFAULT_TIMEOUT_SECONDS);
		
		// get the comment based on ID from the JSON file
		JSONObject json = getJSON("comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		
		robotium.enterText(0,DEFAULT_GET_ENTITY);
		robotium.clickOnButton("List");
		
		robotium.waitForActivity("CommentListActivity", DEFAULT_TIMEOUT_SECONDS);
		robotium.waitForView(ListView.class);
		
		// Check number of comments
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(R.id.list);
		
		assertEquals(jsonArray.length(), comments.getCount());
	}
	public void testListCommentWithPagination() throws JSONException{
		robotium.clickOnButton("List Comments");
		robotium.waitForActivity("CommentListSelectActivity", DEFAULT_TIMEOUT_SECONDS);
		robotium.enterText(0,DEFAULT_GET_ENTITY);
		robotium.clearEditText(1);
		robotium.enterText(1, "0");
		robotium.clearEditText(2);
		robotium.enterText(2, "3");

		robotium.clickOnButton("List");
		
		robotium.waitForActivity("CommentListActivity", DEFAULT_TIMEOUT_SECONDS);
		robotium.waitForView(ListView.class);
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(R.id.list);
		assertEquals(3 , comments.getCount());

	}
	
	public void testGetComment() throws JSONException{
		robotium.clickOnButton("Get Comment");
		robotium.waitForActivity("CommentGetActivity", DEFAULT_TIMEOUT_SECONDS);
		
		// get the comment based on ID from the JSON file
		JSONObject json = getJSON("comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		
		String id = jsonObject.getString("id");
		JSONObject userObject = jsonObject.getJSONObject("user");
		String commentUserId = userObject.getString("id");

		robotium.enterText(0, id);
		robotium.clickOnButton("Get");
		waitForSuccess();
		
		sleep(2000);
		// Check Comment ID
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtCommentId);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		
		String value = txt.getText().toString();
		Integer.parseInt(value);
		assertEquals(value, id);
		
		// Check Comment text
		txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtCommentTextCreated);
		String commentString = jsonObject.getString("text"); 
		assertNotNull(txt);
		
		value = txt.getText().toString();
		assertEquals(value, commentString);
		
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
		
		assertEquals(commentUserId, value);
		
	}
	public void testCreateComment() {
		
		String comment = "Test Comment";
		
		robotium.clickOnButton("Create Comment");
		robotium.waitForActivity("CommentCreateActivity", DEFAULT_TIMEOUT_SECONDS);
		robotium.enterText(0, DEFAULT_ENTITY_URL);
		robotium.enterText(1, comment);
		robotium.clickOnButton("Create");
		
		waitForSuccess();

		sleep(2000);
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