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
import org.json.JSONObject;
import android.app.Activity;
import com.socialize.api.SocializeSession;
import com.socialize.demo.SDKDemoActivity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.networks.facebook.FacebookUtils;


/**
 * @author Jason Polites
 *
 */
public class PostToWallActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#executeDemo()
	 */
	@Override
	public void executeDemo(final String text) {
		
		if(!FacebookUtils.isLinkedForRead(this, FacebookFacade.READ_PERMISSIONS)) {
			FacebookUtils.linkForRead(this, new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					handleError(PostToWallActivity.this, error);
				}
				
				@Override
				public void onCancel() {
					handleCancel();
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					doPost(text);
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					handleError(PostToWallActivity.this, error);
				}
			}, FacebookFacade.READ_PERMISSIONS);
		}
		else {
			doPost(text);
		}
	}
	
	private void doPost(final String text) {
		FacebookUtils.linkForWrite(this, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError(PostToWallActivity.this, error);
			}
			
			@Override
			public void onCancel() {
				handleCancel();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				
				// Params for a Post at: http://developers.facebook.com/docs/reference/api/post/
				Map<String, Object> postData = new HashMap<String, Object>();
				postData.put("name", "Socialize Test");
				postData.put("link", "http://getsocialize.com");
				postData.put("message", text);
				
				FacebookUtils.post(PostToWallActivity.this, "me/feed", postData, new SocialNetworkPostListener() {
					
					@Override
					public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
						handleError(PostToWallActivity.this, error);
					}
					
					@Override
					public void onCancel() {
						handleCancel();
					}
					
					@Override
					public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
						handleResult(responseObject.toString());
					}
				});
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError(PostToWallActivity.this, error);
			}
		});
	}
	
	@Override
	public boolean isTextEntryRequired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "Post To My Wall";
	}
}
