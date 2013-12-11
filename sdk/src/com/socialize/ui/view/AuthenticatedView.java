package com.socialize.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.socialize.ConfigUtils;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeListener;
import com.socialize.ui.SocializeBaseView;

public abstract class AuthenticatedView extends SocializeBaseView {

	private String consumerKey;
	private String consumerSecret;
	private String fbAppId;
	private SocializeListener onErrorListener;

	public AuthenticatedView(Context context) {
		super(context);
	}

	public AuthenticatedView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public final void onViewLoad(IOCContainer container) {
		super.onViewLoad(container);
		
		setErrorHandler((SocializeErrorHandler) container.getBean("socializeUIErrorHandler"));
		
		consumerKey = getConsumerKey(container);
		consumerSecret = getConsumerSecret(container);
		fbAppId = getFacebookAppId(container);
		
		SocializeAuthListener listener = getAuthListener(container);
		
		onBeforeAuthenticate(container);
		
		getSocialize().authenticate(
                container.getContext(),
				consumerKey, 
				consumerSecret, 
				listener);
	}
	
	@Override
	public void onViewUpdate(IOCContainer container) {
		super.onViewUpdate(container);
		
		if(container != null) {
			setErrorHandler((SocializeErrorHandler) container.getBean("socializeUIErrorHandler"));
		}
		
		// Make sure we notify after authenticate to dismiss any pending dialogs.
		onAfterAuthenticate(container);
	}
	
	protected String getConsumerKey(IOCContainer container) {
		return ConfigUtils.getConfig(getContext()).getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
	}
	
	protected String getConsumerSecret(IOCContainer container) {
		return ConfigUtils.getConfig(getContext()).getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
	}
	
	protected String getFacebookAppId(IOCContainer container) {
		return ConfigUtils.getConfig(getContext()).getProperty(SocializeConfig.FACEBOOK_APP_ID);
	}

	public SocializeAuthListener getAuthListener(IOCContainer container) {
		return new AuthenticatedViewListener(this, container);
	}

	// Wrapped so it can be mocked.
	protected Context getViewContext() {
		return getContext();
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
	
	public void setOnErrorListener(SocializeListener onErrorListener) {
		this.onErrorListener = onErrorListener;
	}
	
	public SocializeListener getOnErrorListener() {
		return onErrorListener;
	}

	// Subclasses override
	public void onBeforeAuthenticate(IOCContainer container) {}
	public void onAfterAuthenticate(IOCContainer container) {}
	
	public abstract View getView();

}
