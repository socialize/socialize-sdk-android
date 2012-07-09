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
package com.socialize.test.integration.sdk;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 */
public class AuthenticateSDKTest extends SDKIntegrationTest {

	public void testAuthenticate() throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		new Thread() {

			@Override
			public void run() {
				
				try {
					Socialize.getSocialize().init(context);
					
					Socialize.getSocialize().clearSessionCache(context);
					
					SocializeSession session = Socialize.getSocialize().authenticateSynchronous(context);
					
					addResult(0, session.getUser());
					
					SocializeSession session2 = Socialize.getSocialize().authenticateSynchronous(context);
					
					addResult(1, session2.getUser());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					latch.countDown();
				}
			}
			
		}.start();
		
		
		latch.await(20, TimeUnit.SECONDS);
		
		User user1 = getResult(0);
		User user2 = getResult(0);
		
		assertNotNull(user1);
		assertNotNull(user2);
		
		assertEquals(user1, user2);
	}
	
	
	public void testAuthenticateKnownUser() throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		
		final String token = TestUtils.getDummyFBToken(getContext());
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
		
				DefaultUserProviderCredentials credentials = new DefaultUserProviderCredentials();
				credentials.setAccessToken(token);
				
				FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
				fbInfo.setAppId("foobar_app_id");
				
				credentials.setAuthProviderInfo(fbInfo);
				
				Socialize.getSocialize().authenticateKnownUser(context, credentials, new SocializeAuthListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCancel() {
						latch.countDown();
					}
					
					@Override
					public void onAuthSuccess(SocializeSession session) {
						addResult(0, session);
						latch.countDown();
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		SocializeSession session = getResult(0);
		
		assertNotNull(session);
		UserProviderCredentials creds = session.getUserProviderCredentials(AuthProviderType.FACEBOOK);
		
		assertNotNull(creds);
		assertEquals(token, creds.getAccessToken());
		
		FacebookAuthProviderInfo authProviderInfo = (FacebookAuthProviderInfo) creds.getAuthProviderInfo();
		
		assertNotNull(authProviderInfo);
		assertEquals("foobar_app_id", authProviderInfo.getAppId());
	}
}
