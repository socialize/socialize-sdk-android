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
package com.socialize.networks.facebook;

import android.app.Activity;
import android.content.Context;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareSystem;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;


/**
 * @author Jason Polites
 *
 */
public class FacebookUtilsImpl implements FacebookUtilsProxy {
	
	private UserSystem userSystem;
	private ShareSystem shareSystem;
	private FacebookWallPoster facebookWallPoster;

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#link(android.app.Activity, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, String token, SocializeAuthListener listener) {
		SocializeConfig config = getSocialize().getConfig();
		FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
		fbInfo.setAppId(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		
		DefaultUserProviderCredentials credentials = new DefaultUserProviderCredentials();
		credentials.setAuthProviderInfo(fbInfo);
		credentials.setAccessToken(token);
		
		getSocialize().authenticateKnownUser(
				context, 
				credentials, 
				listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#unlink(android.app.Activity)
	 */
	@Override
	public void unlink(Activity context) {
		SocializeSession session = getSocialize().getSession();
		session.clear(AuthProviderType.FACEBOOK);
		userSystem.saveSession(context, session);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#isLinked(android.content.Context)
	 */
	@Override
	public boolean isLinked(Context context) {
		return getSocialize().isAuthenticated(AuthProviderType.FACEBOOK);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#isAvailable(android.content.Context)
	 */
	@Override
	public boolean isAvailable(Context context) {
		return getSocialize().isSupported(AuthProviderType.FACEBOOK);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#setAppId(android.content.Context, java.lang.String)
	 */
	@Override
	public void setAppId(Context context, String appId) {
		SocializeConfig config = getSocialize().getConfig();
		config.setFacebookAppId(appId);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#getAccessToken(android.content.Context)
	 */
	@Override
	public String getAccessToken() {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.FACEBOOK);
		if(creds != null) {
			return creds.getAccessToken();
		}
		return null;
	}

	@Override
	public void post(final Activity context, final Entity entity, final String text, final SocialNetworkListener listener) {
		shareSystem.addShare(context, getSocialize().getSession(), entity, ShareType.FACEBOOK, new ShareAddListener() {
			@Override
			public void onCreate(Share share) {
				PropagationInfo propInfo = share.getPropagationInfoResponse().getPropagationInfo(ShareType.FACEBOOK);
				facebookWallPoster.post(context, entity, text, propInfo, listener);
			}

			@Override
			public void onError(SocializeException error) {
				if(listener != null) {
					listener.onError(context, SocialNetwork.FACEBOOK, error);
				}
			}
		}, SocialNetwork.FACEBOOK);
	}

	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}

	public void setShareSystem(ShareSystem shareSystem) {
		this.shareSystem = shareSystem;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

}
