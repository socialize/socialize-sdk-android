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
package com.socialize.auth.facebook;

import android.content.Context;
import android.content.Intent;

import com.socialize.api.SocializeAuthRequest;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProvider implements AuthProvider {
	
	private Context context;
	
	public FacebookAuthProvider(Context context) {
		super();
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProvider#authenticate()
	 */
	@Override
	public void authenticate(SocializeAuthRequest authRequest, String appId, final AuthProviderListener listener) {
		
		final String listenerKey = "auth";
		
		ListenerHolder.getInstance().put(listenerKey, new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				ListenerHolder.getInstance().remove(listenerKey);
				listener.onError(error);
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				ListenerHolder.getInstance().remove(listenerKey);
				listener.onAuthSuccess(response);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				ListenerHolder.getInstance().remove(listenerKey);
				listener.onAuthFail(error);
			}
		});
		
		Intent i = new Intent(context, FacebookActivity.class);
		i.putExtra("appId", appId);
		context.startActivity(i);
	}
}
