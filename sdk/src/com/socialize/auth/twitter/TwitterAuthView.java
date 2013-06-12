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
package com.socialize.auth.twitter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.error.SocializeException;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthView extends RelativeLayout {
	
	private ITwitterAuthWebView webView;
	private String consumerKey; 
	private String consumerSecret;
	private TwitterAuthListener twitterAuthListener;
	
	private IBeanFactory<ITwitterAuthWebView> twitterAuthWebViewFactory;

	public TwitterAuthView(Context context) {
		super(context);
	}
	
	public TwitterAuthView(Context context, String consumerKey, String consumerSecret) {
		super(context);
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}
	
	public void init() {
		setBackgroundColor(Color.BLACK);
		LayoutParams params = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		webView = newTwitterAuthWebView(getContext());
		webView.init();
		webView.setLayoutParams(params);
		addView(webView.getView());
	}
	
	// So we can mock
	protected LayoutParams newLayoutParams(int width, int height) {
		return new LayoutParams(width, height);
	}
	
	// So we can mock	
	protected ITwitterAuthWebView newTwitterAuthWebView(Context context) {
		if(twitterAuthWebViewFactory != null) {
			return twitterAuthWebViewFactory.getBean();
		}
		return new TwitterAuthWebView(context);
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
	
	public void setTwitterAuthWebViewFactory(IBeanFactory<ITwitterAuthWebView> twitterAuthWebViewFactory) {
		this.twitterAuthWebViewFactory = twitterAuthWebViewFactory;
	}

	public void authenticate() {
		if(webView != null) {
			webView.authenticate(consumerKey, consumerSecret, newTwitterAuthListener());
		}
	}
	
	protected TwitterAuthListener newTwitterAuthListener() {
		return new TwitterAuthListener() {
			
			@Override
			public void onError(SocializeException e) {
				webView.setVisibility(View.GONE);
				if(twitterAuthListener != null) {
					twitterAuthListener.onError(e);
				}
			}
			
			@Override
			public void onCancel() {
				webView.setVisibility(View.GONE);
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
		};
	}
}
