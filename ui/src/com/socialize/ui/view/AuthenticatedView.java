package com.socialize.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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

	private String consumerKey;
	private String consumerSecret;
	private String fbAppId;
	private Dialog progress;

	public AuthenticatedView(Context context) {
		super(context);
	}

	public AuthenticatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onPostSocializeInit(IOCContainer container) {
		getSocializeUI().initUI(container);
		consumerKey = getSocializeUI().getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		consumerSecret = getSocializeUI().getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
		fbAppId = getSocializeUI().getCustomConfigValue(SocializeConfig.FACEBOOK_APP_ID);
	}

	@Override
	protected void initSocialize() {
		getSocializeUI().initSocialize(getContext());
	}

	public SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}

	public SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public SocializeAuthListener getAuthListener() {
		return new AuthenticatedViewListener(getContext(), this);
	}

	public SocializeAuthListener getAuthListener3rdParty() {
		return new AuthenticatedViewListener3rdParty(getContext(), this);
	}

	protected String getBundleValue(String key) {
		Bundle bundle = null;

		Context context = getViewContext();

		if(context instanceof Activity) {
			Activity a = (Activity) context;
			bundle = a.getIntent().getExtras();
		}

		if(bundle != null) {
			return  bundle.getString(SocializeUI.ENTITY_KEY);
		}

		return null;
	}

	// Wrapped so it can be mocked.
	protected Context getViewContext() {
		return getContext();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		String userId3rdParty = getSocializeUI().getCustomConfigValue(SocializeConfig.FACEBOOK_USER_ID);
		String token3rdParty = getSocializeUI().getCustomConfigValue(SocializeConfig.FACEBOOK_USER_TOKEN);

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

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getFbAppId() {
		return fbAppId;
	}

	// Subclasses override
	public void onBeforeAuthenticate() {}

	public abstract boolean isRequires3rdPartyAuth();

	public abstract View getView();

	@Override
	protected void onBeforeSocializeInit() {
		progress = ProgressDialog.show(getContext(), "Loading Socialize", "Please wait...");
	}

	public void onAfterAuthenticate() {
		if(progress != null) {
			progress.dismiss();
		}
	}
}
