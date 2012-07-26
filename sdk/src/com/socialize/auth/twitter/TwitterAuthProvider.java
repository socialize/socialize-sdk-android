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
package com.socialize.auth.twitter;

import android.content.Context;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthProvider implements AuthProvider<TwitterAuthProviderInfo> {
	
	private Context context;
	private TwitterAuthUtils twitterAuthUtils;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	
	public void init(Context context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProvider#authenticate(com.socialize.auth.AuthProviderInfo, com.socialize.listener.AuthProviderListener)
	 */
	@Override
	public void authenticate(TwitterAuthProviderInfo info, final AuthProviderListener listener) {
		 twitterAuthUtils.showAuthDialog(context, info, newTwitterAuthListener(listener));
	}
	
	@Override
	public boolean validate(TwitterAuthProviderInfo info) {
		if(authProviderInfoBuilder != null) {
			AuthProviderInfo expected = authProviderInfoBuilder.getFactory(AuthProviderType.TWITTER).getInstance();
			return info.matches(expected);
		}
		return true;
	}

	protected TwitterAuthListener newTwitterAuthListener(final AuthProviderListener listener) {
		return new TwitterAuthListener() {
			 
			@Override
			public void onError(SocializeException e) {
				if(listener != null) {
					listener.onError(SocializeException.wrap(e));
				}
			}
			
			@Override
			public void onCancel() {
				if(listener != null) listener.onCancel();
			}
			
			@Override
			public void onAuthSuccess(String token, String secret, String screenName, String userId) {
				if(listener != null) {
					AuthProviderResponse response = newAuthProviderResponse();
					response.setToken(token);
					response.setSecret(secret);
					response.setUserId(userId);
					listener.onAuthSuccess(response);
				}
			}
		};
	}
	
	protected AuthProviderResponse newAuthProviderResponse() {
		return new AuthProviderResponse();
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.AuthProvider#clearCache(android.content.Context, com.socialize.auth.AuthProviderInfo)
	 */
	@Override
	public void clearCache(Context context, TwitterAuthProviderInfo info) {}

	public void setTwitterAuthUtils(TwitterAuthUtils twitterUtils) {
		this.twitterAuthUtils = twitterUtils;
	}
	
	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}
}
