/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize;

import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.api.comment.CommentService;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.factory.FactoryService;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentListener;
import com.socialize.log.SocializeLogger;
import com.socialize.net.DefaultHttpClientFactory;
import com.socialize.net.HttpClientFactory;
import com.socialize.provider.DefaultSocializeProvider;
import com.socialize.provider.comment.CommentProvider;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public final class Socialize {
	
	private FactoryService factoryService;
	private Context context;
	private DefaultSocializeProvider<?> defaultProvider;
	private HttpClientFactory clientFactory;
	private SocializeLogger logger;
	private SocializeConfig config;
	
	public Socialize(Context context) {
		super();
		this.context = context;
	}
	
	public SocializeSession authenticate(String consumerKey, String consumerSecret) {
		String uuid = DeviceUtils.getUDID(context);
		return defaultProvider.authenticate(consumerKey, consumerSecret, uuid);
	}

	public void addComment(SocializeSession session, String key, String comment, CommentListener listener) {
		final CommentService commentService = new CommentService(new CommentProvider(factoryService.getFactoryFor(Comment.class), clientFactory));
		commentService.setListener(listener);
		commentService.addComment(session, key, comment);
	}
	
	public void init() throws SocializeException {

		config = new SocializeConfig();
		config.init(context);
		
		logger = new SocializeLogger();
		logger.init(config);
		
		factoryService = new FactoryService(config);
		clientFactory = new DefaultHttpClientFactory();
		clientFactory.init();
	}
	
	public void destroy() {
		if(clientFactory != null) {
			clientFactory.destroy();
		}
	}
}
