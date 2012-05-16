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
package com.socialize.networks.twitter;

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
import com.socialize.auth.twitter.TwitterAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
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
public class TwitterUtilsImpl implements TwitterUtilsProxy {
	
	private UserSystem userSystem;
	private ShareSystem shareSystem;

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		getSocialize().authenticate(context, AuthProviderType.TWITTER, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#link(android.app.Activity, java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, String token, String secret, SocializeAuthListener listener) {
		
		SocializeConfig config = getSocialize().getConfig();
		TwitterAuthProviderInfo twInfo = new TwitterAuthProviderInfo();
		twInfo.setConsumerKey(config.getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
		twInfo.setConsumerSecret(config.getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
		
		DefaultUserProviderCredentials credentials = new DefaultUserProviderCredentials();
		credentials.setAuthProviderInfo(twInfo);
		credentials.setAccessToken(token);
		credentials.setTokenSecret(secret);
		
		getSocialize().authenticateKnownUser(
				context, 
				credentials, 
				listener);		
		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#unlink(android.app.Activity)
	 */
	@Override
	public void unlink(Context context) {
		SocializeSession session = getSocialize().getSession();
		session.clear(AuthProviderType.TWITTER);
		session.getUser().setAutoPostToTwitter(false);
		userSystem.saveSession(context, session);		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#isLinked(android.content.Context)
	 */
	@Override
	public boolean isLinked(Context context) {
		return getSocialize().isAuthenticated(AuthProviderType.TWITTER);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#isAvailable(android.content.Context)
	 */
	@Override
	public boolean isAvailable(Context context) {
		return getSocialize().isSupported(AuthProviderType.TWITTER);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#setCredentials(android.content.Context, java.lang.String, java.lang.String)
	 */
	@Override
	public void setCredentials(Context context, String consumerKey, String consumerSecret) {
		SocializeConfig config = getSocialize().getConfig();
		config.setTwitterKeySecret(consumerKey, consumerSecret);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#getAccessToken()
	 */
	@Override
	public String getAccessToken(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.TWITTER);
		if(creds != null) {
			return creds.getAccessToken();
		}
		return null;

	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#getTokenSecret()
	 */
	@Override
	public String getTokenSecret(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.TWITTER);
		if(creds != null) {
			return creds.getTokenSecret();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#tweet(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void tweet(final Activity context, Entity entity, String text, final SocialNetworkListener listener) {
		
		if(listener != null) {
			listener.onBeforePost(context, SocialNetwork.TWITTER, null);
		}
		
		shareSystem.addShare(context, getSocialize().getSession(), entity, text, ShareType.TWITTER, new ShareAddListener() {
			@Override
			public void onCreate(Share share) {
				// Server does tweet.
				if(listener != null) {
					listener.onAfterPost(context, SocialNetwork.TWITTER);
				}
			}

			@Override
			public void onError(SocializeException error) {
				if(listener != null) {
					listener.onError(context, SocialNetwork.TWITTER, error);
				}
			}
		}, SocialNetwork.TWITTER);
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}
	
	public void setShareSystem(ShareSystem shareSystem) {
		this.shareSystem = shareSystem;
	}
}
