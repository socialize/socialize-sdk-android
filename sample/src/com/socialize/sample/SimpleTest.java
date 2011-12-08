package com.socialize.sample;

import com.socialize.Socialize;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.UserActivityListListener;

public class SimpleTest {

	public void test() {
		if(Socialize.getSocialize().isAuthenticated()) {
		
			// You can provide any user id you like, but here's how to use the current logged in user
			Long userId = Socialize.getSocialize().getSession().getUser().getId();
			
			// Set the start/end index for pagination
			int startIndex = 0;
			int endIndex = 10;
			
			// List all activity for a single user
			Socialize.getSocialize().listActivityByUser(
				userId, 
				startIndex,
				endIndex,
				new UserActivityListListener() {

					public void onError(SocializeException error) {
						// Handle error
					}
	
					public void onList(ListResult<SocializeAction> activity) {
						// Handle success
					}
			});
		}
	}
	
}
