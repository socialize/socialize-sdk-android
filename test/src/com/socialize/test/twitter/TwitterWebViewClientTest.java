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

import android.test.UiThreadTest;
import android.webkit.WebView;
import com.socialize.auth.twitter.OAuthRequestListener;
import com.socialize.auth.twitter.TwitterOAuthProvider;
import com.socialize.auth.twitter.TwitterWebViewClient;
import com.socialize.test.SocializeActivityTest;
import org.mockito.Mockito;


/**
 * @author Jason Polites
 *
 */
public class TwitterWebViewClientTest extends SocializeActivityTest {

    @UiThreadTest
	public void testOnPageStarted() {
		
		OAuthRequestListener listener = Mockito.mock(OAuthRequestListener.class);
		
		// Can't mock :/
		WebView view = new WebView(getContext()) {
			@Override
			public void stopLoading() {
                // Do nothing
			}
		};
		
		String url = TwitterOAuthProvider.OAUTH_CALLBACK_URL + "?oauth_token=foo&oauth_verifier=bar";
		
		TwitterWebViewClient client = new TwitterWebViewClient();
		client.setOauthRequestListener(listener);
		client.onPageStarted(view, url, null);

        Mockito.verify( listener ).onRequestToken("foo", "bar");
    }
	
}
