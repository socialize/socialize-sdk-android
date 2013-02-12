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

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.widget.Toast;
import com.socialize.api.SocializeSession;
import com.socialize.demo.SDKDemoActivity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookUtils;


/**
 * @author Jason Polites
 *
 */
public class GetEmailActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#executeDemo(java.lang.String)
	 */
	@Override
	public void executeDemo(String text) {
		onFacebookBtnClicked();
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "Get Email Address";

	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#isTextEntryRequired()
	 */
	@Override
	public boolean isTextEntryRequired() {
		return false;
	}

	// ######################################################################
	private void onFacebookBtnClicked() {
		// defaults found in:   com.socialize.networks.facebook.BaseFacebookFacade
		String[] permissions = {"email"};
		FacebookUtils.linkForRead(this, new SocializeAuthListener() {
			@Override
			public void onCancel() {
				Toast.makeText(GetEmailActivity.this, "Facebook login cancelled", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				getFaceBookUserEmailetc();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError(GetEmailActivity.this, error);
			}
			
			@Override
			public void onError(SocializeException error) {
				handleError(GetEmailActivity.this, error);
			}
		}, permissions);
	}
	// ######################################################################
	private void getFaceBookUserEmailetc() {
		String graphPath = "/me";
		// Execute a GET on facebook
		// The "this" argument refers to the current Activity
		FacebookUtils.get(this, graphPath, null, new SocialNetworkPostListener() {
			@Override
			public void onCancel() {
				Toast.makeText(GetEmailActivity.this, "Facebook get cancelled", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
				handleError(GetEmailActivity.this, error);
			}

			@Override
			public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
				if(responseObject.has("email") && !responseObject.isNull("email")) {
					try {
						handleResult(responseObject.getString("email"));
					}
					catch (JSONException e) {
						handleError(GetEmailActivity.this, e);
					}
				}
				else {
					handleResult("Unable to retrieve email address!\n\n" + responseObject.toString());
				}
			}
		});
	}	
}
