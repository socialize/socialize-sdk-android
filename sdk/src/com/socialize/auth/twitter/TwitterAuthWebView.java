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
import android.view.View;
import android.webkit.WebView;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.error.SocializeException;
import com.socialize.oauth.signpost.OAuth;
import com.socialize.oauth.signpost.OAuthTokenListener;
import com.socialize.oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import com.socialize.oauth.signpost.http.HttpParameters;

/**
 * @author Jason Polites
 */
public class TwitterAuthWebView extends WebView implements ITwitterAuthWebView {
	
	private TwitterWebViewClient twitterWebViewClient;
	private IBeanFactory<TwitterWebViewClient> twitterWebViewClientFactory;
	
	public TwitterAuthWebView(Context context) {
		super(context);
	}
	
	public void init() {
		twitterWebViewClient = newTwitterWebViewClient();
		setWebViewClient(twitterWebViewClient);
	}
	
	/**
	 * Starts the authentication process using the given twitter key/secret.
	 * @param consumerKey
	 * @param consumerSecret
	 */
	public synchronized void authenticate(final String consumerKey, final String consumerSecret, final TwitterAuthListener listener)  {
		
		TwitterOAuthProvider provider = newTwitterOAuthProvider();
		CommonsHttpOAuthConsumer consumer = newCommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		OAuthRequestListener oAuthRequestListener = newOAuthRequestListener(listener, provider, consumer);
		
		twitterWebViewClient.setOauthRequestListener(oAuthRequestListener);
		
		provider.retrieveRequestTokenAsync(consumer, TwitterOAuthProvider.OAUTH_CALLBACK_URL, newOAuthRequestTokenUrlListener(listener));
	}
	
	public void setTwitterWebViewClientFactory(IBeanFactory<TwitterWebViewClient> twitterWebViewClientFactory) {
		this.twitterWebViewClientFactory = twitterWebViewClientFactory;
	}

	protected OAuthRequestTokenUrlListener newOAuthRequestTokenUrlListener(final TwitterAuthListener listener) {
		return new OAuthRequestTokenUrlListener() {
			@Override
			public void onRequestUrl(String url) {
				loadUrl(url);
			}
			
			@Override
			public void onError(Exception e) {
				if(listener != null) {
					listener.onError(SocializeException.wrap(e));
				}					
			}
		};
	}
	
	protected OAuthRequestListener newOAuthRequestListener(final TwitterAuthListener listener, final TwitterOAuthProvider provider, final CommonsHttpOAuthConsumer consumer) {
		return new OAuthRequestListener() {
			
			@Override
			public void onCancel(String cancelToken) {
				if(listener != null) {
					listener.onCancel();
				}
			}

			@Override
			public void onRequestToken(String token, String verifier) {
				try {
					provider.retrieveAccessTokenAsync(consumer, verifier, newOAuthTokenListener(listener));
				} 
				catch (Exception e) {
					if(listener != null) {
						listener.onError(SocializeException.wrap(e));
					}
				}
			}
		};
	}
	
	protected OAuthTokenListener newOAuthTokenListener(final TwitterAuthListener listener) {
		return new OAuthTokenListener() {
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

			@Override
			public void onError(Exception e) {
				if(listener != null) {
					listener.onError(SocializeException.wrap(e));
				}				
			}
		};
	}
	
	protected TwitterWebViewClient newTwitterWebViewClient() {
		if(twitterWebViewClientFactory != null) {
			return twitterWebViewClientFactory.getBean();
		}
		return new TwitterWebViewClient();
	}
	
	protected TwitterOAuthProvider newTwitterOAuthProvider() {
		return new TwitterOAuthProvider();
	}
	
	protected CommonsHttpOAuthConsumer newCommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
		return new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
	}

	@Override
	public View getView() {
		return this;
	}
}
