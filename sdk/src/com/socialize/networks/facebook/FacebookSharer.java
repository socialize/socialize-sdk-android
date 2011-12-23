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
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkSharer;
import com.socialize.ui.share.ShareMessageBuilder;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 */
public class FacebookSharer implements SocialNetworkSharer {
	
	private ShareMessageBuilder shareMessageBuilder;
	private SocializeConfig config;
	private DeviceUtils deviceUtils;
	private SocializeLogger logger;
	private FacebookWallPoster facebookWallPoster;
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.SocialNetworkSharer#share(android.app.Activity, com.socialize.entity.Entity, java.lang.String, android.location.Location, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void share(final Activity context, Entity entity, String comment, final SocialNetworkListener listener) {
		
		if(getSocialize().isSupported(AuthProviderType.FACEBOOK)) {
			
			final String body = shareMessageBuilder.buildShareMessage( entity, comment, false, true);
			
			if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				doShare(context, body, listener);
			}
			else {
				
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
						doShare(context, body, listener);
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
	
	protected void doShare(Activity context, String body, SocialNetworkListener listener) {
		
		if(listener != null) {
			listener.onBeforePost(context, SocialNetwork.FACEBOOK);
		}
		
		// We're not going to use the default share text
		String caption = "Download the app now to join the conversation.";
		String linkName = deviceUtils.getAppName();
		String link = deviceUtils.getMarketUrl(false);
		String appId = getSocialize().getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID);
		facebookWallPoster.post(context, appId, linkName, body, link, caption, listener);	
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
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
