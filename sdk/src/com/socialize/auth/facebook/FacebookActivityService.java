package com.socialize.auth.facebook;

import android.content.Intent;
import android.os.Bundle;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookFacade;

public class FacebookActivityService {

	private FacebookFacade facebookFacade;
	private ListenerHolder listenerHolder;
	private FacebookActivity activity;
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
				
				listenerHolder = activity.getBean("listenerHolder");
				logger = activity.getBean("logger");
				config = activity.getBean("config");
				facebookFacade = activity.getBean("facebookFacadeFactory");
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
    	service = new FacebookService(config.getProperty(SocializeConfig.FACEBOOK_APP_ID), facebookFacade, (AuthProviderListener) listenerHolder.pop("auth"), logger);
    	return service;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(facebookFacade != null) {
			facebookFacade.onActivityResult(this.activity, requestCode, resultCode, data);
		}
	}
	
	public void setService(FacebookService service) {
		this.service = service;
	}
	
	public void setActivity(FacebookActivity activity) {
		this.activity = activity;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setFacebookFacade(FacebookFacade facebookFacade) {
		this.facebookFacade = facebookFacade;
	}
}
