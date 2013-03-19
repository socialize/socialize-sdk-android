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

import android.app.Activity;
import android.content.Context;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.networks.facebook.FacebookFacade;

/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProvider implements AuthProvider<FacebookAuthProviderInfo> {
	
	private FacebookFacade facebookFacade;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	private SocializeConfig config;
	
	@Override
	public boolean validateForRead(FacebookAuthProviderInfo info, String...permissions) {
		if(authProviderInfoBuilder != null) {
			AuthProviderInfo expected = authProviderInfoBuilder.getFactory(AuthProviderType.FACEBOOK).initInstanceForRead(permissions);
			return info.matches(expected);
		}
		// Default to true
		return true;
	}

	@Override
	public boolean validateForWrite(FacebookAuthProviderInfo info, String...permissions) {
		if(authProviderInfoBuilder != null) {
			AuthProviderInfo expected = authProviderInfoBuilder.getFactory(AuthProviderType.FACEBOOK).initInstanceForWrite(permissions);
			return info.matches(expected);
		}
		// Default to true
		return true;
	}

	@Deprecated
	@Override
	public boolean validate(FacebookAuthProviderInfo info) {
		return validateForRead(info, FacebookFacade.DEFAULT_PERMISSIONS);
	}

	@Override
	public void authenticate(Context context, FacebookAuthProviderInfo info, final AuthProviderListener listener) {
		if(context instanceof Activity) {
			final boolean sso = config.getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true);
			facebookFacade.authenticateWithActivity((Activity)context, info, sso, listener);	
		}
		else {
			if(listener != null) {
				listener.onError(new SocializeException("Facebook authentication can only be performed from an activity.  The given context [" +
						context.getClass().getName() +
						"] is not an activity"));
			}
		}
	}

	@Override
	public void clearCache(Context context, FacebookAuthProviderInfo info) {
		facebookFacade.logout(context);
	}

	public void setFacebookFacade(FacebookFacade facebookFacade) {
		this.facebookFacade = facebookFacade;
	}

	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
}
