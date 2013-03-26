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
package com.socialize.test.twitter;

import android.content.Context;
import android.webkit.WebViewClient;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.twitter.*;
import com.socialize.error.SocializeException;
import com.socialize.oauth.signpost.OAuth;
import com.socialize.oauth.signpost.OAuthTokenListener;
import com.socialize.oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import com.socialize.oauth.signpost.exception.OAuthCommunicationException;
import com.socialize.oauth.signpost.exception.OAuthExpectationFailedException;
import com.socialize.oauth.signpost.exception.OAuthMessageSignerException;
import com.socialize.oauth.signpost.exception.OAuthNotAuthorizedException;
import com.socialize.oauth.signpost.http.HttpParameters;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthWebViewTest extends SocializeUnitTest {

	@UsesMocks ({
		CommonsHttpOAuthConsumer.class, 
		TwitterOAuthProvider.class, 
		TwitterWebViewClient.class, 
		OAuthRequestListener.class, 
		TwitterAuthListener.class,
		OAuthRequestTokenUrlListener.class})
	public void testAuthenticate() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		
		final CommonsHttpOAuthConsumer commonsHttpOAuthConsumer = AndroidMock.createMock(CommonsHttpOAuthConsumer.class, consumerKey, consumerSecret);
		final TwitterOAuthProvider twitterOAuthProvider = AndroidMock.createMock(TwitterOAuthProvider.class);
		final TwitterWebViewClient twitterWebViewClient = AndroidMock.createMock(TwitterWebViewClient.class);
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final OAuthRequestListener oAuthRequestListener = AndroidMock.createMock(OAuthRequestListener.class, listener, twitterOAuthProvider, commonsHttpOAuthConsumer);
		final OAuthRequestTokenUrlListener oAuthRequestTokenUrlListener = AndroidMock.createMock(OAuthRequestTokenUrlListener.class);
		
		// Expect
		twitterWebViewClient.setOauthRequestListener(oAuthRequestListener);
		twitterOAuthProvider.retrieveRequestTokenAsync(commonsHttpOAuthConsumer, TwitterOAuthProvider.OAUTH_CALLBACK_URL, oAuthRequestTokenUrlListener);
		
		AndroidMock.replay(twitterWebViewClient, twitterOAuthProvider);
		
		TwitterAuthWebView webView = new TwitterAuthWebView(getContext()) {

			@Override
			public TwitterWebViewClient newTwitterWebViewClient() {
				return twitterWebViewClient;
			}

			@Override
			public TwitterOAuthProvider newTwitterOAuthProvider() {
				return twitterOAuthProvider;
			}

			@Override
			public CommonsHttpOAuthConsumer newCommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
				return commonsHttpOAuthConsumer;
			}

			@Override
			protected OAuthRequestListener newOAuthRequestListener(TwitterAuthListener listener, TwitterOAuthProvider provider, CommonsHttpOAuthConsumer consumer) {
				return oAuthRequestListener;
			}
			
			@Override
			protected OAuthRequestTokenUrlListener newOAuthRequestTokenUrlListener(TwitterAuthListener listener) {
				return oAuthRequestTokenUrlListener;
			}

			@Override
			public void setWebViewClient(WebViewClient client) {
				addResult(0, client);
			}
		};
		
		webView.init();
		webView.authenticate(consumerKey, consumerSecret, listener);
		
		AndroidMock.verify(twitterWebViewClient, twitterOAuthProvider);
		
		assertSame(twitterWebViewClient, getResult(0));
	}
	
	@UsesMocks ({
		CommonsHttpOAuthConsumer.class, 
		TwitterOAuthProvider.class,
		TwitterAuthListener.class,
		OAuthTokenListener.class})
	public void testOAuthRequestListener() throws Exception {
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String verifier = "foobar_verifier";
		final String cancelToken = "foobar_cancelToken";
		final String token = "foobar_token";
		
		final CommonsHttpOAuthConsumer consumer = AndroidMock.createMock(CommonsHttpOAuthConsumer.class, consumerKey, consumerSecret);
		final TwitterOAuthProvider provider = AndroidMock.createMock(TwitterOAuthProvider.class);
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final OAuthTokenListener tokenListener = AndroidMock.createMock(OAuthTokenListener.class);
		
		
		listener.onCancel();
		provider.retrieveAccessTokenAsync(consumer, verifier, tokenListener);
		
		AndroidMock.replay(listener, provider);
		
		PublicTwitterAuthWebView webView = new PublicTwitterAuthWebView(getContext()) {

			@Override
			public OAuthTokenListener newOAuthTokenListener(TwitterAuthListener listener) {
				addResult(0, listener);
				return tokenListener;
			}
		};
		
		OAuthRequestListener oAuthRequestListener = webView.newOAuthRequestListener(listener, provider, consumer);
		
		oAuthRequestListener.onCancel(cancelToken);
		oAuthRequestListener.onRequestToken(token, verifier);
		
		AndroidMock.verify(listener, provider);
		
		assertSame(listener, getResult(0));
	}
	
	@UsesMocks({HttpParameters.class, TwitterAuthListener.class})
	public void testOAuthTokenListener() {
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final HttpParameters parameters = AndroidMock.createMock(HttpParameters.class);
		
		String token = "foobar_token";
		String secret = "foobar_secret";
		String screenName = "foobar_screenName";
		String userId = "foobar_userId";
		
		AndroidMock.expect(parameters.getFirst(OAuth.OAUTH_TOKEN)).andReturn(token);
		AndroidMock.expect(parameters.getFirst(OAuth.OAUTH_TOKEN_SECRET)).andReturn(secret);
		AndroidMock.expect(parameters.getFirst("user_id")).andReturn(userId);
		AndroidMock.expect(parameters.getFirst("screen_name")).andReturn(screenName);
		
		listener.onAuthSuccess(token, secret, screenName, userId);
		
		AndroidMock.replay(parameters, listener);
		
		PublicTwitterAuthWebView webView = new PublicTwitterAuthWebView(getContext());
		OAuthTokenListener tokenListener = webView.newOAuthTokenListener(listener);
		
		tokenListener.onResponse(parameters);
		
		AndroidMock.verify(parameters, listener);
		
	}
	
	@UsesMocks ({TwitterAuthListener.class})
	public void testOAuthRequestTokenUrlListener() {
		final TwitterAuthListener listener = AndroidMock.createMock(TwitterAuthListener.class);
		final SocializeException e = new SocializeException();
		final String url = "foobar";
		
		listener.onError(e);
		
		AndroidMock.replay(listener);
		
		PublicTwitterAuthWebView authWebView = new PublicTwitterAuthWebView(getContext()) {
			@Override
			public void loadUrl(String url) {
				addResult(0, url);
			}
		};
		
		OAuthRequestTokenUrlListener newOAuthRequestTokenUrlListener = authWebView.newOAuthRequestTokenUrlListener(listener);
		
		newOAuthRequestTokenUrlListener.onError(e);
		newOAuthRequestTokenUrlListener.onRequestUrl(url);
		
		AndroidMock.verify(listener);
		
		assertEquals(url, getNextResult());
	}
	
	class PublicTwitterAuthWebView extends TwitterAuthWebView {

		public PublicTwitterAuthWebView(Context context) {
			super(context);
		}

		@Override
		public OAuthRequestListener newOAuthRequestListener(TwitterAuthListener listener, TwitterOAuthProvider provider, CommonsHttpOAuthConsumer consumer) {
			return super.newOAuthRequestListener(listener, provider, consumer);
		}

		@Override
		public OAuthTokenListener newOAuthTokenListener(TwitterAuthListener listener) {
			return super.newOAuthTokenListener(listener);
		}

		@Override
		public TwitterWebViewClient newTwitterWebViewClient() {
			return super.newTwitterWebViewClient();
		}

		@Override
		public TwitterOAuthProvider newTwitterOAuthProvider() {
			return super.newTwitterOAuthProvider();
		}

		@Override
		public CommonsHttpOAuthConsumer newCommonsHttpOAuthConsumer(String consumerKey, String consumerSecret) {
			return super.newCommonsHttpOAuthConsumer(consumerKey, consumerSecret);
		}

		@Override
		public OAuthRequestTokenUrlListener newOAuthRequestTokenUrlListener(TwitterAuthListener listener) {
			return super.newOAuthRequestTokenUrlListener(listener);
		}
	}
	

	
}
