package com.socialize.test.mock;

import android.app.Activity;

import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookWallPoster;

public class MockFacebookWallPoster implements FacebookWallPoster {
	
	
	@Override
	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK);
		}
	}

	@Override
	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK);
		}
	}

	@Override
	public void post(Activity parent, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK);
		}
	}

	@Override
	public void post(Activity parent, String appId, String linkName, String message, String link, String caption, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK);
		}
	}
}
