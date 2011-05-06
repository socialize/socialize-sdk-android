package com.socialize;

import android.content.Context;

import com.socialize.api.comment.CommentService;
import com.socialize.entity.factory.FactoryService;
import com.socialize.listener.comment.CommentListener;
import com.socialize.provider.comment.CommentProvider;

public final class Socialize {
	
	private FactoryService factoryService;
	private Context context;
	
	public Socialize(Context context) {
		super();
		this.context = context;
	}

	public void addComment(String key, String comment, CommentListener listener) {
		final CommentService commentService = new CommentService(new CommentProvider(factoryService));
		commentService.setListener(listener);
		commentService.addComment(key, comment);
	}
	
	public void init() {
		factoryService = new FactoryService(context);
	}
	
	public void destroy() {
		
	}
}
