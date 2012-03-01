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

import oauth.signpost.OAuth;
import oauth.signpost.OAuthTokenListener;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpParameters;
import android.content.Context;
import android.webkit.WebView;

/**
 * @author Jason Polites
 */
public class TwitterAuthWebView extends WebView {
	
	private TwitterWebViewClient twitterWebViewClient;
	
	public TwitterAuthWebView(Context context) {
		super(context);
	}
	
	public void init() {
		twitterWebViewClient = newTwitterWebViewClient();
		setWebViewClient(twitterWebViewClient);
	}
	
	protected TwitterWebViewClient newTwitterWebViewClient() {
		return new TwitterWebViewClient();
	}

	/**
	 * Starts the authentication process using the given twitter key/secret.
	 * @param consumerKey
	 * @param consumerSecret
	 */
	public synchronized void authenticate(String consumerKey, String consumerSecret, final TwitterAuthListener listener)  {
		
		final TwitterOAuthProvider provider = new TwitterOAuthProvider();
		final CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		
		twitterWebViewClient.setOauthRequestListener(new OAuthRequestListener() {
			
			@Override
			public void onCancel(String cancelToken) {
				if(listener != null) {
					listener.onCancel();
				}
			}

			@Override
			public void onRequestToken(String token, String verifier) {
				try {
					provider.retrieveAccessToken(consumer, verifier, new OAuthTokenListener() {
						@Override
						public void onResponse(HttpParameters parameters) {
							String token = parameters.getFirst(OAuth.OAUTH_TOKEN);
							String secret = parameters.getFirst(OAuth.OAUTH_TOKEN_SECRET);
							
							// See https://dev.twitter.com/docs/auth/implementing-sign-twitter
							String userId = parameters.getFirst("user_id");
							String screenName = parameters.getFirst("screen_name");
							
							if(listener != null) {
								listener.onAuthSuccess(token, secret, screenName, userId);
							}
						}
					});
				} 
				catch (Exception e) {
					if(listener != null) {
						listener.onError(SocializeException.wrap(e));
					}
				}
			}
		});
		
		try {
			String retrieveRequestToken = provider.retrieveRequestToken(consumer, TwitterOAuthProvider.OAUTH_CALLBACK_URL);
			loadUrl(retrieveRequestToken);
		} 
		catch (Exception e) {
			if(listener != null) {
				listener.onError(SocializeException.wrap(e));
			}
		}
	}
}
