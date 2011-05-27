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
import com.socialize.api.comment.CommentApi;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.factory.FactoryService;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentListener;
import com.socialize.log.SocializeLogger;
import com.socialize.net.HttpClientFactory;
import com.socialize.provider.SocializeProvider;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public final class SocializeService {
	
	private Context context;
	private FactoryService factoryService;
	private SocializeProvider<?> authProvider;
	private HttpClientFactory clientFactory;
	private SocializeLogger logger;
	private SocializeConfig config;
	private DeviceUtils deviceUtils;
	
	private CommentApi commentApi;

	public SocializeService(Context context) {
		super();
		this.context = context;
	}
	
	public SocializeSession authenticate(String endpoint, String consumerKey, String consumerSecret) throws SocializeException {
		String uuid = deviceUtils.getUDID(context);
		return authProvider.authenticate(endpoint, consumerKey, consumerSecret, uuid);
	}

	public void addComment(SocializeSession session, String key, String comment, CommentListener listener) {
//		final CommentApi commentService = new CommentApi(new CommentProvider(factoryService.getFactoryFor(Comment.class), clientFactory));
		commentApi.addComment(session, key, comment, listener);
	}
	
	// Auto-init from IOC
//	public void init() throws SocializeException {
//
//		config = new SocializeConfig();
//		config.init(context);
//		
//		logger = new SocializeLogger();
//		logger.init(config);
//		
//		factoryService = new FactoryService();
//		clientFactory = new DefaultHttpClientFactory();
//		clientFactory.init();
//	}
	
	public void destroy() {
		if(clientFactory != null) {
			clientFactory.destroy();
		}
	}

	public FactoryService getFactoryService() {
		return factoryService;
	}

	public void setFactoryService(FactoryService factoryService) {
		this.factoryService = factoryService;
	}

	public SocializeProvider<?> getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(SocializeProvider<?> provider) {
		this.authProvider = provider;
	}

	public HttpClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(HttpClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public SocializeConfig getConfig() {
		return config;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public DeviceUtils getDeviceUtils() {
		return deviceUtils;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public CommentApi getCommentApi() {
		return commentApi;
	}

	public void setCommentApi(CommentApi commentApi) {
		this.commentApi = commentApi;
	}
}
