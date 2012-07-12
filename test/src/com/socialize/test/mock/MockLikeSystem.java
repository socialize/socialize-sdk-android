package com.socialize.test.mock;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.SocialNetwork;

public class MockLikeSystem extends MockSystem<Like> implements LikeSystem {

	public MockLikeSystem() {
		super(new Like());
	}
	
	@Override
	public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void deleteLike(SocializeSession session, long id, LikeListener listener) {
		if(listener != null) listener.onDelete();
	}

	@Override
	public void getLikesByEntity(SocializeSession session, String entityKey, LikeListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getLikesByEntity(SocializeSession session, String entityKey, int startIndex, int endIndex, LikeListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
		if(listener != null) listener.onGet(action);
	}

	@Override
	public void getLikesById(SocializeSession session, LikeListener listener, long... ids) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getLike(SocializeSession session, long id, LikeListener listener) {
		if(listener != null) listener.onGet(action);
	}

	@Override
	public void getLikesByUser(SocializeSession session, long userId, LikeListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getLikesByUser(SocializeSession session, long userId, int startIndex, int endIndex, LikeListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getLikesByApplication(SocializeSession session, int startIndex, int endIndex, LikeListener listener) {
		if(listener != null) listener.onList(actionList);	
	}
}
