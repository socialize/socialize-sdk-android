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
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.CommentApi;
import com.socialize.api.action.EntityApi;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.net.HttpClientFactory;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeService {
	
	private Context context;
	private HttpClientFactory clientFactory;
	private DeviceUtils deviceUtils;
	private CommentApi commentApi;
	private EntityApi entityApi;
	
	public SocializeService(Context context) {
		super();
		this.context = context;
	}
	
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		// All Api instances have authenticate, so we can just use any old one
		String uuid = deviceUtils.getUDID(context);
		commentApi.authenticateAsync(consumerKey, consumerSecret, uuid, listener, sessionConsumer);
	}

	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		entityApi.createEntity(session, key, name, listener);
	}
	
	public void addComment(SocializeSession session, String key, String comment, CommentListener listener) {
		commentApi.addComment(session, key, comment, listener);
	}
	
	public void getComment(SocializeSession session, int id, CommentListener listener) {
		commentApi.getComment(session, id, listener);
	}
	
	public void listCommentsByEntity(SocializeSession session, String entityKey, CommentListener listener) {
		commentApi.getCommentsByEntity(session, entityKey, listener);
	}
	
	public void listCommentsById(SocializeSession session, CommentListener listener, int...ids) {
		commentApi.getCommentsById(session, listener, ids);
	}
	
	public void destroy() {
		if(clientFactory != null) {
			clientFactory.destroy();
		}
	}

	public void setClientFactory(HttpClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setCommentApi(CommentApi commentApi) {
		this.commentApi = commentApi;
	}

	public void setEntityApi(EntityApi entityApi) {
		this.entityApi = entityApi;
	}

	public CommentApi getCommentApi() {
		return commentApi;
	}

	public EntityApi getEntityApi() {
		return entityApi;
	}
}
