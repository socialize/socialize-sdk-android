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
package com.socialize.demo.implementations.twitter;

import org.json.JSONObject;
import android.app.Activity;
import com.socialize.LocationUtils;
import com.socialize.api.SocializeSession;
import com.socialize.demo.SDKDemoActivity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.twitter.Tweet;
import com.socialize.networks.twitter.TwitterUtils;


/**
 * @author Jason Polites
 *
 */
public class TweetActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#executeDemo()
	 */
	@Override
	public void executeDemo(final String text) {
		
		TwitterUtils.link(this, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError(TweetActivity.this, error);
			}
			
			@Override
			public void onCancel() {
				handleCancel();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				
				Tweet tweet = new Tweet();
				
				tweet.setText(text);
				tweet.setLocation(LocationUtils.getLastKnownLocation(TweetActivity.this));
				tweet.setShareLocation(true);
				
				TwitterUtils.tweet(TweetActivity.this, tweet, new SocialNetworkListener() {
					
					@Override
					public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
						// Nothing to see here.. move along.
						return false;
					}

					@Override
					public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
						handleError(TweetActivity.this, error);
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
				handleError(TweetActivity.this, error);
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
		return "Tweet!";
	}
}
