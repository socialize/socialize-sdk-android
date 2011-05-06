package com.socialize.listener.comment;

import java.util.List;

import com.socialize.api.SocializeApiError;
import com.socialize.entity.Comment;

public abstract class CommentAddListener extends CommentListener {

	@Override
	public void onError(SocializeApiError error) {}

	@Override
	public void onGet(Comment entity) {}

	@Override
	public void onList(List<Comment> entities) {}

	@Override
	public void onUpdate(Comment entity) {}

}
