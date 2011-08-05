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
package com.socialize.test.unit;

import android.app.Activity;

import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.listener.AuthProviderListener;
import com.socialize.test.SocializeActivityTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	Activity.class, 
	Facebook.class, 
	FacebookSessionStore.class, 
	AuthProviderListener.class})
public class FacebookServiceTest extends SocializeActivityTest {

	public void testAuthenticate() {
		
		final String appId = "foobar";
		final String[] permissions = new String[]{};
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		
		// Expect
		AndroidMock.expect(facebookSessionStore.restore(facebook, context)).andReturn(true);
		facebook.authorize(AndroidMock.eq(context), AndroidMock.eq(permissions), (DialogListener) AndroidMock.anyObject());
		
		AndroidMock.replay(facebookSessionStore);
		AndroidMock.replay(facebook);
		
		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener);
		service.authenticate(permissions);
		
		AndroidMock.verify(facebookSessionStore);
		AndroidMock.verify(facebook);
	}
	
	public void testFinishCallsFinishOnActivity() {
		
		final String appId = "foobar";
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		
		// Expect
		context.finish();
		
		AndroidMock.replay(context);
		
		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener);
		service.finish();
		
		AndroidMock.verify(context);
	}
	
	public void testLogoutCallsLogoutOnFacebook() throws Exception {
		
		final String appId = "foobar";
		
		Activity context = AndroidMock.createMock(Activity.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		AuthProviderListener authProviderListener = AndroidMock.createMock(AuthProviderListener.class);
		
		// Expect
		AndroidMock.expect(facebook.logout(context)).andReturn(appId);
		
		AndroidMock.replay(facebook);
		
		FacebookService service = new FacebookService(context, facebook, facebookSessionStore, authProviderListener);
		service.logout();
		
		AndroidMock.verify(facebook);
	}
	
	public void testAuthenticateCallsAuthenticateWithEmptyPermissions() {
		FacebookService service = new FacebookService(null, null, null, null) {
			@Override
			public void authenticate(String[] permissions) {
				assertNotNull(permissions);
				assertTrue(permissions.length == 0);
				addResult(true);
			}
		};
		
		service.authenticate();
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
}
