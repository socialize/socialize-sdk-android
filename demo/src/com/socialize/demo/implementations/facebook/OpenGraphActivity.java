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

import java.util.HashMap;
import java.util.Map;
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
public class OpenGraphActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#executeDemo(java.lang.String)
	 */
	@Override
	public void executeDemo(final String text) {
		
		FacebookUtils.link(this, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError(OpenGraphActivity.this, error);
			}
			
			@Override
			public void onCancel() {
				handleCancel();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				
				Map<String, Object> postData = new HashMap<String, Object>();
				postData.put("object", "http://samples.ogp.me/245211918931839"); // <-- Entity URL, expects og:type tag of socializeandroidtest:dish
				
				String graphPath = "me/og.likes";
				
				FacebookUtils.post(OpenGraphActivity.this, graphPath, postData, new SocialNetworkPostListener() {
					
					@Override
					public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
						handleError(OpenGraphActivity.this, error);
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
							handleError(OpenGraphActivity.this, e);
						}
					}
				});
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError(OpenGraphActivity.this, error);
			}
		}, "publish_actions");
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "Eat a food";
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#isTextEntryRequired()
	 */
	@Override
	public boolean isTextEntryRequired() {
		return true;
	}

}
