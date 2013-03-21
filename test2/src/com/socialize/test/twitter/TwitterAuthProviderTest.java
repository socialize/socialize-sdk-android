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
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.twitter.TwitterAuthListener;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
public class TwitterAuthProviderTest extends SocializeUnitTest {

	@UsesMocks ({AuthProviderResponse.class, AuthProviderListener.class})
	public void test_newTwitterAuthListener() {
		final AuthProviderResponse response = AndroidMock.createMock(AuthProviderResponse.class);
		AuthProviderListener listener = AndroidMock.createMock(AuthProviderListener.class);
		
		String token = "a";
		String secret = "b";
		String userId = "c";
		String screenName = "d";
		
		SocializeException e = new SocializeException();
		
		response.setToken(token);
		response.setSecret(secret);
		response.setUserId(userId);
		
		listener.onAuthSuccess(response);
		listener.onCancel();
		listener.onError(e);
		
		AndroidMock.replay(listener, response);
		
		PublicTwitterAuthProvider provider = new PublicTwitterAuthProvider() {
			@Override
			public AuthProviderResponse newAuthProviderResponse() {
				return response;
			}
		};

		TwitterAuthListener newTwitterAuthListener = provider.newTwitterAuthListener(listener);
		
		newTwitterAuthListener.onAuthSuccess(token, secret, screenName, userId);
		newTwitterAuthListener.onCancel();
		newTwitterAuthListener.onError(e);
		
		AndroidMock.verify(listener, response);
	}
}
