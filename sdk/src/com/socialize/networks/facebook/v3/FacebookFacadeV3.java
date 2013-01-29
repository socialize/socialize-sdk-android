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
package com.socialize.networks.facebook.v3;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.BaseFacebookFacade;
import com.socialize.networks.facebook.OnPermissionResult;


/**
 * @author Jason Polites
 *
 */
public class FacebookFacadeV3 extends BaseFacebookFacade {

	@Override
	public void authenticate(Context context, FacebookAuthProviderInfo info, AuthProviderListener listener) {
	}

	@Override
	public void extendAccessToken(Activity context, SocializeAuthListener listener) {
	}

	@Override
	public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener) {
	}

	@Override
	public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
	}

	@Override
	public void getCurrentPermissions(Activity parent, String token, OnPermissionResult callback) {
	}

	@Override
	public void logout(Context context) {
	}

	@Override
	protected void doFacebookCall(Activity parent, Bundle data, String graphPath, String method, SocialNetworkPostListener listener) {
	}
	
}
