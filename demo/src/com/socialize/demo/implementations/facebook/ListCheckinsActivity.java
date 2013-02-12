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
public class ListCheckinsActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#executeDemo()
	 */
	@Override
	public void executeDemo(String text) {
		
		// We need additional permissions for this
		String[] permissions = {"user_status"};
		
		FacebookUtils.linkForRead(this, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError(ListCheckinsActivity.this, error);
			}
			
			@Override
			public void onCancel() {
				handleCancel();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				
				FacebookUtils.get(ListCheckinsActivity.this, "me/checkins", null, new SocialNetworkPostListener() {
					
					@Override
					public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
						handleError(context, error);
					}
					
					@Override
					public void onCancel() {
						handleCancel();
					}
					
					@Override
					public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
						try {
							handleResult(responseObject.toString(4));
						}
						catch (JSONException e) {
							handleError(parent, e);
						}
					}
				});
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError(ListCheckinsActivity.this, error);
			}
		}, permissions);
	}
	
	@Override
	public boolean isTextEntryRequired() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "List My Checkins";
	}
}
