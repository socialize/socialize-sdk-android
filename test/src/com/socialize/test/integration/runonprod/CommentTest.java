package com.socialize.test.integration.runonprod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.widget.ListView;
import android.widget.TextView;

import com.socialize.test.integration.SocializeRobotiumTest;

public class CommentTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		robotium.clickOnButton("Comment");
		robotium.waitForActivity("CommentActivity", DEFAULT_TIMEOUT_MILLISECONDS);
	}
	public void testListComment() throws JSONException{
		robotium.clickOnButton("List Comments");
		robotium.waitForActivity("CommentListSelectActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		
		// get the comment based on ID from the JSON file
		robotium.enterText(0,DEFAULT_GET_ENTITY);
		robotium.clickOnButton("List");
		
		robotium.waitForActivity("CommentListActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		robotium.waitForView(ListView.class);
		
		// Check number of comments
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(R.id.list);
		
		assertEquals( 30, comments.getCount());
		
		// Click on the first comment in list. 
		robotium.clickInList(0);
		
		waitForSuccess();
		
		// Check Comment ID
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtCommentId);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		String value = txt.getText().toString();
		Integer.parseInt(value);
		
	}
	
	public void testListUserComments() throws JSONException {
		
		// Get the user id for the comments;
		JSONObject json = getJSON("comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		
		JSONObject userObject = jsonObject.getJSONObject("user");
		String commentUserId = userObject.getString("id");		

		robotium.clickOnButton("List User Comments");
		robotium.waitForActivity("CommentListSelectActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		
		robotium.clearEditText(0);
		robotium.enterText(0,commentUserId);
		
		robotium.clickOnButton("List");
		robotium.waitForActivity("CommentUserListActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		robotium.waitForView(ListView.class);
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(R.id.list);
		assertEquals(30 , comments.getCount());
	}
	
	public void testListCommentWithPagination() throws JSONException{
		robotium.clickOnButton("List Comments");
		robotium.waitForActivity("CommentListSelectActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		robotium.enterText(0,DEFAULT_GET_ENTITY);
		robotium.clearEditText(1);
		robotium.enterText(1, "0");
		robotium.clearEditText(2);
		robotium.enterText(2, "3");

		robotium.clickOnButton("List");
		
		robotium.waitForActivity("CommentListActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		robotium.waitForView(ListView.class);
		ListView comments = (ListView) robotium.getCurrentActivity().findViewById(R.id.list);
		assertEquals(3 , comments.getCount());

	}
	

}