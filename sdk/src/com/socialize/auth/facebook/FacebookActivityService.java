package com.socialize.auth.facebook;

import android.content.Intent;
import android.os.Bundle;
import com.socialize.config.SocializeConfig;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookUtilsProxy;
import com.socialize.util.DialogFactory;

public class FacebookActivityService {

	private Facebook facebook;
	private FacebookUtilsProxy facebookUtils;
	private FacebookSessionStore facebookSessionStore;
	private ListenerHolder listenerHolder;
	private FacebookActivity activity;
	private DialogFactory dialogFactory;
	private SocializeConfig config;
	private SocializeLogger logger;
	
	private FacebookService service;
	
	public FacebookActivityService(FacebookActivity activity) {
		super();
		this.activity = activity;
	}

	public FacebookActivityService() {
		super();
	}

	public void onCreate() {
		
		Intent intent = activity.getIntent();
		
		if(intent != null) {
			Bundle extras = intent.getExtras();
			
			if(extras != null) {
				String[] permissions = extras.getStringArray("permissions");
				
				facebookSessionStore = activity.getBean("facebookSessionStore");
				listenerHolder = activity.getBean("listenerHolder");
				dialogFactory = activity.getBean("dialogFactory");
				logger = activity.getBean("logger");
				config = activity.getBean("config");
				facebookUtils = activity.getBean("facebookUtils");
				facebook = facebookUtils.getFacebook(activity);
				service = getFacebookService();
				
				boolean sso = config.getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true);
				
				if(permissions != null && permissions.length > 0) {
					service.authenticate(activity, sso, permissions);
				}
				else {
					service.authenticate(activity, sso);
				}
			}
			else {
				activity.finish();
			}
		}
		else {
			activity.finish();
		}
	}
	
	public void onCancel() {
		if(service != null) {
			service.cancel(activity);
		}
	}
    
    public FacebookService getFacebookService() {
    	service = new FacebookService(facebook, facebookSessionStore, (AuthProviderListener) listenerHolder.pop("auth"), dialogFactory, logger);
    	return service;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(facebook != null) {
			facebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
	
	public void setService(FacebookService service) {
		this.service = service;
	}

	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}
	
	public void setActivity(FacebookActivity activity) {
		this.activity = activity;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
