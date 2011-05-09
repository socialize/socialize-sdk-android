package com.socialize.api.comment;

import com.socialize.api.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Comment;
import com.socialize.provider.comment.CommentProvider;

public class CommentService extends SocializeService<Comment, CommentProvider> {

	private final String endpoint = "/comment/";
	
	public CommentService(CommentProvider provider) {
		super(provider);
	}

	public void addComment(SocializeSession session, String key, String comment) {
		Comment c = new Comment();
		c.setComment(comment);
		super.putAsync(session, endpoint, c);
	}
	
	public void getAllComments(SocializeSession session, String key) {
		super.listAsync(session, endpoint, key);
	}

	public void getComments(SocializeSession session, int...id) {
		
	}
}
