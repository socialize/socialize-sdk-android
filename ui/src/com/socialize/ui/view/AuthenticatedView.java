package com.socialize.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeView;
import com.socialize.util.StringUtils;

public abstract class AuthenticatedView extends SocializeView {
	
	protected String consumerKey;
	protected String consumerSecret;
	protected String fbAppId;
	
	public AuthenticatedView(Context context) {
		super(context);
	}

	public AuthenticatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void onPostSocializeInit(IOCContainer container) {
		getSocializeUI().initUI(container);
		consumerKey = getSocializeUI().getCustomConfigValue(getContext(), SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		consumerSecret = getSocializeUI().getCustomConfigValue(getContext(),SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		fbAppId = getSocializeUI().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_APP_ID);
	}
	
	@Override
	protected void initSocialize() {
		getSocializeUI().initSocialize(getContext());
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected SocializeAuthListener getAuthListener() {
		return new AuthenticatedViewListener(getContext(), this);
	}
	
	protected SocializeAuthListener getAuthListener3rdParty() {
		return new AuthenticatedViewListener3rdParty(getContext(), this);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		String userId3rdParty = getSocializeUI().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_USER_ID);
		String token3rdParty = getSocializeUI().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_USER_TOKEN);
		
//		Bundle bundle = getBundle();
//		if(bundle != null) {
//			userId3rdParty = getSocializeUI().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_USER_ID);
//			token3rdParty = getSocializeUI().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_USER_TOKEN);
//		}
		
		onBeforeAuthenticate();
		
		SocializeAuthListener listener = getAuthListener();
		SocializeAuthListener listener3rdParty = getAuthListener3rdParty();
		
		if(isRequires3rdPartyAuth()) {
			
			if(!StringUtils.isEmpty(userId3rdParty) && !StringUtils.isEmpty(token3rdParty)) {
				getSocialize().authenticateKnownUser(
						consumerKey, 
						consumerSecret, 
						AuthProviderType.FACEBOOK,
						fbAppId,
						userId3rdParty,
						token3rdParty,
						listener3rdParty);
			}
			else {
				getSocialize().authenticate(
						consumerKey, 
						consumerSecret, 
						AuthProviderType.FACEBOOK,
						fbAppId,
						listener);
			}
		}	
		else {
			getSocialize().authenticate(
					consumerKey, 
					consumerSecret, 
					listener);
		}
	}
	
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public void setFbAppId(String fbAppId) {
		this.fbAppId = fbAppId;
	}

	// Subclasses override
	protected void onBeforeAuthenticate() {}
	
	// Subclasses override
	protected void onAfterAuthenticate() {}
	
	protected abstract boolean isRequires3rdPartyAuth();
	
	protected abstract View getView();
	
	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
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
