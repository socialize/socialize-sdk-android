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
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.twitter.ITwitterAuthWebView;
import com.socialize.auth.twitter.TwitterAuthListener;
import com.socialize.auth.twitter.TwitterAuthView;
import com.socialize.error.SocializeException;
import com.socialize.test.SocializeUnitTest;
import com.socialize.testapp.mock.MockRelativeLayoutParams;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthViewTest extends SocializeUnitTest {

    @UsesMocks({ITwitterAuthWebView.class, MockRelativeLayoutParams.class, View.class})
	public void testInit() {
		final ITwitterAuthWebView webView = AndroidMock.createMock(ITwitterAuthWebView.class);
		final MockRelativeLayoutParams params = AndroidMock.createNiceMock(MockRelativeLayoutParams.class, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		final View view = AndroidMock.createMock(View.class, getContext());
		
		webView.init();
		webView.setLayoutParams(params);
		
		AndroidMock.expect(webView.getView()).andReturn(view);
		
		AndroidMock.replay(webView);
		
		TwitterAuthView authView = new TwitterAuthView(getContext()) {
			@Override
			protected LayoutParams newLayoutParams(int width, int height) {
				return params;
			}

			@Override
			protected ITwitterAuthWebView newTwitterAuthWebView(Context context) {
				return webView;
			}

			@Override
			public void addView(View child) {
				addResult(0, child);
			}
		};
		
		authView.init();
		
		AndroidMock.verify(webView);
		
		assertSame(view, getResult(0));
	}
	
	@UsesMocks({MockRelativeLayoutParams.class, TwitterAuthListener.class, SocializeException.class})
	public void testAuthenticate() {
		
		final MockRelativeLayoutParams params = AndroidMock.createNiceMock(MockRelativeLayoutParams.class, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		final TwitterAuthListener twitterAuthListener = AndroidMock.createMock(TwitterAuthListener.class);
		final SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		final String consumerKey = "foo";
		final String consumerSecret = "bar";
		final String token = "foobar_token";
		final String secret = "foobar_secret";
		final String screenName = "foobar_screenName";
		final String userId = "foobar_userId";
		
		twitterAuthListener.onAuthSuccess(token, secret, screenName, userId);
		twitterAuthListener.onError(error);
		twitterAuthListener.onCancel();
		
		AndroidMock.replay(twitterAuthListener);
		
		final ITwitterAuthWebView webView = new ITwitterAuthWebView() {
			
			int index = 0;
			
			@Override
			public void init() {}

			@Override
			public synchronized void authenticate(String consumerKey, String consumerSecret, TwitterAuthListener listener) {
				addResult(index++, consumerKey);
				addResult(index++, consumerSecret);
				addResult(index++, listener);
			}

			@Override
			public void setVisibility(int visibility) {
				addResult(index++, visibility);
			}

			@Override
			public void setLayoutParams(android.view.ViewGroup.LayoutParams params) {}

			@Override
			public View getView() {
				return null;
			}
		};

		TwitterAuthView authView = new TwitterAuthView(getContext()) {
			@Override
			protected LayoutParams newLayoutParams(int width, int height) {
				return params;
			}

			@Override
			protected ITwitterAuthWebView newTwitterAuthWebView(Context context) {
				return webView;
			}

			@Override
			public void addView(View child) {}
		};
		
		authView.setConsumerKey(consumerKey);
		authView.setConsumerSecret(consumerSecret);
		
		authView.setTwitterAuthListener(twitterAuthListener);
		authView.init();
		authView.authenticate();
		
		assertEquals(consumerKey, getResult(0));
		assertEquals(consumerSecret, getResult(1));
		
		TwitterAuthListener listener = getResult(2);
		
		assertNotNull(listener);
		
		listener.onError(error);
		listener.onAuthSuccess(token, secret, screenName, userId);
		listener.onCancel();
		
		AndroidMock.verify(twitterAuthListener);
		
		assertEquals(View.GONE, getResult(3));
		assertEquals(View.GONE, getResult(4));
		assertEquals(View.GONE, getResult(5));
	}
	
}
