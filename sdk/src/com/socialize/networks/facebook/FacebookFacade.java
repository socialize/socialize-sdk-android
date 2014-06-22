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
package com.socialize.networks.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;

import java.util.Map;

/**
 * @author Jason Polites
 */
public interface FacebookFacade {
	
	public static final String[] READ_PERMISSIONS = {};
	
	public static final String[] WRITE_PERMISSIONS = {"publish_actions"};
	
	@Deprecated
	public static final String[] DEFAULT_PERMISSIONS = {"publish_stream", "publish_actions", "photo_upload"};

	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener);
	
	public void authenticateWithActivity(Activity context, FacebookAuthProviderInfo info, boolean sso, final AuthProviderListener listener);

	public void linkForRead (Activity context, SocializeAuthListener listener, String...permissions);
	
	public void linkForWrite (Activity context, SocializeAuthListener listener, String...permissions);

	public void linkForRead (Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener, String...permissions);	
	
	public void linkForWrite (Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener, String...permissions);	
	
	public void unlink (Context context, SocializeDeAuthListener listener);
	
	public boolean isLinkedForRead(Context context, String...permissions);
	
	public boolean isLinkedForWrite(Context context, String...permissions);
	
	public int getSDKMajorVersion();
	
	public String getAccessToken(Context context);
	
	public void onResume(Activity context, SocializeAuthListener listener);
	
	@Deprecated
	public void extendAccessToken(Activity context, SocializeAuthListener listener);
	
	public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener);

	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener);

	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener);	
		
	public void postOG(Activity parent, Entity entity, String message, String action, PropagationInfo propInfo, SocialNetworkListener listener);
	
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener);
	
	public void post(Activity parent, SocialNetworkListener listener, PostData postData);
	
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);

	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void getCurrentPermissions(Activity parent, String token, OnPermissionResult callback);	
	
	public void logout(Context context); 
	
	public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data);

	@Deprecated
	public void link (Activity context, SocializeAuthListener listener);

	@Deprecated
	public void link (Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener);
	
	@Deprecated
	public void link(Activity context, SocializeAuthListener listener, String...permissions);
	
	@Deprecated
	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, AuthProviderListener listener);
	
	@Deprecated
	public boolean isLinked(Context context);	
}
