/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.demo.implementations.facebook;

import java.util.Arrays;
import android.content.Intent;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.socialize.demo.SDKDemoActivity;


/**
 * @author Jason Polites
 *
 */
public class V3LoginActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#executeDemo(java.lang.String)
	 */
	@Override
	public void executeDemo(final String text) {
		// start Facebook Login
		Session.OpenRequest auth = new Session.OpenRequest(this);
		
		String[] permissions = {"publish_stream", "user_status"};
		
		auth.setPermissions(Arrays.asList(permissions));
		auth.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
		auth.setCallback(new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				
				switch(state) {
				
					case OPENING:
						System.out.println("OPENING");
						break;				
				
					case OPENED:
						System.out.println("OPENED");
						if (session.isOpened()) {
							// make request to the /me API
							Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
								// callback after Graph API response with user object
								@Override
								public void onCompleted(GraphUser user, Response response) {
									if(response.getError() != null) {
										handleError(V3LoginActivity.this, response.getError().getException());
									}
									else if (user != null) {
										handleResult("Success! " + user.getUsername());
									}
								}
							});
						}						
						
						break;
						
					case CREATED:
						System.out.println("CREATED");
						break;
						
					case CREATED_TOKEN_LOADED:
						System.out.println("CREATED_TOKEN_LOADED");
						break;
						
					case OPENED_TOKEN_UPDATED:
						System.out.println("OPENED_TOKEN_UPDATED");
						break;
						
					case CLOSED:
						System.out.println("CLOSED");
						if(exception != null) {
							handleError(V3LoginActivity.this, exception);
						}
						else {
							handleCancel();
						}
						break;		
						
					case CLOSED_LOGIN_FAILED:
						System.out.println("CLOSED_LOGIN_FAILED");
						if(exception != null) {
							handleError(V3LoginActivity.this, exception);
						}
						else {
							handleCancel();
						}
						break;							
				
				}
				

			}
		});
		
		Session session = new Session.Builder(this).setApplicationId("268891373224435").build();
		Session.setActiveSession(session);
		session.openForPublish(auth);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session activeSession = Session.getActiveSession();
		if(activeSession != null) {
			activeSession.onActivityResult(this, requestCode, resultCode, data);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "Login V3";
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#isTextEntryRequired()
	 */
	@Override
	public boolean isTextEntryRequired() {
		return false;
	}

}
