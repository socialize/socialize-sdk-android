package com.socialize.auth.facebook;

import android.content.Intent;
import android.os.Bundle;

import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.util.DialogFactory;
import com.socialize.util.Drawables;
import com.socialize.util.IOUtils;

public class FacebookActivityService {

	private Facebook mFacebook;
	private FacebookSessionStore facebookSessionStore;
	private ListenerHolder listenerHolder;
	private FacebookActivity activity;
	private Drawables drawables;
	private DialogFactory dialogFactory;
	private IOUtils ioUtils;
	
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
				ioUtils = activity.getBean("ioUtils");
				mFacebook = new Facebook(appId, drawables);
				
				service = getFacebookService();
				service.authenticate();
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
    	FacebookService service = new FacebookService(activity, mFacebook, facebookSessionStore, (AuthProviderListener) listenerHolder.get("auth"), dialogFactory);
    	service.setIoUtils(ioUtils);
    	return service;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(mFacebook != null) {
			mFacebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
}
