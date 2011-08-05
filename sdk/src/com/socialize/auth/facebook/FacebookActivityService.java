package com.socialize.auth.facebook;

import android.content.Intent;
import android.os.Bundle;

import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.util.DialogFactory;
import com.socialize.util.Drawables;

public class FacebookActivityService {

	private Facebook mFacebook;
	private FacebookSessionStore facebookSessionStore;
	private ListenerHolder listenerHolder;
	private FacebookActivity activity;
	private Drawables drawables;
	private DialogFactory dialogFactory;
	
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
				
				mFacebook = new Facebook(appId, drawables);
				
				FacebookService service = getFacebookService();
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
    
    public FacebookService getFacebookService() {
    	return new FacebookService(activity, mFacebook, facebookSessionStore, (AuthProviderListener) listenerHolder.get("auth"), dialogFactory);
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(mFacebook != null) {
			mFacebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
}
