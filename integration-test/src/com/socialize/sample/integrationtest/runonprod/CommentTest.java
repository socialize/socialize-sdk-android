package com.socialize.sample.integrationtest.runonprod;
import com.socialize.sample.integrationtest.SocializeRobotiumTest;

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
		
		// Click on the first comment in list. 
		robotium.clickInList(0);
		waitForSuccess();
		sleep(2000);
		
		// Check Comment ID
		TextView txt = (TextView) robotium.getCurrentActivity().findViewById(com.socialize.sample.R.id.txtCommentId);
		
		// This is ID, it should be integer
		assertNotNull(txt);
		String value = txt.getText().toString();
		Integer.parseInt(value);
		
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
	

}