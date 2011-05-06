package com.socialize.provider.comment;

import com.socialize.entity.Comment;
import com.socialize.entity.factory.FactoryService;
import com.socialize.provider.DefaultSocializeProvider;

public class CommentProvider extends DefaultSocializeProvider<Comment> {

	public CommentProvider(FactoryService factoryService) {
		super(factoryService);
	}

}
