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
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.init.SocializeInitializationAsserter;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
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
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

/**
 * @author Jason Polites
 *
 */
@UsesMocks({IOCContainer.class, SocializeApiHost.class, SocializeSession.class, SocializeLogger.class, SocializeInitializationAsserter.class})
public class SocializeServiceTest extends SocializeUnitTest {
	
	IOCContainer container;
	SocializeApiHost service;
	SocializeLogger logger;
	IBeanFactory<AuthProviderData> authProviderDataFactory;
	AuthProviderData authProviderData;
	SocializeSession session;
	
	@SuppressWarnings("unchecked")
	private void setupDefaultMocks() {
		container = AndroidMock.createMock(IOCContainer.class);
		service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		logger = AndroidMock.createNiceMock(SocializeLogger.class);
		authProviderDataFactory = (IBeanFactory<AuthProviderData>) AndroidMock.createMock(IBeanFactory.class);
		authProviderData = AndroidMock.createMock(AuthProviderData.class);
		session = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.expect(container.getBean("authProviderDataFactory")).andReturn(authProviderDataFactory);
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		AndroidMock.expect(container.getBean("initializationAsserter")).andReturn(null);
	}
	
	private void replayDefaultMocks() {
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		AndroidMock.replay(logger);
		AndroidMock.replay(authProviderDataFactory);
		AndroidMock.replay(authProviderData);
		AndroidMock.replay(session);
	}
	
	private void verifyDefaultMocks() {
		AndroidMock.verify(container);
		AndroidMock.verify(service);
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
	
	@UsesMocks ({CommentAddListener.class})
	public void testAddCommentSuccess() {
		CommentAddListener listener = AndroidMock.createMock(CommentAddListener.class);
		
		final String key = "foo", comment = "bar";
		
		setupDefaultMocks();
		
		service.addComment(session, key, comment, null, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addComment(key, comment, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({LikeAddListener.class})
	public void testAddLike() {
		LikeAddListener listener = AndroidMock.createMock(LikeAddListener.class);
		
		setupDefaultMocks();
		
		final String key = "foo";
		
		service.addLike(session, key, null, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.like(key, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({ShareAddListener.class, Location.class})
	public void testAddShareWithLocation() {
		ShareAddListener listener = AndroidMock.createMock(ShareAddListener.class);
		Location location = AndroidMock.createMock(Location.class, "foobar");
		
		setupDefaultMocks();
		
		final String key = "foo";
		final String text = "bar";
		
		service.addShare(session, key, text, ShareType.OTHER, location, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.share(key, text, ShareType.OTHER, location, listener);
		
		verifyDefaultMocks();
	}	
	
	@UsesMocks ({ShareAddListener.class})
	public void testAddShareWithoutLocation() {
		
		final String key = "foo";
		final String text = "bar";
		
		ShareAddListener listener = AndroidMock.createMock(ShareAddListener.class);
		
		setupDefaultMocks();
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl() {

			@Override
			public void share(String url, String text, ShareType shareType, Location location, ShareAddListener shareAddListener) {
				addResult(url);
				addResult(text);
				addResult(shareType);
				addResult(shareAddListener);
			}
			
		};
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.share(key, text, ShareType.OTHER, listener);
		
		String urlAfter = getNextResult();
		String textAfter = getNextResult();
		ShareType shareTypeAfter = getNextResult();
		ShareAddListener listenerAfter = getNextResult();
		
		assertNotNull(listenerAfter);
		assertNotNull(shareTypeAfter);
		assertNotNull(textAfter);
		assertNotNull(urlAfter);
		
		assertSame(listener, listenerAfter);
		assertEquals(ShareType.OTHER, shareTypeAfter);
		assertEquals(key, urlAfter);
		assertEquals(text, textAfter);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({ViewAddListener.class})
	public void testAddView() {
		ViewAddListener listener = AndroidMock.createMock(ViewAddListener.class);
		
		final String key = "foo";
		
		setupDefaultMocks();

		service.addView(session, key, null, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.view(key, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({EntityAddListener.class})
	public void testCreateEntitySuccess() {
		EntityAddListener listener = AndroidMock.createMock(EntityAddListener.class);
		
		setupDefaultMocks();
		
		final String key = "foo", name = "bar";
		
		service.createEntity(session, key, name, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addEntity(key, name, listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByEntity() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final String key = "foo";
		
		setupDefaultMocks();
		
		service.listCommentsByEntity(session, key, listener);
		
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

		service.listCommentsByEntity(session, key, start, end, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByEntity(key, start, end,listener);
		
		verifyDefaultMocks();
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByIds() {
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		
		final int[] ids = {1,2,3};
		
		setupDefaultMocks();

		service.listCommentsById(session, listener, ids);
		
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
		
		service.getComment(session, id, listener);
		
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

		service.getEntity(session, key, listener);
		
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
		
		service.getLike(session, id, listener);
		
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
		
		final int id[] = {1,2,3};
		
		setupDefaultMocks();
		
		service.listLikesById(session, listener, id);
		
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

		service.getLike(session, key, listener);
		
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

		service.deleteLike(session, id, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.unlike(id, listener);
		
		verifyDefaultMocks();
	}
	
	
	@UsesMocks ({SocializeAuthListener.class, IBeanFactory.class, AuthProviderData.class})
	public void testAuthenticate() throws SocializeException {
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo", secret = "bar";
		
		setupDefaultMocks();
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		
		authProviderData.setAuthProviderType(AuthProviderType.SOCIALIZE);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		
		service.authenticate(key, secret, authProviderData, listener, socialize, false);

		replayDefaultMocks();
		
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticate(key, secret, listener);
		
		verifyDefaultMocks();
	}
	
//	@UsesMocks ({SocializeConfig.class})
//	public void testAuthenticateWithout3rdPartyId() throws Exception {
//		final String appId = "foobar";
//		
//		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
//	
//		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(appId);
//		
//		AndroidMock.replay(config);
//		
//		SocializeServiceImpl service = new SocializeServiceImpl() {
//
//			@Override
//			public SocializeConfig getConfig() {
//				return config;
//			}
//
//			@Override
//			public void authenticate(String consumerKey, String consumerSecret, AuthProviderType authProviderType, String authProviderAppId, SocializeAuthListener authListener) {
//				addResult(authProviderAppId);
//			}
//		};
//		
//		service.authenticate("foo", "bar", AuthProviderType.FACEBOOK, null);
//		
//		String result = getNextResult();
//		
//		assertNotNull(result);
//		assertEquals(appId, result);
//		
//		AndroidMock.verify(config);
//		
//	}
	
	
//	@UsesMocks ({SocializeConfig.class})
//	public void testAuthenticateWithout3rdPartyIdMissingConfig() throws Exception {
//		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
//	
//		AndroidMock.expect(config.getProperty(SocializeConfig.FACEBOOK_APP_ID)).andReturn(null);
//		
//		AndroidMock.replay(config);
//		
//		SocializeServiceImpl service = new SocializeServiceImpl() {
//
//			@Override
//			public SocializeConfig getConfig() {
//				return config;
//			}
//
//			@Override
//			public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
//				addResult(true);
//			}
//		};
//		
//		service.authenticate("foo", "bar", AuthProviderType.FACEBOOK, null);
//		
//		Boolean result = getNextResult();
//		
//		assertNotNull(result);
//		assertTrue(result);
//		
//		AndroidMock.verify(config);
//		
//	}
	
//	@UsesMocks ({SocializeConfig.class})
//	public void testAuthenticateWithout3rdPartyIdSocialize() throws Exception {
//		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
//	
//		SocializeServiceImpl service = new SocializeServiceImpl() {
//
//			@Override
//			public SocializeConfig getConfig() {
//				return config;
//			}
//
//			@Override
//			public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener authListener) {
//				addResult(true);
//			}
//		};
//		
//		service.authenticate("foo", "bar", AuthProviderType.SOCIALIZE, null);
//		
//		Boolean result = getNextResult();
//		
//		assertNotNull(result);
//		assertTrue(result);
//		
//	}
	
	public void testAuthenticateWithExtraParamsCallAuthenticate() throws SocializeException {
	
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		final String key = "foo", secret = "bar";
		final String appId = "foobar";
		
		setupDefaultMocks();
		
		AndroidMock.expect(authProviderDataFactory.getBean()).andReturn(authProviderData);
		
		authProviderData.setAuthProviderType(AuthProviderType.FACEBOOK);
		authProviderData.setAppId3rdParty(appId);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		
		service.authenticate(key, secret, authProviderData, listener, socialize, true);

		replayDefaultMocks();
		
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticate(key, secret, AuthProviderType.FACEBOOK, appId, listener);
		
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
		
		service.authenticate(consumerKey, consumerSecret, authProviderData, authListener, socialize, false);

		replayDefaultMocks();
		
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticateKnownUser(consumerKey, consumerSecret, AuthProviderType.FACEBOOK, authProviderId, authUserId3rdParty, authToken3rdParty, authListener);
		
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
		
		socialize.addComment(key, comment, listener);
		
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
		
		socialize.addComment(key, comment, listener);
		
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
		
		service.listEntitiesByKey(session, listener, ids);
		
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
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		AndroidMock.expect(container.getBean("config")).andReturn(config);
	
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
		
		authProvider.clearCache(get3rdPartyAppId);
		service.clearSessionCache();
		
		replayDefaultMocks();
		
		AndroidMock.replay(authProvider);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.setSession(session);
		socialize.init(getContext(), container);
		
		socialize.clearSessionCache();
		
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
		
		SocializeServiceImpl service = new SocializeServiceImpl() {
			
			@Override
			public void init(Context context, IOCContainer container) {
				addResult(context);
				addResult(container);
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
				addResult("binarySearch_" + str);
				return -1;
			}
			
			@Override
			public void destroy() {
				addResult("destroy");
			}

			@Override
			protected void sort(Object[] array) {
				addResult("sort");
			}

			@Override
			protected SocializeLogger newLogger() {
				return logger;
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
		
		// Reverse order for asserts
		String binarySearch = getNextResult();
		String destroy = getNextResult();
		String sort = getNextResult();
		Context foundContext = getNextResult();
		IOCContainer foundContainer = getNextResult();
		
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
}
