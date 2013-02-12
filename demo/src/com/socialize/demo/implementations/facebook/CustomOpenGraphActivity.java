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
import com.socialize.ShareUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.demo.SDKDemoActivity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.facebook.FacebookUtils;


/**
 * @author Jason Polites
 *
 */
public class CustomOpenGraphActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#executeDemo(java.lang.String)
	 */
	@Override
	public void executeDemo(final String text) {
		
		// Link if we need to
		FacebookUtils.linkForWrite(this, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError(CustomOpenGraphActivity.this, error);
			}
			
			@Override
			public void onCancel() {
				handleCancel();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				
				// This could be a custom Open Graph path in the form namespace:action
//				final String graphPath = "me/og.likes";
				final String graphPath = "me/socializeandroidtest:eat";
				
				entity.setType("socializeandroidtest:dish");
				
				ShareOptions shareOptions = ShareUtils.getUserShareOptions(CustomOpenGraphActivity.this);
				shareOptions.setText(text);
				
				ShareUtils.shareViaSocialNetworks(CustomOpenGraphActivity.this, entity, shareOptions, new SocialNetworkShareListener() {
					@Override
					public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
						
						// Add/Change the contents of the post to fit with open graph
						postData.setPath(graphPath);
						
						// Facebook like requires an "object" parameter
						postData.getPostValues().put("dish", postData.getPropagationInfo().getEntityUrl());
						
						return false;
					}
					
					@Override
					public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
						handleError(CustomOpenGraphActivity.this, error);
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
							handleError(CustomOpenGraphActivity.this, e);
						}
					}
				}, SocialNetwork.FACEBOOK);
				
//				FacebookUtils.postEntity(OpenGraphActivity.this, entity, text, );
				
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError(CustomOpenGraphActivity.this, error);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "Facebook Like";
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.SDKDemoActivity#isTextEntryRequired()
	 */
	@Override
	public boolean isTextEntryRequired() {
		return true;
	}

}
