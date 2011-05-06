package com.socialize.api.comment;

import com.socialize.api.SocializeService;
import com.socialize.entity.Comment;
import com.socialize.provider.comment.CommentProvider;

public class CommentService extends SocializeService<Comment, CommentProvider> {

	private final String endpoint = "/comment/";
	
	public CommentService(CommentProvider provider) {
		super(provider);
	}

	public void addComment(String key, String comment) {
		Comment c = new Comment();
		c.setComment(comment);
		super.putAsync(endpoint, c);
	}
	
	public void getAllComments(String key) {
		super.listAsync(endpoint, key);
	}

	public void getComments(int...id) {
		
	}
}
