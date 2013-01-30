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
package com.socialize.test.unit.facebook;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookDialogListener;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.error.SocializeException;
import com.socialize.facebook.DialogError;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.AuthProviderListener;
import com.socialize.test.SocializeActivityTest;

/**
 * @author Jason Polites
 *
 */
@Deprecated
@UsesMocks ({
	Activity.class, 
	Facebook.class, 
	FacebookSessionStore.class, 
	AuthProviderListener.class})
public class FacebookDialogListenerTest extends SocializeActivityTest {

	
	public void testOnCompleteSuccess() throws Throwable {
		
		final String appId = "foobar";
		final String id = "foo";
		final String token = "bar";
		final String json = "{id:'" + id + "'}";
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		
		// Can't mock bundle, so create one with the data we expect.
		final Bundle bundle = new Bundle();
		bundle.putString("access_token", token);
		
		AuthProviderListener listener = new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				assertNotNull(response);
				assertEquals(token, response.getToken());
				assertEquals(id, response.getUserId());
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				error.printStackTrace();
				fail();
			}

			@Override
			public void onCancel() {
				fail();
			}
		};
		
		AndroidMock.expect(facebookSessionStore.save(facebook, context)).andReturn(true);
		AndroidMock.expect(facebook.request("me")).andReturn(json);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final FacebookDialogListener dListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				addResult(true);
				latch.countDown();
			}
			
			@Override
			public void handleError(Throwable error) {
				fail();
			}
		};
		
		AndroidMock.replay(facebookSessionStore);
		AndroidMock.replay(facebook);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				dListener.onComplete(bundle);
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		AndroidMock.verify(facebookSessionStore);
		AndroidMock.verify(facebook);
		
		Boolean result = getNextResult();
		assertNotNull(result);
		assertTrue(result);
	}
	
	
	public void testOnCompleteFail() throws Throwable {
		
		final String appId = "foobar";
		final String token = "bar";
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		
		// Can't mock bundle, so create one with the data we expect.
		final Bundle bundle = new Bundle();
		bundle.putString("access_token", token);
		
		final String errorMessage = "foobar";
		final IOException mockError = new IOException(errorMessage);
		
		AuthProviderListener listener = new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				assertNotNull(error);
				assertNotNull(error.getCause());
				assertEquals(errorMessage, error.getCause().getMessage());
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				fail();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();
			}
		};
		
		AndroidMock.expect(facebookSessionStore.save(facebook, context)).andReturn(true);
		AndroidMock.expect(facebook.request("me")).andThrow(mockError);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final FacebookDialogListener dListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				addResult(true);
				latch.countDown();
			}
			
			@Override
			public void handleError(Throwable error) {
				fail();
			}
		};
		
		AndroidMock.replay(facebookSessionStore);
		AndroidMock.replay(facebook);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				dListener.onComplete(bundle);
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		AndroidMock.verify(facebookSessionStore);
		AndroidMock.verify(facebook);
		
		Boolean result = getNextResult();
		assertNull(result);
	}
	
	public void testOnFacebookError() throws Exception {
	
		final String appId = "foobar";
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);

		final String errorMessage = "foobar";
		final FacebookError error = new FacebookError(errorMessage);
		
		AuthProviderListener listener = new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				addResult(0, true);
				assertNotNull(error);
				assertNotNull(error.getCause());
				assertEquals(errorMessage, error.getCause().getMessage());
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				fail();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();
			}
		};
		
		FacebookDialogListener dListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				addResult(1, true);
			}
			
			@Override
			public void handleError(Throwable error) {
				fail();
			}
		};
		
		dListener.onFacebookError(error);
		
		Boolean result0 = getResult(0);
		Boolean result1 = getResult(1);
		
		assertNotNull(result0);
		assertNull(result1);
		
		assertTrue(result0);
	}
	
	
	public void testOnError() throws Exception {
		
		final String appId = "foobar";
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);

		final String errorMessage = "foobar";
		final DialogError error = new DialogError(errorMessage, -1, null);
		
		AuthProviderListener listener = new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				addResult(0, true);
				assertNotNull(error);
				assertNotNull(error.getCause());
				assertEquals(errorMessage, error.getCause().getMessage());
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				fail();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				fail();
			}
			
			@Override
			public void onCancel() {
				fail();
			}
		};
		
		FacebookDialogListener dListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				addResult(1, true);
			}
			
			@Override
			public void handleError(Throwable error) {
				fail();
			}
		};
		
		dListener.onError(error);
		
		Boolean result0 = getResult(0);
		Boolean result1 = getResult(1);
		
		assertNotNull(result0);
		assertNull(result1);
		
		assertTrue(result0);
	}
}
