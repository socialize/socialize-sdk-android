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
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActivitySystem;
import com.socialize.api.action.SocializeCommentSystem;
import com.socialize.api.action.SocializeEntitySystem;
import com.socialize.api.action.SocializeLikeSystem;
import com.socialize.api.action.SocializeShareSystem;
import com.socialize.api.action.SocializeUserSystem;
import com.socialize.api.action.SocializeViewSystem;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.UserActivityListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.net.HttpClientFactory;
import com.socialize.networks.ShareOptions;
import com.socialize.ui.comment.CommentShareOptions;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class SocializeApiHost implements ApiHost {
	
	private HttpClientFactory clientFactory;
	private DeviceUtils deviceUtils;
	private SocializeCommentSystem socializeCommentSystem;
	private SocializeEntitySystem socializeEntitySystem;
	private SocializeLikeSystem socializeLikeSystem;
	private SocializeViewSystem socializeViewSystem;
	private SocializeUserSystem socializeUserSystem;
	private SocializeShareSystem socializeShareSystem;
	private SocializeActivitySystem socializeActivitySystem;
	
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	
	public SocializeApiHost() {
		super();
	}
	
	public void init(Context context) {}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#clearSessionCache()
	 */
	@Override
	public void clearSessionCache() {
		socializeUserSystem.clearSession();
	}
	
	
	@Override
	public void clearSessionCache(AuthProviderType authProviderType) {
		socializeUserSystem.clearSession(authProviderType);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#authenticate(java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener, com.socialize.api.SocializeSessionConsumer)
	 */
	@Override
	@Deprecated
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(AuthProviderType.SOCIALIZE);
		authenticate(consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);
	}
	
	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(AuthProviderType.SOCIALIZE);
		authenticate(context, consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		String udid = deviceUtils.getUDID(context);
		
		// TODO: create test case for this
		if(StringUtils.isEmpty(udid)) {
			if(listener != null) {
				listener.onError(new SocializeException("No UDID provided"));
			}
		}
		else {
			// All Api instances have authenticate, so we can just use any old one
			socializeUserSystem.authenticateAsync(context, consumerKey, consumerSecret, udid, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#authenticate(java.lang.String, java.lang.String, com.socialize.auth.AuthProviderData, com.socialize.listener.SocializeAuthListener, com.socialize.api.SocializeSessionConsumer, boolean)
	 */
	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		throw new UnsupportedOperationException("Method not supported");
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#createEntity(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, com.socialize.listener.entity.EntityListener)
	 */
	@Override
	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		socializeEntitySystem.addEntity(session, key, name, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addComment(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, android.location.Location, boolean, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	@Deprecated
	public void addComment(SocializeSession session, String key, String comment, Location location, CommentShareOptions shareOptions, CommentListener listener) {
		socializeCommentSystem.addComment(session, key, comment, location, shareOptions, listener);
	}

	
	@Override
	public void addComment(SocializeSession session, Entity entity, String comment, Location location, ShareOptions shareOptions, CommentListener listener) {
		socializeCommentSystem.addComment(session, entity, comment, location, shareOptions, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getComment(com.socialize.api.SocializeSession, long, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		socializeCommentSystem.getComment(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listEntitiesByKey(com.socialize.api.SocializeSession, com.socialize.listener.entity.EntityListener, java.lang.String)
	 */
	@Override
	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String...keys) {
		socializeEntitySystem.listEntities(session, listener, keys);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.entity.EntityListener)
	 */
	@Override
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		socializeEntitySystem.getEntity(session, key, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsByEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener) {
		socializeCommentSystem.getCommentsByEntity(session, url, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsByEntity(com.socialize.api.SocializeSession, java.lang.String, int, int, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener) {
		socializeCommentSystem.getCommentsByEntity(session, url, startIndex, endIndex, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsById(com.socialize.api.SocializeSession, com.socialize.listener.comment.CommentListener, int)
	 */
	@Override
	public void listCommentsById(SocializeSession session, CommentListener listener, long...ids) {
		socializeCommentSystem.getCommentsById(session, listener, ids);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void listCommentsByUser(SocializeSession session, long userId, CommentListener listener) {
		socializeCommentSystem.getCommentsByUser(session, userId, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsByUser(com.socialize.api.SocializeSession, long, int, int, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void listCommentsByUser(SocializeSession session, long userId, int startIndex, int endIndex, CommentListener listener) {
		socializeCommentSystem.getCommentsByUser(session, userId, startIndex, endIndex, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addLike(com.socialize.api.SocializeSession, java.lang.String, android.location.Location, com.socialize.listener.like.LikeListener)
	 */
	@Deprecated
	@Override
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		socializeLikeSystem.addLike(session, key, location, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addLike(com.socialize.api.SocializeSession, com.socialize.entity.Entity, android.location.Location, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void addLike(SocializeSession session, Entity entity, Location location, LikeListener listener) {
		socializeLikeSystem.addLike(session, entity, location, listener);
	}

	@Override
	public void listLikesByUser(SocializeSession session, long userId, LikeListener listener) {
		socializeLikeSystem.getLikesByUser(session, userId, listener);
	}

	@Override
	public void listLikesByUser(SocializeSession session, long userId, int startIndex, int endIndex, LikeListener listener) {
		socializeLikeSystem.getLikesByUser(session, userId, startIndex, endIndex, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addView(com.socialize.api.SocializeSession, java.lang.String, android.location.Location, com.socialize.listener.view.ViewListener)
	 */
	@Deprecated
	@Override
	public void addView(SocializeSession session, String key, Location location, ViewListener listener) {
		socializeViewSystem.addView(session, key, location, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addView(com.socialize.api.SocializeSession, com.socialize.entity.Entity, android.location.Location, com.socialize.listener.view.ViewListener)
	 */
	@Override
	public void addView(SocializeSession session, Entity entity, Location location, ViewListener listener) {
		socializeViewSystem.addView(session, entity, location, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addShare(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, com.socialize.api.action.ShareType, android.location.Location, com.socialize.listener.share.ShareListener)
	 */
	@Deprecated
	@Override
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		socializeShareSystem.addShare(session, key, text, shareType, location, listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addShare(com.socialize.api.SocializeSession, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.ShareType, android.location.Location, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		socializeShareSystem.addShare(session, entity, text, shareType, location, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#deleteLike(com.socialize.api.SocializeSession, long, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void deleteLike(SocializeSession session, long id, LikeListener listener) {
		socializeLikeSystem.deleteLike(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listLikesById(com.socialize.api.SocializeSession, com.socialize.listener.like.LikeListener, int)
	 */
	@Override
	public void listLikesById(SocializeSession session, LikeListener listener, long...ids) {
		socializeLikeSystem.getLikesById(session, listener, ids);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getLike(com.socialize.api.SocializeSession, long, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLike(SocializeSession session, long id, LikeListener listener) {
		socializeLikeSystem.getLike(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getLike(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLike(SocializeSession session, String key, LikeListener listener) {
		socializeLikeSystem.getLike(session, key, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getUser(com.socialize.api.SocializeSession, long, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		socializeUserSystem.getUser(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#saveUserProfile(android.content.Context, com.socialize.api.SocializeSession, java.lang.String, java.lang.String, java.lang.String, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void saveUserProfile(Context context, SocializeSession session, String firstName, String lastName, String encodedImage, UserListener listener) {
		socializeUserSystem.saveUserProfile(context, session, firstName, lastName, encodedImage, listener);
	}
	
	public void saveUserProfile(Context context, SocializeSession session, UserProfile profile, UserSaveListener listener) {
		socializeUserSystem.saveUserProfile(context, session, profile, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listActivityByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.activity.ActivityListener)
	 */
	@Override
	public void listActivityByUser(SocializeSession session, long id, UserActivityListener listener) {
		socializeActivitySystem.getActivityByUser(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listActivityByUser(com.socialize.api.SocializeSession, long, int, int, com.socialize.listener.activity.ActivityListener)
	 */
	@Override
	public void listActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, UserActivityListener listener) {
		socializeActivitySystem.getActivityByUser(session, id, startIndex, endIndex, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#destroy()
	 */
	@Override
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

	public void setSocializeCommentSystem(SocializeCommentSystem commentApi) {
		this.socializeCommentSystem = commentApi;
	}

	public void setSocializeEntitySystem(SocializeEntitySystem entityApi) {
		this.socializeEntitySystem = entityApi;
	}

	public SocializeCommentSystem getSocializeCommentSystem() {
		return socializeCommentSystem;
	}

	public SocializeEntitySystem getSocializeEntitySystem() {
		return socializeEntitySystem;
	}

	public SocializeLikeSystem getSocializeLikeSystem() {
		return socializeLikeSystem;
	}

	public void setSocializeLikeSystem(SocializeLikeSystem likeApi) {
		this.socializeLikeSystem = likeApi;
	}

	public SocializeViewSystem getSocializeViewSystem() {
		return socializeViewSystem;
	}

	public void setSocializeViewSystem(SocializeViewSystem viewApi) {
		this.socializeViewSystem = viewApi;
	}
	
	public SocializeUserSystem getSocializeUserSystem() {
		return socializeUserSystem;
	}

	public void setSocializeUserSystem(SocializeUserSystem userApi) {
		this.socializeUserSystem = userApi;
	}
	
	public SocializeShareSystem getSocializeShareSystem() {
		return socializeShareSystem;
	}

	public void setSocializeShareSystem(SocializeShareSystem shareApi) {
		this.socializeShareSystem = shareApi;
	}

	public SocializeActivitySystem getSocializeActivitySystem() {
		return socializeActivitySystem;
	}

	public void setSocializeActivitySystem(SocializeActivitySystem activityApi) {
		this.socializeActivitySystem = activityApi;
	}
	
	public IBeanFactory<AuthProviderData> getAuthProviderDataFactory() {
		return authProviderDataFactory;
	}

	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}

}
