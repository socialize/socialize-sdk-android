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
package com.socialize.ui.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.dialog.AlertDialogFactory;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.facebook.FacebookWallPostListener;
import com.socialize.ui.facebook.FacebookWallPoster;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class FacebookShareClickListener extends ShareClickListener {

	private DeviceUtils deviceUtils;
	private SocializeConfig config;
	private ProgressDialogFactory progressDialogFactory;
	private AlertDialogFactory alertDialogFactory;
	private SocializeLogger logger;
	private FacebookWallPoster facebookWallPoster;
	
	@Override
	protected void doShare(final Activity parent, final String title, final String subject, final String body, final String comment) {

		if(getSocializeUI().isFacebookSupported()) {
			
			if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
				doShareFB(parent, title, subject, body, comment);
			}
			else {
				
				String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
				String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
				String authProviderAppId = config.getProperty(SocializeConfig.FACEBOOK_APP_ID);
				
				AuthProviderType authProvider = AuthProviderType.FACEBOOK;				
				
				getSocialize().authenticate(consumerKey, consumerSecret, authProvider, authProviderAppId, new SocializeAuthListener() {

					@Override
					public void onError(SocializeException error) {
						doError(error, parent);
					}

					@Override
					public void onAuthSuccess(SocializeSession session) {
						doShareFB(parent, title, subject, body, comment);
					}

					@Override
					public void onAuthFail(SocializeException error) {
						doError(error, parent);
					}

					@Override
					public void onCancel() {
						// Do nothing
					}
				});
			}
		}
	}
	
	/**
	 * @param error
	 * @param parent
	 */
	protected void doError(SocializeException e, Activity parent) {
		if(logger != null) {
			logger.error("Error sharing to Facebook", e);
		}
		else {
			e.printStackTrace();
		}
		alertDialogFactory.show(parent, "Error", "Share failed.  Please try again");
	}

	protected void doShareFB(final Activity parent, String title, String subject, String body, String comment) {
		
		// We're not going to use the default share text
		String caption = "Download the app now to join the conversation.";
		String linkName = deviceUtils.getAppName();
		String link = deviceUtils.getMarketUrl(false);
		
		final ProgressDialog dialog = progressDialogFactory.show(parent, "Share", "Sharing to Facebook...");
		
		String appId = getSocializeUI().getCustomConfigValue(SocializeConfig.FACEBOOK_APP_ID);
		
		facebookWallPoster.post(parent, appId, linkName, body, link, caption, new FacebookWallPostListener() {
			@Override
			public void onPost(Activity parent) {
				dialog.dismiss();
				alertDialogFactory.show(parent, "Success", "Share successful!");
			}
			
			@Override
			public void onError(Activity parent, String message, Throwable error) {
				dialog.dismiss();
				alertDialogFactory.show(parent, "Error", "Share failed.  Please try again");
			}
		});
		
	}
	
	protected String getSubject(Activity activity) {
		Intent intent = activity.getIntent();
		Bundle extras = intent.getExtras();
		
		String entityKey = null;
		String entityName = null;
		
		if(extras != null) {
			entityKey = extras.getString(SocializeUI.ENTITY_KEY);
			entityName = extras.getString(SocializeUI.ENTITY_NAME);
		}
		
		if(!StringUtils.isEmpty(entityName)) {
			return entityName;
		}
		else {
			return entityKey;
		}
	}
	
	protected String getContent(Activity activity, String comment) {
		StringBuilder builder = new StringBuilder();
		
		if(!StringUtils.isEmpty(comment)) {
			builder.append(comment);
			builder.append("\n\n");
		}
		
		builder.append("Shared using Socialize for Android.  http://www.getsocialize.com");
		
		return builder.toString();
	}
	
	
	@Override
	protected boolean isHtml() {
		return false;
	}
	
	@Override
	protected boolean isIncludeSocialize() {
		return true;
	}
	
	@Override
	protected ShareType getShareType() {
		return ShareType.FACEBOOK;
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setAlertDialogFactory(AlertDialogFactory alertDialogFactory) {
		this.alertDialogFactory = alertDialogFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}
	
	
}
