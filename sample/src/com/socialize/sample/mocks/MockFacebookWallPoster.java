package com.socialize.sample.mocks;

import android.app.Activity;

import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookWallPoster;

public class MockFacebookWallPoster implements FacebookWallPoster {
	
	@Override
	public void postLike(Activity parent, Entity entity, String comment, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void postComment(Activity parent, Entity entity, String comment, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void postLike(Activity parent, String entityKey, String entityName, String comment, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void postComment(Activity parent, String entityKey, String entityName, String comment, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}


	@Override
	public void post(Activity parent, String message, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void post(Activity parent, String appId, String linkName, String message, String link, String caption, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}
}
