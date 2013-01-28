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

import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.networks.facebook.OnPermissionResult;


/**
 * @author Jason Polites
 *
 */
public class FacebookFacadeV3 implements FacebookFacade {
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#authenticate(android.content.Context, com.socialize.auth.facebook.FacebookAuthProviderInfo, com.socialize.listener.AuthProviderListener)
	 */
	@Override
	public void authenticate(Context context, FacebookAuthProviderInfo info, AuthProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, java.lang.String, boolean, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, com.socialize.listener.SocializeAuthListener, java.lang.String[])
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener, String... permissions) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#unlink(android.content.Context, com.socialize.networks.SocializeDeAuthListener)
	 */
	@Override
	public void unlink(Context context, SocializeDeAuthListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#isLinked(android.content.Context)
	 */
	@Override
	public boolean isLinked(Context context) {
		return false;

	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#getAccessToken(android.content.Context)
	 */
	@Override
	public String getAccessToken(Context context) {
		return null;

	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#extendAccessToken(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void extendAccessToken(Activity context, SocializeAuthListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postEntity(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.share.SocialNetworkShareListener)
	 */
	@Override
	public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postLike(android.app.Activity, com.socialize.entity.Entity, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postComment(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postPhoto(android.app.Activity, com.socialize.entity.Share, java.lang.String, android.net.Uri, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postPhoto(Activity parent, Share share, String comment, Uri photoUri, SocialNetworkListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postPhoto(android.app.Activity, java.lang.String, java.lang.String, android.net.Uri, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postOG(android.app.Activity, com.socialize.entity.Entity, java.lang.String, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void postOG(Activity parent, Entity entity, String message, String action, PropagationInfo propInfo, SocialNetworkListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.entity.PropagationInfo, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, com.socialize.networks.SocialNetworkListener, com.socialize.networks.PostData)
	 */
	@Override
	public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#post(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#get(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#delete(android.app.Activity, java.lang.String, java.util.Map, com.socialize.networks.SocialNetworkPostListener)
	 */
	@Override
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#getCurrentPermissions(android.app.Activity, java.lang.String, com.socialize.networks.facebook.OnPermissionResult)
	 */
	@Override
	public void getCurrentPermissions(Activity parent, String token, OnPermissionResult callback) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#logout(android.content.Context)
	 */
	@Override
	public void logout(Context context) {
	}

}
