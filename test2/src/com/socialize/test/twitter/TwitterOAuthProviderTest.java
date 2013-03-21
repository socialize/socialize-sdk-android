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

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.twitter.OAuthRequestTokenUrlListener;
import com.socialize.auth.twitter.TwitterOAuthProvider;
import com.socialize.oauth.signpost.OAuthConsumer;
import com.socialize.oauth.signpost.exception.OAuthCommunicationException;
import com.socialize.oauth.signpost.exception.OAuthExpectationFailedException;
import com.socialize.oauth.signpost.exception.OAuthMessageSignerException;
import com.socialize.oauth.signpost.exception.OAuthNotAuthorizedException;
import com.socialize.test.SocializeActivityTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 *
 */
public class TwitterOAuthProviderTest extends SocializeActivityTest {

	@UsesMocks ({OAuthConsumer.class})
	public void test_retrieveRequestTokenAsync() throws Throwable {
		
		final String url = "foobar";
		final String callbackUrl = "foobar_callback";
		final OAuthConsumer consumer = AndroidMock.createMock(OAuthConsumer.class);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		@SuppressWarnings("serial")
		final TwitterOAuthProvider provider = new TwitterOAuthProvider() {
			@Override
			public String retrieveRequestToken(OAuthConsumer consumer, String callbackUrl) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
				addResult(0, consumer);
				addResult(1, callbackUrl);
				return url;
			}
		};
		
		final OAuthRequestTokenUrlListener listener = new OAuthRequestTokenUrlListener() {
			
			@Override
			public void onRequestUrl(String url) {
				addResult(2, url);
				latch.countDown();
			}
			
			@Override
			public void onError(Exception e) {
				fail();
			}
		};
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				provider.retrieveRequestTokenAsync(consumer, callbackUrl, listener);
			}
		});
		
		
		latch.await(10, TimeUnit.SECONDS);
		
		OAuthConsumer consumerAfter = getResult(0);
		String callbackUrlAfter = getResult(1);
		String urlAfter = getResult(2);
		
		assertNotNull(consumerAfter);
		assertNotNull(callbackUrlAfter);
		assertNotNull(urlAfter);
		
		assertSame(consumer, consumerAfter);
		assertEquals(callbackUrl, callbackUrlAfter);
		assertEquals(url, urlAfter);
	}
}
