package com.socialize.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeView;

public abstract class AuthenticatedView extends SocializeView {

	private String consumerKey;
	private String consumerSecret;
	private String fbAppId;

	public AuthenticatedView(Context context) {
		super(context);
	}

	public AuthenticatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onPostSocializeInit(IOCContainer container) {
		getSocializeUI().initUI(container);
		consumerKey = getConsumerKey(container);
		consumerSecret = getConsumerSecret(container);
		fbAppId = getFacebookAppId(container);
	}
	
	protected String getConsumerKey(IOCContainer container) {
		return getSocializeUI().getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
	}
	
	protected String getConsumerSecret(IOCContainer container) {
		return getSocializeUI().getCustomConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
	}
	
	protected String getFacebookAppId(IOCContainer container) {
		return getSocializeUI().getCustomConfigValue(SocializeConfig.FACEBOOK_APP_ID);
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

	@Deprecated
	public SocializeAuthListener getAuthListener3rdParty() {
		return new AuthenticatedViewListener3rdParty(getContext(), this);
	}

//	protected String getBundleValue(String key) {
//		Bundle bundle = null;
//
//		Context context = getViewContext();
//
//		if(context instanceof Activity) {
//			Activity a = (Activity) context;
//			bundle = a.getIntent().getExtras();
//		}
//
//		if(bundle != null) {
//			return  bundle.getString(SocializeUI.ENTITY_KEY);
//		}
//
//		return null;
//	}

	// Wrapped so it can be mocked.
	protected Context getViewContext() {
		return getContext();
	}

	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		SocializeAuthListener listener = getAuthListener();
		onBeforeAuthenticate();
		getSocialize().authenticate(
				consumerKey, 
				consumerSecret, 
				listener);
	}

	@Override
	protected void onViewUpdate() {
		super.onViewUpdate();
		
		// Make sure we notify after authenticate
		onAfterAuthenticate();
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
	public void onAfterAuthenticate() {}

	public abstract View getView();

}
