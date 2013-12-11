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
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthWebViewTest extends SocializeUnitTest {

	public void testAuthenticate() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		
		final CommonsHttpOAuthConsumer commonsHttpOAuthConsumer = Mockito.mock(CommonsHttpOAuthConsumer.class);
		final TwitterOAuthProvider twitterOAuthProvider = Mockito.mock(TwitterOAuthProvider.class);
		final TwitterWebViewClient twitterWebViewClient = Mockito.mock(TwitterWebViewClient.class);
		final TwitterAuthListener listener = Mockito.mock(TwitterAuthListener.class);
		final OAuthRequestListener oAuthRequestListener = Mockito.mock(OAuthRequestListener.class);
		final OAuthRequestTokenUrlListener oAuthRequestTokenUrlListener = Mockito.mock(OAuthRequestTokenUrlListener.class);
		
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

        // Expect
        Mockito.verify(twitterWebViewClient).setOauthRequestListener(oAuthRequestListener);
        Mockito.verify(twitterOAuthProvider).retrieveRequestTokenAsync(commonsHttpOAuthConsumer, TwitterOAuthProvider.OAUTH_CALLBACK_URL, oAuthRequestTokenUrlListener);

		assertSame(twitterWebViewClient, getResult(0));
	}
	
	public void testOAuthRequestListener() throws Exception {
		
		final String verifier = "foobar_verifier";
		final String cancelToken = "foobar_cancelToken";
		final String token = "foobar_token";
		
		final CommonsHttpOAuthConsumer consumer = Mockito.mock(CommonsHttpOAuthConsumer.class);
		final TwitterOAuthProvider provider = Mockito.mock(TwitterOAuthProvider.class);
		final TwitterAuthListener listener = Mockito.mock(TwitterAuthListener.class);
		final OAuthTokenListener tokenListener = Mockito.mock(OAuthTokenListener.class);
		
		
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

        Mockito.verify(listener).onCancel();
        Mockito.verify(provider).retrieveAccessTokenAsync(consumer, verifier, tokenListener);
		
		assertSame(listener, getResult(0));
	}
	
	public void testOAuthTokenListener() {
		final TwitterAuthListener listener = Mockito.mock(TwitterAuthListener.class);
		final HttpParameters parameters = Mockito.mock(HttpParameters.class);
		
		String token = "foobar_token";
		String secret = "foobar_secret";
		String screenName = "foobar_screenName";
		String userId = "foobar_userId";
		
		Mockito.when(parameters.getFirst(OAuth.OAUTH_TOKEN)).thenReturn(token);
		Mockito.when(parameters.getFirst(OAuth.OAUTH_TOKEN_SECRET)).thenReturn(secret);
		Mockito.when(parameters.getFirst("user_id")).thenReturn(userId);
		Mockito.when(parameters.getFirst("screen_name")).thenReturn(screenName);
		

		
		PublicTwitterAuthWebView webView = new PublicTwitterAuthWebView(getContext());
		OAuthTokenListener tokenListener = webView.newOAuthTokenListener(listener);
		
		tokenListener.onResponse(parameters);

		Mockito.verify(listener).onAuthSuccess(token, secret, screenName, userId);
		
	}
	
	public void testOAuthRequestTokenUrlListener() {
		final TwitterAuthListener listener = Mockito.mock(TwitterAuthListener.class);
		final SocializeException e = new SocializeException();
		final String url = "foobar";
		
		PublicTwitterAuthWebView authWebView = new PublicTwitterAuthWebView(getContext()) {
			@Override
			public void loadUrl(String url) {
				addResult(0, url);
			}
		};
		
		OAuthRequestTokenUrlListener newOAuthRequestTokenUrlListener = authWebView.newOAuthRequestTokenUrlListener(listener);
		
		newOAuthRequestTokenUrlListener.onError(e);
		newOAuthRequestTokenUrlListener.onRequestUrl(url);
		
		Mockito.verify(listener).onError(e);
		
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
