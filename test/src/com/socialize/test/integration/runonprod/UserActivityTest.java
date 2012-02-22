package com.socialize.test.integration.runonprod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.widget.ListView;

import com.socialize.sample.ListItem;
import com.socialize.test.integration.SocializeRobotiumTest;

public class UserActivityTest extends SocializeRobotiumTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		authenticateSocialize();
		robotium.clickOnButton("User Activity");
		robotium.waitForActivity("UserActivitySelectActivity", DEFAULT_TIMEOUT_MILLISECONDS);
	}
	
	public void testListUserActivity() throws JSONException {
		
		// Get the user id for the comments;
		JSONObject json = getJSON("comments.json");
		JSONArray jsonArray = json.getJSONArray("items");
		JSONObject jsonObject = (JSONObject) jsonArray.get(0);
		
		JSONObject userObject = jsonObject.getJSONObject("user");
		String commentUserId = userObject.getString("id");		

		robotium.clearEditText(0);
		robotium.enterText(0,commentUserId);
		
		robotium.clickOnButton("List Activity");
		robotium.waitForActivity("UserActivityListActivity", DEFAULT_TIMEOUT_MILLISECONDS);
		robotium.waitForView(ListView.class);
		ListView activity = (ListView) robotium.getCurrentActivity().findViewById(R.id.list);
		assertEquals(14 ,activity.getCount());
		
		int comments = 0;
		int shares = 0;
		int likes = 0;
		
		for (int i = 0; i < activity.getCount(); i++) {
			Object item = activity.getItemAtPosition(i);
			assertNotNull(item);
			assertTrue(item instanceof ListItem);
			
			ListItem li = (ListItem) item;
			
			String text = li.getName();
			
			if(text.startsWith("COMMENT")) {
				comments++;
			}
			else if(text.startsWith("SHARE")) {
				shares++;
			}
			else if(text.startsWith("LIKE")) {
				likes++;
			}
		}
		
		assertEquals(10, comments);
		assertEquals(2, shares);
		assertEquals(2, likes);
	}
	
	

}