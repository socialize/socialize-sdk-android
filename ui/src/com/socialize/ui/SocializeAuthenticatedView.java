package com.socialize.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;

public abstract class SocializeAuthenticatedView extends SocializeView {
	
	public static final String NAMESPACE = "http://getsocialize.com";

	protected String consumerKey;
	protected String consumerSecret;
	protected String fbAppId;
	
	public SocializeAuthenticatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onPostSocializeInit(IOCContainer container) {
		SocializeUI.getInstance().initUI(container);
		consumerKey = Socialize.getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		consumerSecret = Socialize.getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		fbAppId = Socialize.getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_FACEBOOK_APP_ID);
	}
	
	@Override
	protected void initSocialize() {
		SocializeUI.getInstance().initSocialize(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		Bundle bundle = getBundle();
		String userId3rdParty = null;
		String token3rdParty = null;
		
		if(bundle != null) {
			userId3rdParty = bundle.getString(SocializeUI.FACEBOOK_USER_ID);
			token3rdParty = bundle.getString(SocializeUI.FACEBOOK_USER_TOKEN);
		}
		
		AuthListener listener = new AuthListener();
		AuthListener3rdParty listener3rdParty = new AuthListener3rdParty();
		
		if(isRequires3rdPartyAuth()) {
			Socialize.getSocialize().authenticate(
					consumerKey, 
					consumerSecret, 
					AuthProviderType.FACEBOOK,
					fbAppId,
					userId3rdParty,
					token3rdParty,
					listener3rdParty);
		}	
		else {
			Socialize.getSocialize().authenticate(
					consumerKey, 
					consumerSecret, 
					listener);
		}
	}
	
	protected class AuthListener3rdParty extends AuthListener {
		@Override
		public void onAuthFail(SocializeException error) {
			// Assume bad token, re-auth with FB
			Socialize.getSocialize().authenticate(
					consumerKey, 
					consumerSecret, 
					AuthProviderType.FACEBOOK,
					fbAppId,
					new AuthListener());
		}
	}
	
	protected class AuthListener implements SocializeAuthListener {
		@Override
		public void onError(SocializeException error) {
			showError(context, error.getMessage());
			error.printStackTrace();
		}
		
		@Override
		public void onAuthSuccess(SocializeSession session) {
			// Render the childView
			View view = getView();
			addView(view);
		}
		
		@Override
		public void onAuthFail(SocializeException error) {
			showError(context, error.getMessage());
			error.printStackTrace();
		}
	}
	
	protected abstract boolean isRequires3rdPartyAuth();
	
	protected abstract View getView();
	
	protected Bundle getBundle() {
		Bundle bundle = null;
		
		if(context instanceof Activity) {
			Activity a = (Activity) context;
			bundle = a.getIntent().getExtras();
		}
		
		return bundle;
	}
}
