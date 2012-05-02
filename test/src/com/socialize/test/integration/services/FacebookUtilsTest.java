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
package com.socialize.test.integration.services;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.facebook.FacebookAuthProvider;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.error.SocializeException;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class FacebookUtilsTest extends SocializeActivityTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Socialize.getSocialize().destroy(true);
	}

	@Override
	protected void tearDown() throws Exception {
		Socialize.getSocialize().destroy(true);
		super.tearDown();
	}
	
	public void test_link () throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getActivity();
		
		// Stub in the FacebookAuthProvider
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				addResult(0, info);
				latch.countDown();
			}
		};
		
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);
		
		// Set a mock FB ID
		FacebookUtils.setAppId(getActivity(), "foobar");
		
		// Ensure we don't have a session
		FacebookUtils.unlink(getActivity());
		
		// Validate
		assertFalse(FacebookUtils.isLinked(getContext()));
		
		// Now Link
		FacebookUtils.link(context, null);
		
		latch.await(20, TimeUnit.SECONDS);
		
		FacebookAuthProviderInfo data = getResult(0);
		
		assertNotNull(data);
		assertEquals(AuthProviderType.FACEBOOK, data.getType());
		assertEquals("foobar", data.getAppId());
		
		SocializeIOC.unregisterStub("facebookProvider");
	}
	
	public void test_link_with_token () throws InterruptedException {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getActivity();
		
		// Stub in the FacebookAuthProvider to ensure we DON'T auth with FB
		FacebookAuthProvider mockFacebookAuthProvider = new FacebookAuthProvider() {
			@Override
			public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
				fail();
			}
		};
		
		SocializeIOC.registerStub("facebookProvider", mockFacebookAuthProvider);
		
		// Set a mock FB ID
		FacebookUtils.setAppId(getActivity(), "foobar");
		
		FacebookUtils.link(context, TestUtils.DUMMY_FB_TOKEN, new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				addResult(0, session);
				latch.countDown();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				fail();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		SocializeSession session = getResult(0);
		
		assertNotNull(session);
		
		UserProviderCredentials userProviderCredentials = session.getUserProviderCredentials(AuthProviderType.FACEBOOK);
		
		assertNotNull(userProviderCredentials);
		assertEquals(TestUtils.DUMMY_FB_TOKEN, userProviderCredentials.getAccessToken());
		assertTrue(FacebookUtils.isLinked(context));
		assertEquals(TestUtils.DUMMY_FB_TOKEN, FacebookUtils.getAccessToken(context));
		
		SocializeIOC.unregisterStub("facebookProvider");
	}
	
	public void test_unlink () {}
	public void test_isLinked() {}
	public void test_isAvailable() {
		
		Socialize.getSocialize().init(getContext());
		
		Socialize.getSocialize().getConfig().setFacebookAppId("foobar");
		
		assertTrue(FacebookUtils.isAvailable(getContext()));
		
		Socialize.getSocialize().getConfig().setFacebookAppId(null);
		
		assertFalse(FacebookUtils.isAvailable(getContext()));
		
	}
	public void test_setAppId() {}
	public void test_getAccessToken() {}
	public void test_post() {}
	
}
