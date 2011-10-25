package com.socialize.auth.facebook;

import android.content.Intent;
import android.os.Bundle;

import com.socialize.config.SocializeConfig;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.util.DialogFactory;
import com.socialize.util.Drawables;

public class FacebookActivityService {

	private Facebook facebook;
	private FacebookSessionStore facebookSessionStore;
	private ListenerHolder listenerHolder;
	private FacebookActivity activity;
	private Drawables drawables;
	private DialogFactory dialogFactory;
	private SocializeConfig config;
	
	private FacebookService service;
	
	public FacebookActivityService(FacebookActivity activity) {
		super();
		this.activity = activity;
	}

	public void onCreate() {
		
		Intent intent = activity.getIntent();
		
		if(intent != null) {
			Bundle extras = intent.getExtras();
			
			if(extras != null) {
				String appId = extras.getString("appId");
				
				drawables = activity.getBean("drawables");
				facebookSessionStore = activity.getBean("facebookSessionStore");
				listenerHolder = activity.getBean("listenerHolder");
				dialogFactory = activity.getBean("dialogFactory");
				config = activity.getBean("config");
				facebook = new Facebook(appId, drawables);
				service = getFacebookService();
				
				boolean sso = config.getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true);
				
				service.authenticate(sso);
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
			service.cancel();
		}
	}
    
    public FacebookService getFacebookService() {
    	service = new FacebookService(activity, facebook, facebookSessionStore, (AuthProviderListener) listenerHolder.get("auth"), dialogFactory);
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
}
