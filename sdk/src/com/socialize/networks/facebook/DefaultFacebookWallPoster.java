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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.FacebookError;
import com.socialize.facebook.RequestListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.util.AppUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * Posts to the Facebook wall.
 * @author Jason Polites
 */
public class DefaultFacebookWallPoster implements FacebookWallPoster {
	
	private Drawables drawables;
	private SocializeLogger logger;
	private AppUtils appUtils;
	private ShareMessageBuilder shareMessageBuilder;
	private SocializeConfig config;
	
	@Override
	public void postLike(Activity parent, Entity entity, String comment, SocialNetworkListener listener) {

		String linkName = appUtils.getAppName();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Likes ");
		builder.append(shareMessageBuilder.getEntityLink(entity, false));
		builder.append("\n\n");
		builder.append("Posted from ");
		builder.append(linkName);
		
		if(config.isBrandingEnabled()) {
			builder.append(" using Socialize for Android. http://www.getsocialize.com");
		}
		
		
		post(parent, builder.toString(), listener);		
	}

	@Override
	public void postComment(Activity parent, Entity entity, String comment, SocialNetworkListener listener) {
		String linkName = appUtils.getAppName();
		
		StringBuilder builder = new StringBuilder();
			
		builder.append(shareMessageBuilder.getEntityLink(entity, false));
		builder.append("\n\n");
		builder.append(comment);
		builder.append("\n\n");
		builder.append("Posted from ");
		builder.append(linkName);
		
		if(config.isBrandingEnabled()) {
			builder.append(" using Socialize for Android. http://www.getsocialize.com");
		}
		
		post(parent, builder.toString(), listener);		
	}

	@Deprecated
	@Override
	public void postLike(final Activity parent, String entityKey, String entityName, String comment, SocialNetworkListener listener) {
		postLike(parent, Entity.newInstance(entityKey, entityName), comment, listener);
	}
	
	@Override
	public void postComment(final Activity parent, String entityKey, String entityName, String comment, SocialNetworkListener listener) {
		postComment(parent, Entity.newInstance(entityKey, entityName), comment, listener);
	}

	@Override
	public void post(final Activity parent, String message, final SocialNetworkListener listener) {
		String caption = "Download the app now to join the conversation.";
		String linkName = appUtils.getAppName();
		String link = appUtils.getAppUrl();
		String appId = getSocialize().getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID);
		
		if(!StringUtils.isEmpty(appId)) {
			post(parent, appId, linkName, message, link, caption, listener);
		}
		else {
			String msg = "Cannot post message to Facebook.  No app id found.  Make sure you specify facebook.app.id in socialize.properties";
			onError(parent, msg, new SocializeException(msg), listener);
		}
	}
	
	@Override
	public void post(final Activity parent, String appId, String linkName, String message, String link, String caption, final SocialNetworkListener listener) {
		Bundle params = new Bundle();
		params.putString("name", linkName);
		params.putString("message", message);
		params.putString("link", link);
		params.putString("caption", caption);
		
		Facebook fb = newFacebook(appId, drawables);
		
		final FacebookSessionStore store = newFacebookSessionStore();
		
		store.restore(fb, parent);
		
		AsyncFacebookRunner runner = newAsyncFacebookRunner(fb);
		
		RequestListener requestListener = newRequestListener(parent, listener);
		
		runner.request("me/feed", params, "POST", requestListener, null);	
	}
	
	// So we can mock
	protected Facebook newFacebook(String appId, Drawables drawables) {
		return new Facebook(appId, drawables);
	}
	
	// So we can mock
	protected RequestListener newRequestListener(final Activity parent, final SocialNetworkListener listener) {
		final String defaultErrorMessage = "Facebook Error";
		
		return new RequestListener() {
			public void onMalformedURLException(MalformedURLException e, Object state) {
				onError(parent, defaultErrorMessage, e, listener);
			}
			public void onIOException(IOException e, Object state) {
				onError(parent, defaultErrorMessage, e, listener);
			}
			public void onFileNotFoundException(final FileNotFoundException e, Object state) {
				onError(parent, defaultErrorMessage, e, listener);
			}
			public void onFacebookError(FacebookError e, Object state) {
				onError(parent, defaultErrorMessage, e, listener);
			}
			public void onComplete(final String response, Object state) {
				if(!StringUtils.isEmpty(response)) {
					try {
						JSONObject responseObject = newJSONObject(response);
						
						if(responseObject.has("error")) {
							
							// Clear the session cache
							getSocialize().clear3rdPartySession(parent, AuthProviderType.FACEBOOK);
							
							JSONObject error = responseObject.getJSONObject("error");
							
							if(error.has("message") && !error.isNull("message")) {
								String msg = error.getString("message");
								if(logger != null) {
									logger.error(msg);
								}
								else {
									System.err.println(msg);
								}
								
								onError(parent, msg, new SocializeException(msg), listener);
							}
							else {
								onError(parent, defaultErrorMessage, new SocializeException("Facebook Error (Unknown)"), listener);
							}
							
							return;
						}
					}
					catch (JSONException e) {
						onError(parent, defaultErrorMessage, e, listener);
						return;
					}
				}
				
				if(listener != null) {
					parent.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							listener.onAfterPost(parent, SocialNetwork.FACEBOOK);
						}
					});
				}
			}
		};
	}
	
	protected JSONObject newJSONObject(String response) throws JSONException {
		return new JSONObject(response);
	}
	
	// So we can mock
	protected AsyncFacebookRunner newAsyncFacebookRunner(Facebook fb) {
		return new AsyncFacebookRunner(fb);
	}

	// So we can mock
	protected FacebookSessionStore newFacebookSessionStore() {
		return new FacebookSessionStore();
	}
	
	// So we can mock
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	protected void onError(final Activity parent, final String msg, final Throwable e, final SocialNetworkListener listener) {
		
		if(logger != null) {
			if(e != null) {
				logger.error(msg, e);
			}
			else {
				logger.error(msg);
			}
		}
		else {
			System.err.println(msg);
			if(e != null) {
				e.printStackTrace();
			}
		}
		
		if(listener != null) {
			parent.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					listener.onError(parent, SocialNetwork.FACEBOOK, msg, e);
				}
			});
		}
	}
}
