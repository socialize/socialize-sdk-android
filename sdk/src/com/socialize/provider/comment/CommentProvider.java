package com.socialize.provider.comment;

import com.socialize.entity.Comment;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.provider.DefaultSocializeProvider;

public class CommentProvider extends DefaultSocializeProvider<Comment> {
	
	public CommentProvider(SocializeObjectFactory<Comment> factory) {
		super(factory);
	}

}
