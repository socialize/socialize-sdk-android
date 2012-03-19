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

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.client.HttpClient;

import android.os.AsyncTask;

/**
 * @author Jason Polites
 *
 */
public class TwitterOAuthProvider extends CommonsHttpOAuthProvider {
	
	private static final long serialVersionUID = 3804709921717607933L;
	
	public static final String OAUTH_CALLBACK_URL = "socializeoauth://sign-in-with-twitter";
	public static final String REQUEST_TOKEN_ENDPOINT = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_TOKEN_ENDPOINT = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_ENDPOINT = "https://api.twitter.com/oauth/authorize";
	
	public TwitterOAuthProvider(HttpClient httpClient) {
		super(REQUEST_TOKEN_ENDPOINT, ACCESS_TOKEN_ENDPOINT, AUTHORIZE_ENDPOINT, httpClient);
	}

	public TwitterOAuthProvider() {
		super(REQUEST_TOKEN_ENDPOINT, ACCESS_TOKEN_ENDPOINT, AUTHORIZE_ENDPOINT);
	}

	public void retrieveRequestTokenAsync(final OAuthConsumer consumer, final String callbackUrl, final OAuthRequestTokenUrlListener listener) {
		
		new AsyncTask<Void, Void, Void>() {
			
			String url;
			Exception error;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					url = retrieveRequestToken(consumer, callbackUrl);
				} 
				catch (Exception e) {
					error = e;
				}
			
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(listener != null) {
					if(error != null)  {
						listener.onError(error);
					}
					else {
						listener.onRequestUrl(url);
					}
				}
			}
		}.execute();
	}
}
