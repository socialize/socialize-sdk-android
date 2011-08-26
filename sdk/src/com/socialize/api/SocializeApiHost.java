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
package com.socialize.api;

import android.content.Context;
import android.location.Location;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.action.CommentApi;
import com.socialize.api.action.EntityApi;
import com.socialize.api.action.LikeApi;
import com.socialize.api.action.ViewApi;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.net.HttpClientFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeApiHost {
	
	private Context context;
	private HttpClientFactory clientFactory;
	private DeviceUtils deviceUtils;
	private CommentApi commentApi;
	private EntityApi entityApi;
	private LikeApi likeApi;
	private ViewApi viewApi;
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	
	public SocializeApiHost(Context context) {
		super();
		this.context = context;
	}
	
	public void clearSessionCache() {
		commentApi.clearSession();
	}
	
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(AuthProviderType.SOCIALIZE);
		authenticate(consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);
	}
	
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProvider, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(authProvider);
		authenticate(consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);
	}
	
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		String udid = deviceUtils.getUDID(context);
		
		// TODO: create test case for this
		if(StringUtils.isEmpty(udid)) {
			if(listener != null) {
				listener.onError(new SocializeException("No UDID provided"));
			}
		}
		else {
			// All Api instances have authenticate, so we can just use any old one
			commentApi.authenticateAsync(consumerKey, consumerSecret, udid, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
		}
	}
	
	@Deprecated
	public void authenticate(String consumerKey, String consumerSecret, String authUserId3rdParty, String authToken3rdParty, AuthProviderType authProvider, String appId3rdParty, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		String udid = deviceUtils.getUDID(context);
		
		// TODO: create test case for this
		if(StringUtils.isEmpty(udid)) {
			if(listener != null) {
				listener.onError(new SocializeException("No UDID provided"));
			}
		}
		else {
			// All Api instances have authenticate, so we can just use any old one
			commentApi.authenticateAsync(consumerKey, consumerSecret, udid, authUserId3rdParty, authToken3rdParty, authProvider, appId3rdParty, listener, sessionConsumer, do3rdPartyAuth);
		}
	}

	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		entityApi.addEntity(session, key, name, listener);
	}
	
	public void addComment(SocializeSession session, String key, String comment, Location location, CommentListener listener) {
		commentApi.addComment(session, key, comment, location, listener);
	}
	
	public void getComment(SocializeSession session, int id, CommentListener listener) {
		commentApi.getComment(session, id, listener);
	}
	
	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String...keys) {
		entityApi.listEntities(session, listener, keys);
	}
	
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		entityApi.getEntity(session, key, listener);
	}
	
	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener) {
		commentApi.getCommentsByEntity(session, url, listener);
	}
	
	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener) {
		commentApi.getCommentsByEntity(session, url, startIndex, endIndex, listener);
	}
	
	public void listCommentsById(SocializeSession session, CommentListener listener, int...ids) {
		commentApi.getCommentsById(session, listener, ids);
	}
	
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		likeApi.addLike(session, key, location, listener);
	}
	
	public void addView(SocializeSession session, String key, Location location, ViewListener listener) {
		viewApi.addView(session, key, location, listener);
	}
	
	public void deleteLike(SocializeSession session, int id, LikeListener listener) {
		likeApi.deleteLike(session, id, listener);
	}
	
	public void listLikesById(SocializeSession session, LikeListener listener, int...ids) {
		likeApi.getLikesById(session, listener, ids);
	}
	
	public void getLike(SocializeSession session, int id, LikeListener listener) {
		likeApi.getLike(session, id, listener);
	}
	
	public void getLike(SocializeSession session, String key, LikeListener listener) {
		likeApi.getLike(session, key, listener);
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

	public LikeApi getLikeApi() {
		return likeApi;
	}

	public void setLikeApi(LikeApi likeApi) {
		this.likeApi = likeApi;
	}

	public ViewApi getViewApi() {
		return viewApi;
	}

	public void setViewApi(ViewApi viewApi) {
		this.viewApi = viewApi;
	}

	public IBeanFactory<AuthProviderData> getAuthProviderDataFactory() {
		return authProviderDataFactory;
	}

	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}
	
}
