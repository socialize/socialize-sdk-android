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

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeServiceImpl;
import com.socialize.SocializeServiceImpl.InitTask;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.SocializeActivitySystem;
import com.socialize.api.action.SocializeCommentSystem;
import com.socialize.api.action.SocializeEntitySystem;
import com.socialize.api.action.SocializeLikeSystem;
import com.socialize.api.action.SocializeShareSystem;
import com.socialize.api.action.SocializeSubscriptionSystem;
import com.socialize.api.action.SocializeUserSystem;
import com.socialize.api.action.SocializeViewSystem;
import com.socialize.api.action.UserSystem;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.init.SocializeInitializationAsserter;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.PublicSocialize;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.profile.UserProfile;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.Drawables;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 *
 */
@UsesMocks({
	IOCContainer.class, 
	SocializeSession.class, 
	SocializeLogger.class, 
	SocializeInitializationAsserter.class,
	SocializeCommentSystem.class,
	SocializeShareSystem.class,
	SocializeLikeSystem.class,
	SocializeEntitySystem.class,
	SocializeViewSystem.class,
	SocializeActivitySystem.class,
	SocializeUserSystem.class,
	SocializeSubscriptionSystem.class,
	Drawables.class,
	SocializeConfig.class,
	SocializeProvider.class})
public class SocializeServiceTest extends SocializeActivityTest {
	
	IOCContainer container;
	
	SocializeCommentSystem commentSystem;
	SocializeShareSystem shareSystem;
	SocializeLikeSystem likeSystem;
	SocializeEntitySystem entitySystem;
	SocializeViewSystem viewSystem;
	SocializeActivitySystem activitySystem;
	SocializeUserSystem userSystem;
	SocializeSubscriptionSystem subscriptionSystem;
	Drawables drawables;
	SocializeProvider<?> provider;
	
	SocializeLogger logger;
	IBeanFactory<AuthProviderData> authProviderDataFactory;
	AuthProviderData authProviderData;
	SocializeSession session;
	
	SocializeConfig config;
	
	@SuppressWarnings("unchecked")
	private void setupDefaultMocks() {
		container = AndroidMock.createMock(IOCContainer.class);
		logger = AndroidMock.createNiceMock(SocializeLogger.class);
		authProviderDataFactory = (IBeanFactory<AuthProviderData>) AndroidMock.createMock(IBeanFactory.class);
		authProviderData = AndroidMock.createMock(AuthProviderData.class);
		session = AndroidMock.createMock(SocializeSession.class);
		provider = AndroidMock.createMock(SocializeProvider.class);
		
		config = AndroidMock.createNiceMock(SocializeConfig.class);
		commentSystem = AndroidMock.createMock(SocializeCommentSystem.class, provider);
		shareSystem = AndroidMock.createMock(SocializeShareSystem.class, provider);
		likeSystem = AndroidMock.createMock(SocializeLikeSystem.class, provider);
		entitySystem = AndroidMock.createMock(SocializeEntitySystem.class, provider);
		viewSystem = AndroidMock.createMock(SocializeViewSystem.class, provider);
		activitySystem = AndroidMock.createMock(SocializeActivitySystem.class, provider);
		userSystem = AndroidMock.createMock(SocializeUserSystem.class, provider);
		subscriptionSystem = AndroidMock.createMock(SocializeSubscriptionSystem.class, provider);
		drawables = AndroidMock.createMock(Drawables.class);
		
		AndroidMock.expect(container.getBean("commentSystem")).andReturn(commentSystem);
		AndroidMock.expect(container.getBean("shareSystem")).andReturn(shareSystem);
		AndroidMock.expect(container.getBean("likeSystem")).andReturn(likeSystem);
		AndroidMock.expect(container.getBean("viewSystem")).andReturn(viewSystem);
		AndroidMock.expect(container.getBean("userSystem")).andReturn(userSystem);
		AndroidMock.expect(container.getBean("activitySystem")).andReturn(activitySystem);
		AndroidMock.expect(container.getBean("entitySystem")).andReturn(entitySystem);
		AndroidMock.expect(container.getBean("subscriptionSystem")).andReturn(subscriptionSystem);
		AndroidMock.expect(container.getBean("drawables")).andReturn(drawables);
		AndroidMock.expect(container.getBean("config")).andReturn(config);
		
		AndroidMock.expect(container.getBean("authProviderDataFactory")).andReturn(authProviderDataFactory);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		AndroidMock.expect(container.getBean("initializationAsserter")).andReturn(null);
	}
	
	private void replayDefaultMocks() {
		
		AndroidMock.replay(config);
		AndroidMock.replay(commentSystem);
		AndroidMock.replay(shareSystem);
		AndroidMock.replay(likeSystem);
		AndroidMock.replay(entitySystem);
		AndroidMock.replay(viewSystem);
		AndroidMock.replay(activitySystem);
		AndroidMock.replay(userSystem);
		AndroidMock.replay(subscriptionSystem);
		AndroidMock.replay(provider);
		AndroidMock.replay(drawables);
		
		AndroidMock.replay(container);
		AndroidMock.replay(logger);
		AndroidMock.replay(authProviderDataFactory);
		AndroidMock.replay(authProviderData);
		AndroidMock.replay(session);
	}
	
	private void verifyDefaultMocks() {
		
		AndroidMock.verify(config);
		AndroidMock.verify(commentSystem);
		AndroidMock.verify(shareSystem);
		AndroidMock.verify(likeSystem);
		AndroidMock.verify(entitySystem);
		AndroidMock.verify(viewSystem);
		AndroidMock.verify(activitySystem);
		AndroidMock.verify(userSystem);
		AndroidMock.verify(subscriptionSystem);
		AndroidMock.verify(provider);
		AndroidMock.verify(drawables);		
		
		AndroidMock.verify(container);
		AndroidMock.verify(logger);
		AndroidMock.verify(authProviderDataFactory);
		AndroidMock.verify(authProviderData);
		AndroidMock.verify(session);
	}
	
	public void testSocializeInitDestroy() {
		
		setupDefaultMocks();

		container.destroy();
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.destroy();
		
		assertFalse(socialize.isInitialized());
		
		verifyDefaultMocks();
	}
	
	public void testSocializeMultiInitDestroy() {
		
		setupDefaultMocks();

		container.destroy();
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.init(getContext(), container);
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.destroy();
		socialize.destroy();
		
		assertTrue(socialize.isInitialized());
		
		socialize.destroy();
		
		assertFalse(socialize.isInitialized());
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({IOCContainer.class, SocializeLogger.class})
	public void testInitFail() {
		container = AndroidMock.createMock(IOCContainer.class);
		logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		AndroidMock.expect(container.getBean((String)AndroidMock.anyObject())).andThrow(new RuntimeException("TEST ERROR. IGNORE ME!"));
		logger.error(AndroidMock.eq(SocializeLogger.INITIALIZE_FAILED), (Exception) AndroidMock.anyObject());
		
		AndroidMock.replay(container);
		AndroidMock.replay(logger);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.setLogger(logger);
		socialize.init(new MockContext(), container);
		
		AndroidMock.verify(container);
		AndroidMock.verify(logger);
	}
	
	@UsesMocks ({CommentAddListener.class, Comment.class, Entity.class})
	public void testAddCommentSuccess() {
		CommentAddListener listener = AndroidMock.createMock(CommentAddListener.class);
		
		final String comment = "bar";
		
		setupDefaultMocks();
		
		final Comment commentObject = AndroidMock.createMock(Comment.class);
		Entity entity = AndroidMock.createMock(Entity.class);
		
		commentObject.setText(comment);
		commentObject.setEntity(entity);
		
		commentSystem.addComment(AndroidMock.eq(session), AndroidMock.eq(commentObject), (Location) AndroidMock.isNull(), (ShareOptions) AndroidMock.isNull(), AndroidMock.eq(listener));
		
		replayDefaultMocks();
		
		AndroidMock.replay(commentObject);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl()  {

			@Override
			protected Comment newComment() {
				return commentObject;
			}
		};
		
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addComment(getActivity(), entity, comment, listener);
		
		verifyDefaultMocks();
		
		AndroidMock.verify(commentObject);
	}
	
	@UsesMocks ({LikeAddListener.class})
	public void testAddLike() {
		LikeAddListener listener = AndroidMock.createMock(LikeAddListener.class);
		
		setupDefaultMocks();
		
		final String key = "foo";
		
		likeSystem.addLike(AndroidMock.eq(session), (Entity) AndroidMock.anyObject(), (Location) AndroidMock.isNull(), AndroidMock.eq(listener));
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.like(getActivity(), Entity.newInstance(key, null), listener);
		
		verifyDefaultMocks();
	}
	
	public void authenticateWithTypeAndListener () {
		//(AuthProviderType authProviderType, SocializeAuthListener authListener) {
		//authenticate(Context context, AuthProviderType authProviderType, SocializeAuthListener authListener) {
	}
	 
	@UsesMocks({SocializeUserSystem.class, UserProfile.class, UserSaveListener.class})
	public void testSaveCurrentUserProfile() { 
		
		session = AndroidMock.createMock(SocializeSession.class);
		userSystem = AndroidMock.createMock(SocializeUserSystem.class, AndroidMock.createMock(SocializeProvider.class));
		
		UserProfile mockProfile = AndroidMock.createMock(UserProfile.class);
		final UserSaveListener mockListener = AndroidMock.createMock(UserSaveListener.class);
		
		PublicSocialize socializeService = new PublicSocialize() {
			@Override
			public boolean assertAuthenticated(SocializeListener listener) {
				assertEquals(mockListener, listener);
				return true;
			}
		};
		
		//we'll use the default mocks already created for sessions/userSystem
		socializeService.setSession(session);
		socializeService.setUserSystem(userSystem);
		userSystem.saveUserProfile(getContext(), session, mockProfile, mockListener);
		
		
		AndroidMock.replay( mockProfile, mockListener );
		
		socializeService.saveCurrentUserProfile(getContext(), mockProfile,mockListener);
		
		//verify default and newly created mocks
		AndroidMock.verify( mockProfile, mockListener);
	}
	@UsesMocks({SocializeSession.class, UserSystem.class, AuthProvider.class})
	public void testClear3rdPartySession() {
		PublicSocialize socializeService = new PublicSocialize();
	
		//create session mock and add expect methods
		String thirdPartyId = "3rdpartyId";
		AuthProvider mockAuthProvider = AndroidMock.createMock(AuthProvider.class);
		SocializeSession mockSession = AndroidMock.createMock(SocializeSession.class);
		AndroidMock.expect(mockSession.getAuthProvider()).andReturn(mockAuthProvider);
		AndroidMock.expect(mockSession.get3rdPartyAppId()).andReturn(thirdPartyId);
		
		mockSession.clear(AuthProviderType.FACEBOOK);
		socializeService.setSession(mockSession);
		
		//create UserSystem mock
		UserSystem userSystemMock = AndroidMock.createMock(UserSystem.class);
		userSystemMock.clearSession(AuthProviderType.FACEBOOK);
		socializeService.setUserSystem(userSystemMock);
		
		//add mocks to be replayed
		AndroidMock.replay(userSystemMock, mockSession);
		
		//run the method
		socializeService.clear3rdPartySession(getContext(), AuthProviderType.FACEBOOK);
		
		//verify the mocks
		AndroidMock.verify(userSystemMock, mockSession);
	}
	@UsesMocks({SocializeAuthListener.class})
	public void testCheckKeys() {
		PublicSocialize socializeService = new PublicSocialize();
		SocializeAuthListener mockAuthListener = AndroidMock.createMock(SocializeAuthListener.class);
		mockAuthListener.onError((SocializeException)AndroidMock.anyObject());
		AndroidMock.replay( mockAuthListener );
		//this should test to make sure an error is passed back to the auth listener
		socializeService.checkKeys("", "", mockAuthListener);
		AndroidMock.verify(mockAuthListener);
	}
	
	@UsesMocks ({ShareAddListener.class, Location.class})
	public void testAddShareWithLocation() {
		ShareAddListener listener = AndroidMock.createMock(ShareAddListener.class);
		Location location = AndroidMock.createMock(Location.class, "foobar");
		
		setupDefaultMocks();
		
		final String key = "foo";
		final String text = "bar";
		
		shareSystem.addShare(AndroidMock.eq(session), (Entity) AndroidMock.anyObject(), AndroidMock.eq(text), AndroidMock.eq(ShareType.OTHER), AndroidMock.eq(location), AndroidMock.eq(listener));

//		shareSystem.addShare(session, key, text, ShareType.OTHER, location, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addShare(getActivity(), Entity.newInstance(key, null), text, ShareType.OTHER, location, listener);
		
		verifyDefaultMocks();
	}	
	
	@UsesMocks ({ShareAddListener.class})
	public void testAddShareWithoutLocation() {
		
		final String key = "foo";
		final String text = "bar";
		
		ShareAddListener listener = AndroidMock.createMock(ShareAddListener.class);
		
		setupDefaultMocks();
		
		shareSystem.addShare(AndroidMock.eq(session), (Entity) AndroidMock.anyObject(), AndroidMock.eq(text), AndroidMock.eq(ShareType.OTHER), (Location) AndroidMock.isNull(), AndroidMock.eq(listener));
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		socialize.addShare(getActivity(), Entity.newInstance(key, null), text, ShareType.OTHER, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({ViewAddListener.class})
	public void testAddView() {
		ViewAddListener listener = AndroidMock.createMock(ViewAddListener.class);
		
		final String key = "foo";
		
		setupDefaultMocks();
		
		viewSystem.addView(AndroidMock.eq(session), (Entity) AndroidMock.anyObject(), (Location) AndroidMock.isNull(), AndroidMock.eq(listener));

//		viewSystem.addView(session, key, null, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.view(getActivity(), Entity.newInstance(key, null), listener);
		
		AndroidMock.verify(container);
	}
	
	@UsesMocks ({EntityAddListener.class})
	public void testCreateEntitySuccess() {
		EntityAddListener listener = AndroidMock.createMock(EntityAddListener.class);
		
		setupDefaultMocks();
		
		final String key = "foo", name = "bar";
		
		entitySystem.addEntity(AndroidMock.eq(session), (Entity) AndroidMock.anyObject(), AndroidMock.eq(listener));
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addEntity(getActivity(), Entity.newInstance(key, name), listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByEntity() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final String key = "foo";
		
		setupDefaultMocks();
		
		commentSystem.getCommentsByEntity(session, key, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByEntity(key,listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByEntityPaginated() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final String key = "foo";
		final int start = 0, end = 10;
		
		setupDefaultMocks();

		commentSystem.getCommentsByEntity(session, key, start, end, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByEntity(key, start, end,listener);
		
		verifyDefaultMocks();
	}
	

	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByUser() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final long key = 69;
		
		setupDefaultMocks();
		
		commentSystem.getCommentsByUser(session, key, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByUser(key,listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByUserPaginated() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final long key = 69;
		final int start = 0, end = 10;
		
		setupDefaultMocks();

		commentSystem.getCommentsByUser(session, key, start, end, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByUser(key, start, end,listener);
		
		verifyDefaultMocks();
	}
	

	@UsesMocks ({LikeListListener.class})
	public void testListLikesByUser() {
		LikeListListener listener = AndroidMock.createMock(LikeListListener.class);
		
		final long key = 69;
		
		setupDefaultMocks();
		
		likeSystem.getLikesByUser(session, key, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listLikesByUser(key,listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({LikeListListener.class})
	public void testListLikesByUserPaginated() {
		LikeListListener listener = AndroidMock.createMock(LikeListListener.class);
		
		final long key = 69;
		final int start = 0, end = 10;
		
		setupDefaultMocks();

		likeSystem.getLikesByUser(session, key, start, end, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listLikesByUser(key, start, end,listener);
		
		verifyDefaultMocks();
	}		
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByIds() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final long[] ids = {1,2,3};
		
		setupDefaultMocks();

		commentSystem.getCommentsById(session, listener, ids);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsById(listener, ids);
		
		verifyDefaultMocks();
	}

	@UsesMocks ({CommentGetListener.class})
	public void testGetCommentById() {
		CommentGetListener listener = AndroidMock.createMock(CommentGetListener.class);
		
		final int id = 1;
		
		setupDefaultMocks();
		
		commentSystem.getComment(session, id, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getCommentById(id, listener);
		
		verifyDefaultMocks();
	}
	
	
	@UsesMocks ({EntityGetListener.class})
	public void testGetEntity() {
		EntityGetListener listener = AndroidMock.createMock(EntityGetListener.class);
		
		final String key = "foo";
		
		setupDefaultMocks();

		entitySystem.getEntity(session, key, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getEntity(key, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({LikeGetListener.class})
	public void testGetLikeById() {
		LikeGetListener listener = AndroidMock.createMock(LikeGetListener.class);
		
		final int id = 1;
		
		setupDefaultMocks();
		
		likeSystem.getLike(session, id, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getLikeById(id, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({LikeListListener.class})
	public void testListLikesById() {
		LikeListListener listener = AndroidMock.createMock(LikeListListener.class);
		
		final long id[] = {1,2,3};
		
		setupDefaultMocks();
		
		likeSystem.getLikesById(session, listener, id);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listLikesById(listener, id);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({LikeGetListener.class})
	public void testGetLikeByKey() {
		LikeGetListener listener = AndroidMock.createMock(LikeGetListener.class);
		
		final String key = "foobar";
		
		setupDefaultMocks();

		likeSystem.getLike(session, key, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getLike(key, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({LikeDeleteListener.class})
	public void testDeleteLike() {
		LikeDeleteListener listener = AndroidMock.createMock(LikeDeleteListener.class);
		
		final int id = 1;
		
		setupDefaultMocks();

		likeSystem.deleteLike(session, id, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.unlike(id, listener);
		
		verifyDefaultMocks();
	}
	

	 
	@UsesMocks ({SocializeAuthListener.class})
	public void  testAuthenticateWithListener() {
		final SocializeAuthListener mockListener = AndroidMock.createMock(SocializeAuthListener.class);
		PublicSocialize socializeService = new PublicSocialize() {
			@Override
			public void authenticate(Context context, SocializeAuthListener authListener) {
				assertEquals(authListener, mockListener);
			}
		};
		socializeService.authenticate( mockListener );
	}
	@UsesMocks ({SocializeAuthListener.class, IBeanFactory.class, AuthProviderData.class})
	public void testAuthenticate() throws SocializeException {
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo", secret = "bar";
		
		setupDefaultMocks();
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		
		authProviderData.setAuthProviderType(AuthProviderType.SOCIALIZE);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		
		userSystem.authenticate(getActivity(), key, secret, authProviderData, listener, socialize, false);

		replayDefaultMocks();
		
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticate(getActivity(), key, secret, listener);
		
		verifyDefaultMocks();
	}
	
	
	public void testAuthenticateWithExtraParamsCallAuthenticate() throws SocializeException {
	
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo", secret = "bar";
		final String appId = "foobar";
		
		setupDefaultMocks();
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		
		authProviderData.setAuthProviderType(AuthProviderType.FACEBOOK);
		authProviderData.setAppId3rdParty(appId);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		
		userSystem.authenticate(getActivity(), key, secret, authProviderData, listener, socialize, true);

		replayDefaultMocks();
		
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticate(getActivity(), key, secret, AuthProviderType.FACEBOOK, appId, listener);
		
		verifyDefaultMocks();
	}
	
	
	public void testAuthenticateKnownUser() throws SocializeException {
		SocializeAuthListener authListener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String consumerKey = "foo", consumerSecret = "bar";
		final String authProviderId = "foobar", authUserId3rdParty = "foobar_user", authToken3rdParty = "foobar_token";
		
		setupDefaultMocks();
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		
		authProviderData.setAuthProviderType(AuthProviderType.FACEBOOK);
		authProviderData.setAppId3rdParty(authProviderId);
		authProviderData.setToken3rdParty(authToken3rdParty);
		authProviderData.setUserId3rdParty(authUserId3rdParty);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		
		userSystem.authenticate(getActivity(), consumerKey, consumerSecret, authProviderData, authListener, socialize, false);

		replayDefaultMocks();
		
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticateKnownUser(getActivity(), consumerKey, consumerSecret, AuthProviderType.FACEBOOK, authProviderId, authUserId3rdParty, authToken3rdParty, authListener);
		
		verifyDefaultMocks();
		
	}
	
	public void testIsAuthenticatedWithProvider() {
		
		
		session = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.expect(session.getAuthProviderType()).andReturn(AuthProviderType.FACEBOOK);

		AndroidMock.replay(session);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl() {
			@Override
			public boolean isAuthenticated() {
				return true;
			}
		};
		
		socialize.setSession(session);
		
		assertTrue(socialize.isAuthenticated(AuthProviderType.FACEBOOK));
		
		AndroidMock.verify(session);
	}
	
	public void testAddCommentFail() {
		
		final String key = "foo", comment = "bar";
		
		CommentAddListener listener = new CommentAddListener() {
			@Override
			public void onError(SocializeException error) {
				addResult(error);
			}

			@Override
			public void onCreate(Comment entity) {}
		};

		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.setSession(session);
		
		assertFalse(socialize.isInitialized());
		
		socialize.addComment(getActivity(), Entity.newInstance(key, null), comment, listener);
		
		Exception error = getNextResult();
		
		assertNotNull(error);
		assertTrue(error instanceof SocializeException);
		
	}
	
	public void testNotAuthenticated() {
		
		setupDefaultMocks();
		replayDefaultMocks();
		
		final String key = "foo", comment = "bar";
		
		CommentAddListener listener = new CommentAddListener() {
			@Override
			public void onError(SocializeException error) {
				addResult(error);
			}

			@Override
			public void onCreate(Comment entity) {}
		};

		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		assertFalse(socialize.isAuthenticated());
		
		socialize.addComment(getActivity(), Entity.newInstance(key, null), comment, listener);
		
		Exception error = getNextResult();
		
		assertNotNull(error);
		assertTrue(error instanceof SocializeException);
		
		verifyDefaultMocks();
	}
	
	
	@UsesMocks ({EntityListListener.class})
	public void testListEntities() {
		EntityListListener listener = AndroidMock.createMock(EntityListListener.class);
		
		final String[] ids = {"A","B","C"};
		
		setupDefaultMocks();
		
		entitySystem.listEntities(session, listener, ids);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listEntitiesByKey(listener, ids);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({IOCContainer.class, SocializeConfig.class})
	public void testInitAndGetConfig() {
		
		setupDefaultMocks();
	
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(new MockContext(), container);
		
		SocializeConfig gotten = socialize.getConfig();
		
		verifyDefaultMocks();
		
		assertSame(config, gotten);
	}
	
	@UsesMocks (AuthProvider.class)
	public void testClearSessionCache() {
		setupDefaultMocks();
		
		AuthProvider authProvider = AndroidMock.createMock(AuthProvider.class);
		
		final String get3rdPartyAppId = "foobar";
		
		AndroidMock.expect(session.getAuthProvider()).andReturn(authProvider);
		AndroidMock.expect(session.get3rdPartyAppId()).andReturn(get3rdPartyAppId);
		
		authProvider.clearCache(getContext(), get3rdPartyAppId);
		userSystem.clearSession();
		
		replayDefaultMocks();
		
		AndroidMock.replay(authProvider);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.setSession(session);
		socialize.init(getContext(), container);
		
		socialize.clearSessionCache(getContext());
		
		AndroidMock.verify(authProvider);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({MockContext.class, SocializeInitListener.class, SocializeServiceImpl.class})
	public void testInitTaskDoInBackground() throws Exception {
		
		MockContext context = AndroidMock.createMock(MockContext.class);
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeServiceImpl socialize = AndroidMock.createMock(SocializeServiceImpl.class);
		String[] paths = {"foo", "bar"};
		
		AndroidMock.expect(socialize.initWithContainer(context, paths)).andReturn(null);
		
		AndroidMock.replay(socialize);
		
		InitTask task = new InitTask(socialize, context, paths, listener, logger);
		
		assertTrue(AsyncTask.class.isAssignableFrom(task.getClass()));
		
		task.doInBackground((Void[])null);
		
		AndroidMock.verify(socialize);
		
	}
	
	@UsesMocks ({
		MockContext.class, 
		SocializeInitListener.class, 
		SocializeServiceImpl.class, 
		IOCContainer.class})
	public void testInitTaskOnPostExecute() throws Exception {
		
		MockContext context = AndroidMock.createMock(MockContext.class);
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeServiceImpl socialize = AndroidMock.createMock(SocializeServiceImpl.class);
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		String[] paths = {"foo", "bar"};
		
		listener.onInit(context, container);
		
		AndroidMock.replay(listener);
		
		InitTask task = new InitTask(socialize, context, paths, listener, logger);
		
		task.onPostExecute(container);
		
		AndroidMock.verify(listener);
		
	}
	
	
	@UsesMocks ({
		MockContext.class, 
		SocializeInitListener.class, 
		SocializeServiceImpl.class, 
		IOCContainer.class})
	public void testInitTaskOnPostExecuteWithError() throws Exception {
		
		MockContext context = AndroidMock.createMock(MockContext.class);
		SocializeInitListener listener = AndroidMock.createMock(SocializeInitListener.class);
		SocializeServiceImpl socialize = AndroidMock.createMock(SocializeServiceImpl.class);
		SocializeException error = new SocializeException();
		String[] paths = {"foo", "bar"};
		
		AndroidMock.expect(socialize.initWithContainer(context, paths)).andThrow(error);
		
		listener.onError(error);
		
		AndroidMock.replay(socialize);
		AndroidMock.replay(listener);
		
		InitTask task = new InitTask(socialize, context, paths, listener, logger);
		
		task.doInBackground((Void[])null);
		
		task.onPostExecute(null);
		
		AndroidMock.verify(socialize);
		AndroidMock.verify(listener);
	}
	
	@UsesMocks ({SocializeIOC.class, ResourceLocator.class, ClassLoaderProvider.class, SocializeLogger.class})
	public void testInitWithContainer() throws Exception {
		
		final SocializeIOC socializeIOC = AndroidMock.createMock(SocializeIOC.class);
		final ClassLoaderProvider classLoaderProvider = AndroidMock.createMock(ClassLoaderProvider.class);
		final ResourceLocator resourceLocator = AndroidMock.createMock(ResourceLocator.class);
		final SocializeLogger logger = AndroidMock.createMock(SocializeLogger.class);
		
		final Context context = new MockContext();
		
		final String[] mockPaths = {"foobar"};
		final String[] initPaths = {};
		
		SocializeServiceImpl service = new SocializeServiceImpl() {
			
			@Override
			public void init(Context context, IOCContainer container) {
				addResult(0, context);
				addResult(1, container);
			}

			@Override
			public boolean isInitialized() {
				return true;
			}

			@Override
			protected SocializeIOC newSocializeIOC() {
				return socializeIOC;
			}

			@Override
			protected ResourceLocator newResourceLocator() {
				return resourceLocator;
			}

			@Override
			protected ClassLoaderProvider newClassLoaderProvider() {
				return classLoaderProvider;
			}
			
			@Override
			protected int binarySearch(String[] array, String str) {
				// Simulate not found
				addResult(2, "binarySearch_" + str);
				return -1;
			}
			
			@Override
			public void destroy() {
				addResult(3, "destroy");
			}

			@Override
			protected void sort(Object[] array) {
				addResult(4, "sort");
			}

			@Override
			protected SocializeLogger newLogger() {
				return logger;
			}

			@Override
			protected String[] getInitPaths() {
				return initPaths;
			}
		};
		
		
		resourceLocator.setClassLoaderProvider(classLoaderProvider);
		resourceLocator.setLogger(logger);
		
		socializeIOC.init(context, resourceLocator, mockPaths);
		
		AndroidMock.replay(socializeIOC);
		AndroidMock.replay(resourceLocator);
		
		service.initWithContainer(context, mockPaths);
		
		AndroidMock.verify(socializeIOC);
		AndroidMock.verify(resourceLocator);
		
		Context foundContext = getResult(0);
		IOCContainer foundContainer = getResult(1);
		String binarySearch = getResult(2);
		String destroy = getResult(3);
		String sort = getResult(4);
		
		assertNotNull(binarySearch);
		assertNotNull(destroy);
		assertNotNull(sort);
		assertNotNull(foundContext);
		assertNotNull(foundContainer);
		
		assertEquals("binarySearch_foobar", binarySearch);
		assertEquals("destroy", destroy);
		assertEquals("sort", sort);
		assertSame(context, foundContext);
		assertSame(socializeIOC, foundContainer);
	}
	

	public void testSetSocializeCredentials() {
		PublicSocialize socializeUI = new PublicSocialize();

		String consumerKey = "foo";
		String consumerSecret = "bar";

		socializeUI.getConfig().setSocializeCredentials(consumerKey, consumerSecret);

		assertEquals(consumerKey, socializeUI.getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY));
		assertEquals(consumerSecret, socializeUI.getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET));
	}

	public void testSetFacebookUserCredentials() {
		PublicSocialize socializeUI = new PublicSocialize();

		String userId = "foo";
		String token = "bar";

		socializeUI.getConfig().setFacebookUserCredentials(userId, token);

		assertEquals(userId, socializeUI.getConfig().getProperty(SocializeConfig.FACEBOOK_USER_ID));
		assertEquals(token, socializeUI.getConfig().getProperty(SocializeConfig.FACEBOOK_USER_TOKEN));
	}

	public void testSetFacebookAppId() {
		PublicSocialize socializeUI = new PublicSocialize();

		String appId = "foobar";

		socializeUI.getConfig().setFacebookAppId(appId);

		assertEquals(appId, socializeUI.getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID));
	}
}
