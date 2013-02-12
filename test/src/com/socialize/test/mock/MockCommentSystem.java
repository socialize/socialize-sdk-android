package com.socialize.test.mock;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.CommentSystem;
import com.socialize.entity.Application;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.comment.CommentListener;
import com.socialize.networks.SocialNetwork;

public class MockCommentSystem extends MockSystem<Comment> implements CommentSystem {
	
	static Comment comment;
	static {
		comment = new Comment();
		Application application = new Application();
		comment.setApplication(application);
		comment.setText("Mock comment");
		comment.setDate(0L);
		comment.setUser(new User());
	}
	
	public MockCommentSystem() {
		super(comment);
	}
	
	
	@Override
	public void deleteComment(SocializeSession session, long commentId, CommentListener listener) {
		if(listener != null) listener.onDelete();
	}

	@Override
	public void addComment(SocializeSession session, Comment comment, CommentOptions commentOptions, CommentListener listener, SocialNetwork... networks) {
		if(listener != null) listener.onCreate(comment);
	}

	@Override
	public void addComment(SocializeSession session, Entity entity, String comment, CommentOptions commentOptions, CommentListener listener, SocialNetwork... networks) {
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
	public void getCommentsByApplication(SocializeSession session, int startIndex, int endIndex, CommentListener listener) {
		if(listener != null) listener.onList(actionList);
	}

	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		if(listener != null) listener.onGet(action);
	}
}
