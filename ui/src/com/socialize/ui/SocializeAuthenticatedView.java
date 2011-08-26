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
import com.socialize.util.StringUtils;

public abstract class SocializeAuthenticatedView extends SocializeView {
	
	protected String consumerKey;
	protected String consumerSecret;
	protected String fbAppId;
	
	public SocializeAuthenticatedView(Context context) {
		super(context);
	}

	public SocializeAuthenticatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onPostSocializeInit(IOCContainer container) {
		SocializeUI.getInstance().initUI(container);
		consumerKey = SocializeUI.getInstance().getCustomConfigValue(getContext(), SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		consumerSecret = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		fbAppId = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_APP_ID);
	}
	
	@Override
	protected void initSocialize() {
		SocializeUI.getInstance().initSocialize(getContext());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		Bundle bundle = getBundle();
		String userId3rdParty = null;
		String token3rdParty = null;
		
		if(bundle != null) {
			userId3rdParty = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_USER_ID);
			token3rdParty = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_USER_TOKEN);
		}
		
		onBeforeAuthenticate();
		
		AuthListener listener = new AuthListener();
		AuthListener3rdParty listener3rdParty = new AuthListener3rdParty();
		
		if(isRequires3rdPartyAuth()) {
			
			if(!StringUtils.isEmpty(userId3rdParty) && !StringUtils.isEmpty(token3rdParty)) {
				Socialize.getSocialize().authenticateKnownUser(
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
						AuthProviderType.FACEBOOK,
						fbAppId,
						listener);
			}
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
			onAfterAuthenticate();
			showError(getContext(), error.getMessage());
			error.printStackTrace();
		}
		
		@Override
		public void onAuthSuccess(SocializeSession session) {
			// Render the childView
			onAfterAuthenticate();
			View view = getView();
			addView(view);
		}
		
		@Override
		public void onAuthFail(SocializeException error) {
			onAfterAuthenticate();
			showError(getContext(), error.getMessage());
			error.printStackTrace();
		}
	}
	
	// Subclasses override
	protected void onBeforeAuthenticate() {}
	
	// Subclasses override
	protected void onAfterAuthenticate() {}
	
	protected abstract boolean isRequires3rdPartyAuth();
	
	protected abstract View getView();
	
	protected Bundle getBundle() {
		Bundle bundle = null;
		Context context = getContext();
		if(context instanceof Activity) {
			Activity a = (Activity) context;
			bundle = a.getIntent().getExtras();
		}
		
		return bundle;
	}
}
