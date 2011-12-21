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
import com.socialize.api.action.SocializeActivitySystem;
import com.socialize.api.action.SocializeCommentSystem;
import com.socialize.api.action.SocializeEntitySystem;
import com.socialize.api.action.SocializeLikeSystem;
import com.socialize.api.action.SocializeShareSystem;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeUserSystem;
import com.socialize.api.action.SocializeViewSystem;
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
		SocializeUserSystem.class, 
		DeviceUtils.class, 
		SocializeSession.class, 
		SocializeAuthListener.class, 
		SocializeSessionConsumer.class,
		IBeanFactory.class,
		AuthProviderData.class})
	public void testAuthenticateWithAuthProviderData() throws SocializeException, InterruptedException {
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		SocializeUserSystem SocializeUserSystem = AndroidMock.createMock(SocializeUserSystem.class, provider);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		AuthProviderData authProviderData = AndroidMock.createMock(AuthProviderData.class);
		IBeanFactory<AuthProviderData> authProviderDataFactory = AndroidMock.createMock(IBeanFactory.class);
		
		SocializeSessionConsumer mockSessionConsumer = AndroidMock.createMock(SocializeSessionConsumer.class);
		
		final String udid = "foobar";
		final String consumerKey = "foobar_consumerKey";
		final String consumerSecret = "foobar_consumerSecret";
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		AndroidMock.expect(deviceUtils.getUDID(getContext())).andReturn(udid);
		
		SocializeUserSystem.authenticateAsync(consumerKey, consumerSecret, udid, authProviderData, listener, mockSessionConsumer, false);
		
		AndroidMock.replay(authProviderDataFactory);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(SocializeUserSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setDeviceUtils(deviceUtils);
		service.setSocializeUserSystem(SocializeUserSystem);
		service.setAuthProviderDataFactory(authProviderDataFactory);
		
		service.authenticate(consumerKey, consumerSecret, listener, mockSessionConsumer);
		
		AndroidMock.verify(authProviderDataFactory);
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(SocializeUserSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({
		SocializeProvider.class, 
		SocializeUserSystem.class, 
		DeviceUtils.class, 
		SocializeSession.class, 
		SocializeAuthListener.class, 
		SocializeSessionConsumer.class})
	public void testAuthenticate3rdPartyWithAuthProviderData() throws SocializeException, InterruptedException {
		
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		SocializeUserSystem SocializeUserSystem = AndroidMock.createMock(SocializeUserSystem.class, provider);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		SocializeSessionConsumer mockSessionConsumer = AndroidMock.createMock(SocializeSessionConsumer.class);
		
		final String udid = "foobar";
		final String consumerKey = "foobar_consumerKey";
		final String consumerSecret = "foobar_consumerSecret";
		
		AndroidMock.expect(deviceUtils.getUDID(getContext())).andReturn(udid);
		
		AuthProviderData data = new AuthProviderData();
		
		SocializeUserSystem.authenticateAsync(consumerKey, consumerSecret, udid, data, listener, mockSessionConsumer, true);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(SocializeUserSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setDeviceUtils(deviceUtils);
		service.setSocializeUserSystem(SocializeUserSystem);
		
		service.authenticate(consumerKey, consumerSecret, data, listener, mockSessionConsumer, true);
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(SocializeUserSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeCommentSystem.class, CommentListener.class})
	public void testAddComment() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeCommentSystem SocializeCommentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		final String comment = "foobar_comment";
		
		SocializeCommentSystem.addComment(session, key, comment, null, null, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setSocializeCommentSystem(SocializeCommentSystem);
		
		service.addComment(session, key, comment, null, null, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeEntitySystem.class, EntityListener.class})
	public void testCreateEntity() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeEntitySystem api = AndroidMock.createMock(SocializeEntitySystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String key = "foobar";
		final String name = "foobar_comment";
		
		api.addEntity(session, key, name, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setSocializeEntitySystem(api);
		
		service.createEntity(session, key, name, listener);
		
		AndroidMock.verify(api);
	}
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeCommentSystem.class, CommentListener.class})
	public void testGetComment() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeCommentSystem SocializeCommentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final int id = 69;
		
		SocializeCommentSystem.getComment(session, id, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeCommentSystem(SocializeCommentSystem);
		
		service.getComment(session, id, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeCommentSystem.class, CommentListener.class})
	public void testListCommentsByEntity() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeCommentSystem SocializeCommentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		
		SocializeCommentSystem.getCommentsByEntity(session, key, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setSocializeCommentSystem(SocializeCommentSystem);
		
		service.listCommentsByEntity(session, key, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeCommentSystem.class, CommentListener.class})
	public void testListCommentsByEntityPaginated() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeCommentSystem SocializeCommentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final String key = "foobar";
		final int start = 0, end = 10;
		
		SocializeCommentSystem.getCommentsByEntity(session, key, start, end, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setSocializeCommentSystem(SocializeCommentSystem);
		
		service.listCommentsByEntity(session, key, start, end, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeCommentSystem.class, CommentListener.class})
	public void testListCommentsById() throws SocializeException {
		SocializeProvider<Comment> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeCommentSystem SocializeCommentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		CommentListener listener = AndroidMock.createMock(CommentListener.class);
		
		final long[] ids = {1,2,3};
		
		SocializeCommentSystem.getCommentsById(session, listener, ids);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setSocializeCommentSystem(SocializeCommentSystem);
		
		service.listCommentsById(session, listener, ids);
		
		AndroidMock.verify(SocializeCommentSystem);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeEntitySystem.class, EntityListener.class})
	public void testListEntities() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeEntitySystem api = AndroidMock.createMock(SocializeEntitySystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String[] ids = {"A","B","C"};
		
		api.listEntities(session, listener, ids);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		service.init(getContext());
		
		service.setSocializeEntitySystem(api);
		
		service.listEntitiesByKey(session, listener, ids);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeEntitySystem.class, EntityListener.class})
	public void testListEntitiesPaginated() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeEntitySystem api = AndroidMock.createMock(SocializeEntitySystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String[] ids = {"A","B","C"};
		
		api.listEntities(session, listener, ids);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeEntitySystem(api);
		
		service.listEntitiesByKey(session, listener, ids);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeEntitySystem.class, EntityListener.class})
	public void testGetEntity() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeEntitySystem api = AndroidMock.createMock(SocializeEntitySystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		EntityListener listener = AndroidMock.createMock(EntityListener.class);
		
		final String key = "foobar";
		
		api.getEntity(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeEntitySystem(api);
		
		service.getEntity(session, key, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeLikeSystem.class, LikeListener.class})
	public void testGetLikeById() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeLikeSystem api = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		int id = 69;
		
		api.getLike(session, id, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeLikeSystem(api);
		
		service.getLike(session, id, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeLikeSystem.class, LikeListener.class})
	public void testGetLikeByKey() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeLikeSystem api = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		String key = "foobar";
		
		api.getLike(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeLikeSystem(api);
		
		service.getLike(session, key, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeLikeSystem.class, LikeListener.class})
	public void testDeleteLike() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeLikeSystem api = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		int id = 69;
		
		api.deleteLike(session, id, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeLikeSystem(api);
		
		service.deleteLike(session, id, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeLikeSystem.class, LikeListener.class})
	public void testListLikesById() throws SocializeException {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeLikeSystem api = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		final long[] ids = {1,2,3};
		
		api.getLikesById(session, listener, ids);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeLikeSystem(api);
		
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
	@UsesMocks ({SocializeUserSystem.class})
	public void testClearSessionCache() {
		SocializeProvider<Entity> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeUserSystem api = AndroidMock.createMock(SocializeUserSystem.class, provider);
		
		api.clearSession();
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeUserSystem(api);
		
		service.clearSessionCache();
		
		AndroidMock.verify(api);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeLikeSystem.class, LikeListener.class})
	public void testAddLike() throws SocializeException {
		SocializeProvider<Like> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeLikeSystem SocializeCommentSystem = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		LikeListener listener = AndroidMock.createMock(LikeListener.class);
		
		final String key = "foobar";
		
		SocializeCommentSystem.addLike(session, key, null, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeLikeSystem(SocializeCommentSystem);
		
		service.addLike(session, key, null, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeViewSystem.class, ViewListener.class})
	public void testAddView() throws SocializeException {
		SocializeProvider<View> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeViewSystem SocializeCommentSystem = AndroidMock.createMock(SocializeViewSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		ViewListener listener = AndroidMock.createMock(ViewListener.class);
		
		final String key = "foobar";
		
		SocializeCommentSystem.addView(session, key, null, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeViewSystem(SocializeCommentSystem);
		
		service.addView(session, key, null, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeShareSystem.class, ShareListener.class})
	public void testAddShare() throws SocializeException {
		SocializeProvider<Share> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeShareSystem SocializeCommentSystem = AndroidMock.createMock(SocializeShareSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		ShareListener listener = AndroidMock.createMock(ShareListener.class);
		
		final String key = "foobar";
		final String shareText = "share_text";
		final ShareType type = ShareType.FACEBOOK;
		
		SocializeCommentSystem.addShare(session, key, shareText, type, null, listener);
		
		AndroidMock.replay(SocializeCommentSystem);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeShareSystem(SocializeCommentSystem);
		
		service.addShare(session, key,  shareText, type, null, listener);
		
		AndroidMock.verify(SocializeCommentSystem);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeUserSystem.class, UserListener.class})
	public void testGetUser() throws SocializeException {
		SocializeProvider<User> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeUserSystem api = AndroidMock.createMock(SocializeUserSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserListener listener = AndroidMock.createMock(UserListener.class);
		
		final int key = 69;
		
		api.getUser(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeUserSystem(api);
		
		service.getUser(session, key, listener);
		
		AndroidMock.verify(api);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeUserSystem.class, UserListener.class})
	public void testSaveUserProfile() throws SocializeException {
		SocializeProvider<User> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeUserSystem api = AndroidMock.createMock(SocializeUserSystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserListener listener = AndroidMock.createMock(UserListener.class);
		
		String firstName = "foobar_firstName";
		String lastName = "foobar_lastName";
		String encodedImage = "foobar_encodedImage";
		
		api.saveUserProfile(getContext(), session, firstName, lastName, encodedImage, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeUserSystem(api);
		
		service.saveUserProfile(getContext(), session, firstName, lastName, encodedImage, listener);
		
		AndroidMock.verify(api);
	}	
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeActivitySystem.class, UserActivityListener.class})
	public void testGetActivity() throws SocializeException {
		SocializeProvider<SocializeAction> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeActivitySystem api = AndroidMock.createMock(SocializeActivitySystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserActivityListener listener = AndroidMock.createMock(UserActivityListener.class);
		
		final int key = 69;
		
		api.getActivityByUser(session, key, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeActivitySystem(api);
		
		service.listActivityByUser(session, key, listener);
		
		AndroidMock.verify(api);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeActivitySystem.class, UserActivityListener.class})
	public void testGetActivityWithPagination() throws SocializeException {
		SocializeProvider<SocializeAction> provider = AndroidMock.createMock(SocializeProvider.class);
		SocializeActivitySystem api = AndroidMock.createMock(SocializeActivitySystem.class, provider);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserActivityListener listener = AndroidMock.createMock(UserActivityListener.class);
		
		final int key = 69;
		
		int start = 0 , end = 100;
		
		api.getActivityByUser(session, key, start, end, listener);
		
		AndroidMock.replay(api);
		
		SocializeApiHost service = new SocializeApiHost();
		
		service.init(getContext());
		
		service.setSocializeActivitySystem(api);
		
		service.listActivityByUser(session, key, start, end, listener);
		
		AndroidMock.verify(api);
	}	
}
