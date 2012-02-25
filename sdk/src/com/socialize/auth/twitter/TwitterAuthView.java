/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.auth.twitter;

import com.socialize.error.SocializeException;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthView extends RelativeLayout {
	
	private TwitterAuthWebView webView;
	private String consumerKey; 
	private String consumerSecret;
	private TwitterAuthListener twitterAuthListener;

	public TwitterAuthView(Context context) {
		super(context);
	}
	
	public void init() {
		setBackgroundColor(Color.BLACK);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		webView = new TwitterAuthWebView(getContext());
		webView.init();
		webView.setLayoutParams(params);
		addView(webView);
	}
	
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public void setTwitterAuthListener(TwitterAuthListener twitterAuthListener) {
		this.twitterAuthListener = twitterAuthListener;
	}

	public void authenticate() {
		if(webView != null) {
			webView.authenticate(consumerKey, consumerSecret, new TwitterAuthListener() {
				
				@Override
				public void onError(SocializeException e) {
					if(twitterAuthListener != null) {
						twitterAuthListener.onError(e);
					}
				}
				
				@Override
				public void onCancel() {
					if(twitterAuthListener != null) {
						twitterAuthListener.onCancel();
					}
				}

				@Override
				public void onAuthSuccess(String token, String secret, String screenName, String userId) {
					webView.setVisibility(View.GONE);
					if(twitterAuthListener != null) {
						twitterAuthListener.onAuthSuccess(token, secret, screenName, userId);
					}
				}
			});
		}
	}

}
