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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.facebook.AsyncFacebookRunner;
import com.socialize.facebook.AsyncFacebookRunner.RequestListener;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class FacebookShareClickListener extends ShareClickListener {

	private DeviceUtils deviceUtils;
	private Drawables drawables;
	private SocializeConfig config;
	private ProgressDialogFactory progressDialogFactory;
	
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
						doError(error);
					}

					@Override
					public void onAuthSuccess(SocializeSession session) {
						doShareFB(parent, title, subject, body, comment);
					}

					@Override
					public void onAuthFail(SocializeException error) {
						doError(error);
					}

					@Override
					public void onCancel() {
						// Do nothing
					}
				});
			}
		}
	}
	
	protected void doShareFB(Activity parent, String title, String subject, String body, String comment) {
		
		final ProgressDialog dialog = progressDialogFactory.show(parent, "Share", "Sharing to Facebook...");
		
		String appId = getSocializeUI().getCustomConfigValue(SocializeConfig.FACEBOOK_APP_ID);
		
		subject = getSubject(parent);
		body = getContent(parent, comment);

		Bundle params = new Bundle();
		params.putString("name", subject);
		params.putString("message", body);
		params.putString("link", deviceUtils.getMarketUrl(false));
		params.putString("caption", deviceUtils.getAppName());
		
		Facebook fb = new Facebook(appId, drawables);
		
		FacebookSessionStore store = new FacebookSessionStore();
		store.restore(fb, parent);
		
		AsyncFacebookRunner runner = new AsyncFacebookRunner(fb);
		
		runner.request("me/feed", params, "POST", new RequestListener() {
			public void onMalformedURLException(MalformedURLException e, Object state) {
				dialog.dismiss();
				doError(e);
			}
			public void onIOException(IOException e, Object state) {
				dialog.dismiss();
				doError(e);
			}
			public void onFileNotFoundException(final FileNotFoundException e, Object state) {
				dialog.dismiss();
				doError(e);
			}
			public void onFacebookError(FacebookError e, Object state) {
				dialog.dismiss();
				doError(e);
			}
			public void onComplete(final String response, Object state) {
				// TODO: add success dialog
				dialog.dismiss();
			}
		}, null);	
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
	
	protected void doError(Throwable e) {
		// TODO: Add error dialog
		e.printStackTrace();
	}
	
	@Override
	protected boolean isHtml() {
		return false;
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

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}
}
