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
package com.socialize.auth.facebook;

import android.content.Context;
import android.content.Intent;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookUtilsProxy;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProvider implements AuthProvider<FacebookAuthProviderInfo> {
	
	private Context context;
	private ListenerHolder holder; // This is a singleton
	private SocializeLogger logger;
	private FacebookSessionStore facebookSessionStore;
	private FacebookUtilsProxy facebookUtils;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	
	public FacebookAuthProvider() {
		super();
	}

	public void init(Context context, ListenerHolder holder) {
		this.context = context;
		this.holder = holder;
	}
	
	@Override
	public boolean validate(FacebookAuthProviderInfo info) {
		if(authProviderInfoBuilder != null) {
			AuthProviderInfo expected = authProviderInfoBuilder.getFactory(AuthProviderType.FACEBOOK).getInstance(FacebookService.DEFAULT_PERMISSIONS);
			return info.matches(expected);
		}
		// Default to true
		return true;
	}

	@Override
	public void authenticate(FacebookAuthProviderInfo info, final AuthProviderListener listener) {

		final String listenerKey = "auth";
		
		holder.push(listenerKey, new AuthProviderListener() {
			
			@Override
			public void onError(SocializeException error) {
				holder.remove(listenerKey);
				listener.onError(error);
			}
			
			@Override
			public void onAuthSuccess(AuthProviderResponse response) {
				holder.remove(listenerKey);
				listener.onAuthSuccess(response);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				holder.remove(listenerKey);
				listener.onAuthFail(error);
			}

			@Override
			public void onCancel() {
				holder.remove(listenerKey);
				listener.onCancel();
			}
		});
		
		Intent i = new Intent(context, FacebookActivity.class);
		i.putExtra("appId", info.getAppId());
		
		if(info.getPermissions() != null) {
			i.putExtra("permissions", info.getPermissions());
		}
		
		context.startActivity(i);		
	}

	@Override
	public void clearCache(Context context, FacebookAuthProviderInfo info) {
		Facebook mFacebook = getFacebook(context);
		
		try {
			if(mFacebook != null) {
				mFacebook.logout(context);
			}
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Failed to log out of Facebook", e);
			}
			else {
				SocializeLogger.e("Failed to log out of Facebook", e);
			}
		}
		finally {
			if(facebookSessionStore != null) {
				facebookSessionStore.clear(context);
			}
		}
	}
	
	protected Facebook getFacebook(Context context) {
		return facebookUtils.getFacebook(context);
	}
	
	public void setFacebookUtils(FacebookUtilsProxy facebookUtils) {
		this.facebookUtils = facebookUtils;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setFacebookSessionStore(FacebookSessionStore facebookSessionStore) {
		this.facebookSessionStore = facebookSessionStore;
	}

	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}
}
