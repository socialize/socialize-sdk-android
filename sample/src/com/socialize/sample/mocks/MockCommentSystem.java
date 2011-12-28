package com.socialize.sample.mocks;

import android.location.Location;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.CommentSystem;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.listener.comment.CommentListener;
import com.socialize.networks.ShareOptions;

public class MockCommentSystem implements CommentSystem {
	
	private Comment comment;
	private ListResult<Comment> commentList;
	
	@Override
	public void addComment(SocializeSession session, Entity entity, String text, Location location, ShareOptions shareOptions, CommentListener listener) {
		listener.onCreate(comment);
	}

	@Override
	public void getCommentsByEntity(SocializeSession session, String entityKey, CommentListener listener) {
		listener.onList(commentList);
	}

	@Override
	public void getCommentsByEntity(SocializeSession session, String entityKey, int startIndex, int endIndex, CommentListener listener) {
		listener.onList(commentList);
	}

	@Override
	public void getCommentsByUser(SocializeSession session, long userId, CommentListener listener) {
		listener.onList(commentList);
	}

	@Override
	public void getCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener) {
		listener.onList(commentList);
	}

	@Override
	public void getCommentsById(SocializeSession session, CommentListener listener, long... ids) {
		listener.onList(commentList);
	}

	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		listener.onGet(comment);
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public void setCommentList(ListResult<Comment> commentList) {
		this.commentList = commentList;
	}

}
