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
import com.socialize.api.action.ActivityApi;
import com.socialize.api.action.CommentApi;
import com.socialize.api.action.EntityApi;
import com.socialize.api.action.LikeApi;
import com.socialize.api.action.RecommendationApi;
import com.socialize.api.action.ShareApi;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.UserApi;
import com.socialize.api.action.ViewApi;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.ActivityListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.net.HttpClientFactory;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeApiHost implements ApiHost {
	
	private Context context;
	private HttpClientFactory clientFactory;
	private DeviceUtils deviceUtils;
	private CommentApi commentApi;
	private EntityApi entityApi;
	private LikeApi likeApi;
	private ViewApi viewApi;
	private UserApi userApi;
	private ShareApi shareApi;
	private ActivityApi activityApi;
	private RecommendationApi recommendationApi;
	
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	
	public SocializeApiHost() {
		super();
	}
	
	public void init(Context context) {
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#clearSessionCache()
	 */
	@Override
	public void clearSessionCache() {
		userApi.clearSession();
	}
	
	
	@Override
	public void clearSessionCache(AuthProviderType authProviderType) {
		userApi.clearSession(authProviderType);
		
		
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#authenticate(java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener, com.socialize.api.SocializeSessionConsumer)
	 */
	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		AuthProviderData authProviderData = authProviderDataFactory.getBean();
		authProviderData.setAuthProviderType(AuthProviderType.SOCIALIZE);
		authenticate(consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);
	}
	
//	public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProvider, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
//		AuthProviderData authProviderData = authProviderDataFactory.getBean();
//		authProviderData.setAuthProviderType(authProvider);
//		authenticate(consumerKey, consumerSecret, authProviderData, listener, sessionConsumer, false);
//	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#authenticate(java.lang.String, java.lang.String, com.socialize.auth.AuthProviderData, com.socialize.listener.SocializeAuthListener, com.socialize.api.SocializeSessionConsumer, boolean)
	 */
	@Override
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
			userApi.authenticateAsync(consumerKey, consumerSecret, udid, authProviderData, listener, sessionConsumer, do3rdPartyAuth);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#createEntity(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, com.socialize.listener.entity.EntityListener)
	 */
	@Override
	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		entityApi.addEntity(session, key, name, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addComment(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, android.location.Location, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void addComment(SocializeSession session, String key, String comment, Location location, CommentListener listener) {
		commentApi.addComment(session, key, comment, location, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getComment(com.socialize.api.SocializeSession, long, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void getComment(SocializeSession session, long id, CommentListener listener) {
		commentApi.getComment(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listEntitiesByKey(com.socialize.api.SocializeSession, com.socialize.listener.entity.EntityListener, java.lang.String)
	 */
	@Override
	public void listEntitiesByKey(SocializeSession session, EntityListener listener, String...keys) {
		entityApi.listEntities(session, listener, keys);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.entity.EntityListener)
	 */
	@Override
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		entityApi.getEntity(session, key, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listRecommendedEntitiesByLike(com.socialize.api.SocializeSession, com.socialize.listener.entity.EntityListener, long)
	 */
	@Override
	public void listRecommendedEntitiesByLike(SocializeSession session, EntityListener listener, long id) {
		recommendationApi.listRecommendedEntityesForLike(session, listener, id);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsByEntity(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void listCommentsByEntity(SocializeSession session, String url, CommentListener listener) {
		commentApi.getCommentsByEntity(session, url, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsByEntity(com.socialize.api.SocializeSession, java.lang.String, int, int, com.socialize.listener.comment.CommentListener)
	 */
	@Override
	public void listCommentsByEntity(SocializeSession session, String url, int startIndex, int endIndex, CommentListener listener) {
		commentApi.getCommentsByEntity(session, url, startIndex, endIndex, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listCommentsById(com.socialize.api.SocializeSession, com.socialize.listener.comment.CommentListener, int)
	 */
	@Override
	public void listCommentsById(SocializeSession session, CommentListener listener, int...ids) {
		commentApi.getCommentsById(session, listener, ids);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addLike(com.socialize.api.SocializeSession, java.lang.String, android.location.Location, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void addLike(SocializeSession session, String key, Location location, LikeListener listener) {
		likeApi.addLike(session, key, location, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addView(com.socialize.api.SocializeSession, java.lang.String, android.location.Location, com.socialize.listener.view.ViewListener)
	 */
	@Override
	public void addView(SocializeSession session, String key, Location location, ViewListener listener) {
		viewApi.addView(session, key, location, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#addShare(com.socialize.api.SocializeSession, java.lang.String, java.lang.String, com.socialize.api.action.ShareType, android.location.Location, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		shareApi.addShare(session, key, text, shareType, location, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#deleteLike(com.socialize.api.SocializeSession, long, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void deleteLike(SocializeSession session, long id, LikeListener listener) {
		likeApi.deleteLike(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listLikesById(com.socialize.api.SocializeSession, com.socialize.listener.like.LikeListener, int)
	 */
	@Override
	public void listLikesById(SocializeSession session, LikeListener listener, int...ids) {
		likeApi.getLikesById(session, listener, ids);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getLike(com.socialize.api.SocializeSession, long, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLike(SocializeSession session, long id, LikeListener listener) {
		likeApi.getLike(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getLike(com.socialize.api.SocializeSession, java.lang.String, com.socialize.listener.like.LikeListener)
	 */
	@Override
	public void getLike(SocializeSession session, String key, LikeListener listener) {
		likeApi.getLike(session, key, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#getUser(com.socialize.api.SocializeSession, long, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		userApi.getUser(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#saveUserProfile(android.content.Context, com.socialize.api.SocializeSession, java.lang.String, java.lang.String, java.lang.String, com.socialize.listener.user.UserListener)
	 */
	@Override
	public void saveUserProfile(Context context, SocializeSession session, String firstName, String lastName, String encodedImage, UserListener listener) {
		userApi.saveUserProfile(context, session, firstName, lastName, encodedImage, listener);
	}
	
	public void saveUserProfile(Context context, SocializeSession session, UserProfile profile, UserSaveListener listener) {
		userApi.saveUserProfile(context, session, profile, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listActivityByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.activity.ActivityListener)
	 */
	@Override
	public void listActivityByUser(SocializeSession session, long id, ActivityListener listener) {
		activityApi.getActivityByUser(session, id, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ApiHost#listActivityByUser(com.socialize.api.SocializeSession, long, int, int, com.socialize.listener.activity.ActivityListener)
	 */
	@Override
	public void listActivityByUser(SocializeSession session, long id, int startIndex, int endIndex, ActivityListener listener) {
		activityApi.getActivityByUser(session, id, startIndex, endIndex, listener);
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
	
	public UserApi getUserApi() {
		return userApi;
	}

	public void setUserApi(UserApi userApi) {
		this.userApi = userApi;
	}
	
	public ShareApi getShareApi() {
		return shareApi;
	}

	public void setShareApi(ShareApi shareApi) {
		this.shareApi = shareApi;
	}

	public ActivityApi getActivityApi() {
		return activityApi;
	}

	public void setActivityApi(ActivityApi activityApi) {
		this.activityApi = activityApi;
	}
	
	public RecommendationApi getRecommendationApi() {
		return recommendationApi;
	}

	public void setRecommendationApi(RecommendationApi recommendationApi) {
		this.recommendationApi = recommendationApi;
	}

	public IBeanFactory<AuthProviderData> getAuthProviderDataFactory() {
		return authProviderDataFactory;
	}

	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}
}
