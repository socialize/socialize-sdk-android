package com.socialize.test.mock;

import java.util.Map;
import android.app.Activity;
import android.net.Uri;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookPermissionCallback;
import com.socialize.networks.facebook.FacebookWallPoster;

public class MockFacebookWallPoster implements FacebookWallPoster {
	
	@Override
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postOG(Activity parent, Entity entity, String message, String action, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void postPhoto(Activity parent, Share share, String comment, Uri photoUri, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
		
	}

	@Override
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void post(Activity parent, SocialNetworkListener listener, PostData postData) {
		if(listener != null) {
			listener.onAfterPost(parent, SocialNetwork.FACEBOOK, null);
		}
	}

	@Override
	public void getCurrentPermissions(Activity parent, String token, FacebookPermissionCallback callback) {
		if(callback != null) {
			callback.onSuccess(FacebookService.DEFAULT_PERMISSIONS);
		}
	}
}
