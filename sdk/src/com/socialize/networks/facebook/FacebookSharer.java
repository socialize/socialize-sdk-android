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

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkSharer;

/**
 * @author Jason Polites
 */
public class FacebookSharer implements SocialNetworkSharer {
	
	private ShareMessageBuilder shareMessageBuilder;
	private SocializeConfig config;
	private SocializeLogger logger;
	private FacebookWallPoster facebookWallPoster;
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.SocialNetworkSharer#share(android.app.Activity, com.socialize.entity.Entity, java.lang.String, android.location.Location, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void shareEntity(final Activity context, Entity entity, String comment, SocialNetworkListener listener) {
		share(context, entity, comment, listener, ActionType.SHARE, true);
	}
	
	@Override
	public void shareComment(Activity context, Entity entity, String comment, SocialNetworkListener listener) {
		share(context, entity, comment, listener, ActionType.COMMENT, false);
	}

	@Override
	public void shareLike(Activity context, Entity entity, String comment, SocialNetworkListener listener) {
		share(context, entity, comment, listener, ActionType.LIKE, false);
	}

	protected void share(final Activity context, final Entity entity, final String comment, final SocialNetworkListener listener, final ActionType type, boolean autoAuth) {

		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			
			if(getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				doShare(context, entity, comment, listener, type);
			}
			else if(autoAuth) {
				
				String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
				String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
				String authProviderAppId = config.getProperty(SocializeConfig.FACEBOOK_APP_ID);
				
				AuthProviderType authProvider = AuthProviderType.FACEBOOK;				
				
				getSocialize().authenticate(context, consumerKey, consumerSecret, authProvider, authProviderAppId, new SocializeAuthListener() {

					@Override
					public void onError(SocializeException error) {
						doError(error, context, listener);
					}

					@Override
					public void onAuthSuccess(SocializeSession session) {
						doShare(context, entity, comment, listener, type);
					}

					@Override
					public void onAuthFail(SocializeException error) {
						doError(error, context, listener);
					}

					@Override
					public void onCancel() {
						// Do nothing
					}
				});
			}
		}	
	}
	
	protected void doError(SocializeException e, Activity parent, SocialNetworkListener listener) {
		String msg = "Error sharing to Facebook";
		
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			e.printStackTrace();
		}
		
		if(listener != null) {
			listener.onError(parent, SocialNetwork.FACEBOOK, msg, e);
		}
	}
	
	protected void doShare(final Activity context, Entity entity, String comment, final SocialNetworkListener listener, ActionType type) {
		if(listener != null) {
			listener.onBeforePost(context, SocialNetwork.FACEBOOK);
		}
		
		switch (type) {
			case COMMENT:
				facebookWallPoster.postComment(context, entity, comment, listener);
				break;
				
			case SHARE:
				String body = shareMessageBuilder.buildShareMessage( entity, comment, false, true);
				facebookWallPoster.post(context, body, listener);
				break;
				
			case LIKE:
				facebookWallPoster.postLike(context, entity, comment, listener);
				break;			
		}
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}

	// So we can mock
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

}
