package com.socialize.test.mock;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;

public class MockShareSystem extends MockSystem<Share> implements ShareSystem {

	public MockShareSystem() {
		super(new Share());
	}
	
	@Override
	public boolean canShare(Context context, ShareType shareType) {
		return true;
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
	public void share(Activity context, SocializeSession session, SocializeAction action, String comment, Location location, ShareType destination, SocialNetworkListener listener) {
		if(listener != null) listener.onAfterPost(context, SocialNetwork.valueOf(destination), null);
	}

	@Override
	public void getSharesByUser(SocializeSession session, long userId, int startIndex, int endIndex, ShareListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getSharesById(SocializeSession session, ShareListener listener, long... ids) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getShare(SocializeSession session, long id, ShareListener listener) {
		if(listener != null) listener.onGet(action);
	}
	
	@Override
	public void addShare(Context context, SocializeSession session, Entity entityKey, String text, ShareType shareType, Location location, ShareListener listener) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void addShare(Context context, SocializeSession session, Entity entityKey, String text, SocialNetwork network, Location location, ShareListener listener) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void addShare(Context context, SocializeSession session, Entity entityKey, String text, ShareType shareType, ShareListener listener, SocialNetwork... network) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void addShare(Context context, SocializeSession session, Entity entityKey, ShareType shareType, ShareListener listener, SocialNetwork... network) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void getSharesByApplication(SocializeSession session, int startIndex, int endIndex, ShareListener listener) {
		if(listener != null) listener.onList(actionList);
	}
}
