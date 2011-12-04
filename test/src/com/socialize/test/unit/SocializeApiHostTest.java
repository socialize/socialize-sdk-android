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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.UserActivityApi;
import com.socialize.api.action.CommentApi;
import com.socialize.api.action.EntityApi;
import com.socialize.api.action.LikeApi;
import com.socialize.api.action.ShareApi;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.UserApi;
import com.socialize.api.action.ViewApi;
import com.socialize.auth.AuthProviderData;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.User;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.activity.UserActivityListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.view.ViewListener;
import com.socialize.net.HttpClientFactory;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeApiHostTest extends SocializeUnitTest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({
		SocializeProvider.class, 
		UserApi.class, 
		DeviceUtils.class, 
		SocializeSession.class, 
		SocializeAuthListener.class, 
		SocializeSessionConsumer.class,
		IBeanFactory.class,
		AuthProviderData.class})
	public void testAuthenticateWithAuthProviderData() throws SocializeException, InterruptedException {
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		UserApi userApi = AndroidMock.createMock(UserApi.class, provider);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		AuthProviderData authProviderData = AndroidMock.createMock(AuthProviderData.class);
		IBeanFactory<AuthProviderData> authProviderDataFactory = AndroidMock.createMock(IBeanFactory.class);
		
		SocializeSessionConsumer mockSessionConsumer = AndroidMock.createMock(SocializeSessionConsumer.class);
		
		final String udid = "foobar";
		final String consumerKey = "foobar_consumerKey";
		final String consumerSecret = "foobar_consumerSecret";
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		AndroidMock.expect(deviceUtils.getUDID(getContext())).andReturn(udid);
		
		userApi.authenticateAsync(consumerKey, consumerSecret, udid, authProviderData, listener, mockSessionConsumer, false);
		
		AndroidMock.replay(authProviderDataFactory);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(userApi);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setDeviceUtils(deviceUtils);
		service.setUserApi(userApi);
		service.setAuthProviderDataFactory(authProviderDataFactory);
		
		service.authenticate(consumerKey, consumerSecret, listener, mockSessionConsumer);
		
		AndroidMock.verify(authProviderDataFactory);
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(userApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		SocializeProvider.class, 
		UserApi.class, 
		DeviceUtils.class, 
		SocializeSession.class, 
		SocializeAuthListener.class, 
		SocializeSessionConsumer.class})
	public void testAuthenticate3rdPartyWithAuthProviderData() throws SocializeException, InterruptedException {
		
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		UserApi userApi = AndroidMock.createMock(UserApi.class, provider);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		SocializeSessionConsumer mockSessionConsumer = AndroidMock.createMock(SocializeSessionConsumer.class);
		
		final String udid = "foobar";
		final String consumerKey = "foobar_consumerKey";
		final String consumerSecret = "foobar_consumerSecret";
		
		AndroidMock.expect(deviceUtils.getUDID(getContext())).andReturn(udid);
		
		AuthProviderData data = new AuthProviderData();
		
		userApi.authenticateAsync(consumerKey, consumerSecret, udid, data, listener, mockSessionConsumer, true);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(userApi);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setDeviceUtils(deviceUtils);
		service.setUserApi(userApi);
		
		service.authenticate(consumerKey, consumerSecret, data, listener, mockSessionConsumer, true);
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(userApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testAddComment() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		final String comment = "foobar_comment";
		
		commentApi.addComment(session, key, comment, null, true, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setCommentApi(commentApi);
		
		service.addComment(session, key, comment, null, true, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({EntityApi.class, EntityListener.class})
	public void testCreateEntity() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		EntityApi api = AndroidMock.createMock(EntityApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String key = "foobar";
		final String name = "foobar_comment";
		
		api.addEntity(session, key, name, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setEntityApi(api);
		
		service.createEntity(session, key, name, listener);
		
		AndroidMock.verify(api);
	}
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testGetComment() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final int id = 69;
		
		commentApi.getComment(session, id, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setCommentApi(commentApi);
		
		service.getComment(session, id, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testListCommentsByEntity() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		
		commentApi.getCommentsByEntity(session, key, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setCommentApi(commentApi);
		
		service.listCommentsByEntity(session, key, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testListCommentsByEntityPaginated() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		final int start = 0, end = 10;
		
		commentApi.getCommentsByEntity(session, key, start, end, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setCommentApi(commentApi);
		
		service.listCommentsByEntity(session, key, start, end, listener);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({CommentApi.class, CommentListener.class})
	public void testListCommentsById() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final int[] ids = {1,2,3};
		
		commentApi.getCommentsById(session, listener, ids);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setCommentApi(commentApi);
		
		service.listCommentsById(session, listener, ids);
		
		AndroidMock.verify(commentApi);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({EntityApi.class, EntityListener.class})
	public void testListEntities() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		EntityApi api = AndroidMock.createMock(EntityApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String[] ids = {"A","B","C"};
		
		api.listEntities(session, listener, ids);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setEntityApi(api);
		
		service.listEntitiesByKey(session, listener, ids);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({EntityApi.class, EntityListener.class})
	public void testListEntitiesPaginated() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		EntityApi api = AndroidMock.createMock(EntityApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String[] ids = {"A","B","C"};
		
		api.listEntities(session, listener, ids);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setEntityApi(api);
		
		service.listEntitiesByKey(session, listener, ids);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({EntityApi.class, EntityListener.class})
	public void testGetEntity() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		EntityApi api = AndroidMock.createMock(EntityApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String key = "foobar";
		
		api.getEntity(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setEntityApi(api);
		
		service.getEntity(session, key, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({LikeApi.class, LikeListener.class})
	public void testGetLikeById() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		LikeApi api = AndroidMock.createMock(LikeApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		int id = 69;
		
		api.getLike(session, id, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setLikeApi(api);
		
		service.getLike(session, id, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({LikeApi.class, LikeListener.class})
	public void testGetLikeByKey() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		LikeApi api = AndroidMock.createMock(LikeApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		String key = "foobar";
		
		api.getLike(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setLikeApi(api);
		
		service.getLike(session, key, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({LikeApi.class, LikeListener.class})
	public void testDeleteLike() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		LikeApi api = AndroidMock.createMock(LikeApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		int id = 69;
		
		api.deleteLike(session, id, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setLikeApi(api);
		
		service.deleteLike(session, id, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({LikeApi.class, LikeListener.class})
	public void testListLikesById() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		LikeApi api = AndroidMock.createMock(LikeApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		final int[] ids = {1,2,3};
		
		api.getLikesById(session, listener, ids);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setLikeApi(api);
		
		service.listLikesById(session, listener, ids);
		
		AndroidMock.verify(api);
	}
	
	@UsesMocks ({HttpClientFactory.class})
	public void testDestroy() {
		HttpClientFactory factory = AndroidMock.createMock(HttpClientFactory.class);
		factory.destroy();
		
		AndroidMock.replay(factory);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		service.setClientFactory(factory);
		
		service.destroy();
		
		AndroidMock.verify(factory);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({UserApi.class})
	public void testClearSessionCache() {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		UserApi api = AndroidMock.createMock(UserApi.class, provider);
		
		api.clearSession();
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setUserApi(api);
		
		service.clearSessionCache();
		
		AndroidMock.verify(api);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({LikeApi.class, LikeListener.class})
	public void testAddLike() throws SocializeException {
		SocializeProvider<Like> provider = AndroidMock.createMock(SocializeProvider.class);
		LikeApi commentApi = AndroidMock.createMock(LikeApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		final String key = "foobar";
		
		commentApi.addLike(session, key, null, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setLikeApi(commentApi);
		
		service.addLike(session, key, null, listener);
		
		AndroidMock.verify(commentApi);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({ViewApi.class, ViewListener.class})
	public void testAddView() throws SocializeException {
		SocializeProvider<View> provider = AndroidMock.createMock(SocializeProvider.class);
		ViewApi commentApi = AndroidMock.createMock(ViewApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		ViewListener listener = AndroidMock.createMock(ViewListener.class);
		
		final String key = "foobar";
		
		commentApi.addView(session, key, null, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setViewApi(commentApi);
		
		service.addView(session, key, null, listener);
		
		AndroidMock.verify(commentApi);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({ShareApi.class, ShareListener.class})
	public void testAddShare() throws SocializeException {
		SocializeProvider<Share> provider = AndroidMock.createMock(SocializeProvider.class);
		ShareApi commentApi = AndroidMock.createMock(ShareApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		ShareListener listener = AndroidMock.createMock(ShareListener.class);
		
		final String key = "foobar";
		final String shareText = "share_text";
		final ShareType type = ShareType.FACEBOOK;
		
		commentApi.addShare(session, key, shareText, type, null, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setShareApi(commentApi);
		
		service.addShare(session, key,  shareText, type, null, listener);
		
		AndroidMock.verify(commentApi);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({UserApi.class, UserListener.class})
	public void testGetUser() throws SocializeException {
		SocializeProvider<User> provider = AndroidMock.createMock(SocializeProvider.class);
		UserApi api = AndroidMock.createMock(UserApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserListener listener = AndroidMock.createMock(UserListener.class);
		
		final int key = 69;
		
		api.getUser(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setUserApi(api);
		
		service.getUser(session, key, listener);
		
		AndroidMock.verify(api);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({UserApi.class, UserListener.class})
	public void testSaveUserProfile() throws SocializeException {
		SocializeProvider<User> provider = AndroidMock.createMock(SocializeProvider.class);
		UserApi api = AndroidMock.createMock(UserApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserListener listener = AndroidMock.createMock(UserListener.class);
		
		String firstName = "foobar_firstName";
		String lastName = "foobar_lastName";
		String encodedImage = "foobar_encodedImage";
		
		api.saveUserProfile(getContext(), session, firstName, lastName, encodedImage, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setUserApi(api);
		
		service.saveUserProfile(getContext(), session, firstName, lastName, encodedImage, listener);
		
		AndroidMock.verify(api);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({UserActivityApi.class, UserActivityListener.class})
	public void testGetActivity() throws SocializeException {
		SocializeProvider<SocializeAction> provider = AndroidMock.createMock(SocializeProvider.class);
		UserActivityApi api = AndroidMock.createMock(UserActivityApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserActivityListener listener = AndroidMock.createMock(UserActivityListener.class);
		
		final int key = 69;
		
		api.getActivityByUser(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setUserActivityApi(api);
		
		service.listActivityByUser(session, key, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({UserActivityApi.class, UserActivityListener.class})
	public void testGetActivityWithPagination() throws SocializeException {
		SocializeProvider<SocializeAction> provider = AndroidMock.createMock(SocializeProvider.class);
		UserActivityApi api = AndroidMock.createMock(UserActivityApi.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserActivityListener listener = AndroidMock.createMock(UserActivityListener.class);
		
		final int key = 69;
		
		int start = 0 , end = 100;
		
		api.getActivityByUser(session, key, start, end, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setUserActivityApi(api);
		
		service.listActivityByUser(session, key, start, end, listener);
		
		AndroidMock.verify(api);
	}	
}
