package com.socialize.sample.mocks;

import android.app.Activity;
import android.location.Location;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareSystem;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;

public class MockShareSystem extends MockSystem<Share> implements ShareSystem {

	public MockShareSystem() {
		super(new Share());
	}
	
	@Override
	public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void getSharesByEntity(SocializeSession session, String key, int startIndex, int endIndex, ShareListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getSharesByUser(SocializeSession session, long userId, ShareListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void shareEntity(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		if(listener != null) listener.onAfterPost(context, destination);
	}

	@Override
	public void shareComment(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		if(listener != null) listener.onAfterPost(context, destination);
	}

	@Override
	public void shareLike(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		if(listener != null) listener.onAfterPost(context, destination);
	}
}
