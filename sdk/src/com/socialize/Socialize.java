package com.socialize;

import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.api.comment.CommentService;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.factory.FactoryService;
import com.socialize.listener.comment.CommentListener;
import com.socialize.provider.DefaultSocializeProvider;
import com.socialize.provider.comment.CommentProvider;
import com.socialize.util.DeviceUtils;

public final class Socialize {
	
	private FactoryService factoryService;
	private Context context;
	private DefaultSocializeProvider<?> defaultProvider;
	
	public Socialize(Context context) {
		super();
		this.context = context;
	}
	
	public SocializeSession authenticate(String consumerKey, String consumerSecret) {
		String uuid = DeviceUtils.getUUID(context);
		return defaultProvider.authenticate(consumerKey, consumerSecret, uuid);
	}

	public void addComment(SocializeSession session, String key, String comment, CommentListener listener) {
		final CommentService commentService = new CommentService(new CommentProvider(factoryService.getFactoryFor(Comment.class)));
		commentService.setListener(listener);
		commentService.addComment(session, key, comment);
	}
	
	public void init() {
		SocializeConfig config =new SocializeConfig();
		config.init(context);
		factoryService = new FactoryService(config);
	}
	
	public void destroy() {
		
	}
}
