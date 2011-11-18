package com.socialize.sample.mocks;

import android.app.Activity;

import com.socialize.ui.facebook.FacebookWallPostListener;
import com.socialize.ui.facebook.FacebookWallPoster;

public class MockFacebookWallPoster implements FacebookWallPoster {

	@Override
	public void postLike(Activity parent, String entityKey, String entityName, String comment, boolean isUseLink, FacebookWallPostListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void postComment(Activity parent, String entityKey, String entityName, String comment, boolean isUseLink, FacebookWallPostListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void post(Activity parent, String message, FacebookWallPostListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

	@Override
	public void post(Activity parent, String appId, String linkName, String message, String link, String caption, FacebookWallPostListener listener) {
		if(listener != null) {
			listener.onPost(parent);
		}
	}

}
