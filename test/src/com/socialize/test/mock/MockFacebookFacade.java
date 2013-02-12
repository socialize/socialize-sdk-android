/*
 * Copyright (c) 2013 Socialize Inc.
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
package com.socialize.test.mock;

import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.networks.facebook.OnPermissionResult;


/**
 * @author Jason Polites
 *
 */
public class MockFacebookFacade implements FacebookFacade {

	private SocializeConfig config;
	
	private String userId;
	private String token;
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}	
	
	public void init() {
		userId = config.getProperty("facebook.user.id");
		token = config.getProperty("facebook.token");
	}
	
	@Override
	public int getSDKMajorVersion() {
		return 2;
	}

	@Override
	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, AuthProviderListener listener) {
		AuthProviderResponse mockResponse = new AuthProviderResponse();
		mockResponse.setUserId(userId);
		mockResponse.setToken(token);
		listener.onAuthSuccess(mockResponse);
		
	}

	@Override
	public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {
		
	}

	@Override
	public void authenticateWithActivity(Activity context, FacebookAuthProviderInfo info, boolean sso, AuthProviderListener listener) {
		AuthProviderResponse mockResponse = new AuthProviderResponse();
		mockResponse.setUserId(userId);
		mockResponse.setToken(token);
		listener.onAuthSuccess(mockResponse);
	}

	@Override
	public void authenticate(Activity context, String appId, String[] permissions, boolean sso, boolean read, AuthProviderListener listener) {
		AuthProviderResponse mockResponse = new AuthProviderResponse();
		mockResponse.setUserId(userId);
		mockResponse.setToken(token);
		listener.onAuthSuccess(mockResponse);
	}

	@Override
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postOG(Activity parent, Entity entity, String message, String action, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

//	@Override
//	public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener) {
//		if(listener != null) {
//			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
//		}
//	}
//
//	@Override
//	public void postPhoto(Activity parent, Share share, String comment, Uri photoUri, SocialNetworkListener listener) {
//		if(listener != null) {
//			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
//		}
//	}

	@Override
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}	
	
	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		if(listener != null) {
			listener.onAuthSuccess(new MockSocializeSession());
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, java.lang.String, boolean, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener) {
		link(context, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#link(android.app.Activity, com.socialize.listener.SocializeAuthListener, java.lang.String[])
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener, String... permissions) {
		link(context, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#unlink(android.content.Context, com.socialize.networks.SocializeDeAuthListener)
	 */
	@Override
	public void unlink(Context context, SocializeDeAuthListener listener) {
		if(listener != null) {
			listener.onSuccess();
		}
	}
	
	

	@Override
	public void linkForRead(Activity context, SocializeAuthListener listener, String... permissions) {
		if(listener != null) {
			listener.onAuthSuccess(new MockSocializeSession());
		}
	}

	@Override
	public void linkForWrite(Activity context, SocializeAuthListener listener, String... permissions) {
		if(listener != null) {
			listener.onAuthSuccess(new MockSocializeSession());
		}
	}

	@Override
	public void linkForRead(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener, String... permissions) {
		if(listener != null) {
			listener.onAuthSuccess(new MockSocializeSession());
		}
	}

	@Override
	public void linkForWrite(Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener, String... permissions) {
		if(listener != null) {
			listener.onAuthSuccess(new MockSocializeSession());
		}
	}

	@Override
	public boolean isLinkedForRead(Context context, String... permissions) {
		return false;
	}

	@Override
	public boolean isLinkedForWrite(Context context, String... permissions) {
		return false;
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
		return "MOCK_TOKEN";
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#extendAccessToken(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void extendAccessToken(Activity context, SocializeAuthListener listener) {
		if(listener != null) {
			listener.onAuthSuccess(new MockSocializeSession());
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#postEntity(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.share.SocialNetworkShareListener)
	 */
	@Override
	public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#getCurrentPermissions(android.app.Activity, java.lang.String, com.socialize.networks.facebook.OnPermissionResult)
	 */
	@Override
	public void getCurrentPermissions(Activity parent, String token, OnPermissionResult callback) {
		if(callback != null) {
			callback.onSuccess(DEFAULT_PERMISSIONS);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookFacade#logout(android.content.Context)
	 */
	@Override
	public void logout(Context context) {}

	@Override
	public void onResume(Activity context, SocializeAuthListener listener) {
		
	}
	
	

}
