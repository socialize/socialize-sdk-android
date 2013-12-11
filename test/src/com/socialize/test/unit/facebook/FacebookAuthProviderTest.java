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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.SocializeListener;
import com.socialize.networks.facebook.v2.FacebookFacadeV2;
import com.socialize.test.SocializeUnitTest;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProviderTest extends SocializeUnitTest {

	public void testAuthenticate() {
		
		final String appId = "foobar";
		Activity context = Mockito.mock(Activity.class);
		AuthProviderListener listener = Mockito.mock(AuthProviderListener.class);
		SocializeException error = Mockito.mock(SocializeException.class);
		AuthProviderResponse response = Mockito.mock(AuthProviderResponse.class);
		
		ListenerHolder holder = new ListenerHolder() {

			@Override
			public void push(String key, SocializeListener listener) {
				addResult(listener);
			}
			
			@Override
			public void remove(String key) {
				addResult(key);
			}

			@Override
			public <L extends SocializeListener> L pop(String key) {
				return null;
			}
		};
		

		FacebookFacadeV2 provider = new FacebookFacadeV2();
		provider.setHolder(holder);
		
		FacebookAuthProviderInfo fb = new FacebookAuthProviderInfo();
		fb.setAppId(appId);
		
		provider.authenticateWithActivity(context, fb, true, listener);
		
		// We should have a listener from the put method
		AuthProviderListener wrapper = getNextResult();
		
		assertNotNull(wrapper);
		
		// Test the listener
		wrapper.onError(error);
		String key = getNextResult();
		assertNotNull(key);
		assertEquals("auth", key);
		
		wrapper.onAuthSuccess(response);
		key = getNextResult();
		assertNotNull(key);
		assertEquals("auth", key);
		
		wrapper.onAuthFail(error);
		key = getNextResult();
		assertNotNull(key);
		assertEquals("auth", key);


        Mockito.verify(context).startActivity((Intent)Mockito.anyObject());
        Mockito.verify(listener).onError(error);
        Mockito.verify(listener).onAuthSuccess(response);
        Mockito.verify(listener).onAuthFail(error);
    }
	
	@Deprecated
	public void testClearCache() throws MalformedURLException, IOException {
		
		final String appId = "foobar";
		
		final Facebook facebook = Mockito.mock(Facebook.class, "foobar");
		
		Mockito.when(facebook.logout(getContext())).thenReturn(null);
		
		FacebookFacadeV2 facade = new FacebookFacadeV2() {
			@Override
			protected Facebook getFacebook(Context context) {
				return facebook;
			}
		};
		
		FacebookAuthProviderInfo fb = new FacebookAuthProviderInfo();
		fb.setAppId(appId);
		
		facade.logout(getContext());
    }
	
}
