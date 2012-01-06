package com.socialize.sample.mocks;

import android.location.Location;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.CommentSystem;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.listener.comment.CommentListener;
import com.socialize.networks.ShareOptions;

public class MockCommentSystem extends MockSystem<Comment> implements CommentSystem {
	
	public MockCommentSystem() {
		super(new Comment());
	}

	@Override
	public void addComment(SocializeSession session, Comment comment, Location location, ShareOptions shareOptions, CommentListener listener) {
		if(listener != null) listener.onCreate(comment);
	}

	@Override
	public void addComment(SocializeSession session, Entity entity, String text, Location location, ShareOptions shareOptions, CommentListener listener) {
		if(listener != null) listener.onCreate(action);
	}

	@Override
	public void getCommentsByEntity(SocializeSession session, String entityKey, CommentListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getCommentsByEntity(SocializeSession session, String entityKey, int startIndex, int endIndex, CommentListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getCommentsByUser(SocializeSession session, long userId, CommentListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getCommentsById(SocializeSession session, CommentListener listener, long... ids) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		if(listener != null) listener.onGet(action);
	}
}
