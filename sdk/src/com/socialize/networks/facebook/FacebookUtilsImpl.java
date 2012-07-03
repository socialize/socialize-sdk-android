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

import java.io.IOException;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.Facebook.ServiceListener;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.ImageUtils;
import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class FacebookUtilsImpl implements FacebookUtilsProxy {
	
	private UserSystem userSystem;
	private FacebookWallPoster facebookWallPoster;
	private ImageUtils imageUtils;
	private Facebook facebook;
	private SocializeLogger logger;
	private SocializeConfig config;

	@Override
	public synchronized Facebook getFacebook(Context context) {
		if(facebook == null) {
			facebook = new Facebook(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
		}
		return facebook;
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener);
	}
	
	@Override
	public void link(Activity context, SocializeAuthListener listener, String... permissions) {
		getSocialize().authenticate(context, AuthProviderType.FACEBOOK, listener, permissions);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#link(android.app.Activity, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, String token, SocializeAuthListener listener) {
		SocializeConfig config = ConfigUtils.getConfig(context);
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
	public void unlink(Context context) {
		SocializeSession session = getSocialize().getSession();
		session.clear(AuthProviderType.FACEBOOK);
		
		UserSettings userSettings = session.getUserSettings();
		
		if(userSettings != null) {
			userSettings.setAutoPostFacebook(false);
		}
		
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
		SocializeConfig config = ConfigUtils.getConfig(context);
		config.setFacebookAppId(appId);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.facebook.FacebookUtilsProxy#getAccessToken(android.content.Context)
	 */
	@Override
	public String getAccessToken(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.FACEBOOK);
		if(creds != null) {
			return creds.getAccessToken();
		}
		return null;
	}

	@Override
	public void postEntity(final Activity context, final Entity entity, final String text, final SocialNetworkShareListener listener) {
		ShareOptions options = ShareUtils.getUserShareOptions(context);
		options.setText(text);
		options.setShowAuthDialog(false);
		ShareUtils.shareViaSocialNetworks(context, entity, options, listener, SocialNetwork.FACEBOOK);		
	}
	
	@Override
	public void extendAccessToken(final Context context) {
		
		if(isLinked(context)) {
			getFacebook(context).extendAccessTokenIfNeeded(context, new ServiceListener() {
				@Override
				public void onFacebookError(FacebookError e) {
					if(logger != null) {
						logger.warn("An error occurred while attempting to extend a Facebook access token.  The local Facebook account will be cleared.", e);
					}				
					
					// Clear the local session state
					unlink(context);
				}
				
				@Override
				public void onError(Error e) {
					if(logger != null) {
						logger.warn("An error occurred while attempting to extend a Facebook access token.  The local Facebook account will be cleared.", e);
					}
					
					// Clear the local session state
					unlink(context);
				}
				
				@Override
				public void onComplete(Bundle values) {
					// Update the local session state
					SocializeSession session = getSocialize().getSession();
					
					if(session != null) {
						
						String newAccessToken = values.getString(Facebook.TOKEN);
						
						if(!StringUtils.isEmpty(newAccessToken)) {
							
							if(logger != null && logger.isDebugEnabled()) {
								logger.debug("Got new Facebook access token [" +
										newAccessToken +
										"]");
							}
							
							UserProviderCredentialsMap map = session.getUserProviderCredentials();
							
							UserProviderCredentials creds = map.get(AuthProviderType.FACEBOOK);
							
							DefaultUserProviderCredentials newCreds = new DefaultUserProviderCredentials();
							
							if(creds != null) {
								newCreds.merge(creds);
							}
							
							newCreds.setAccessToken(newAccessToken);
							
							map.put(AuthProviderType.FACEBOOK, newCreds);
							
							getSocialize().setSession(session);
							getSocialize().saveSession(context);
						}
						else {
							if(logger != null) {
								logger.warn("Access token returned from Facebook was empty during request to extend");
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void post(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		facebookWallPoster.post(context, graphPath, config.getProperty(SocializeConfig.FACEBOOK_APP_ID), postData, listener);
	}

	@Override
	public void get(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		facebookWallPoster.get(context, graphPath, config.getProperty(SocializeConfig.FACEBOOK_APP_ID), postData, listener);
	}

	@Override
	public void delete(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		SocializeConfig config = ConfigUtils.getConfig(context);
		facebookWallPoster.delete(context, graphPath, config.getProperty(SocializeConfig.FACEBOOK_APP_ID), postData, listener);
	}

	@Override
	public byte[] getImageForPost(Activity context, Uri imagePath) throws IOException {
		return imageUtils.scaleImage(context, imagePath);
	}
	
	@Override
	public byte[] getImageForPost(Activity context, Bitmap image, CompressFormat format) throws IOException {
		return imageUtils.scaleImage(context, image, format);
	}

	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}
	
	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

}
