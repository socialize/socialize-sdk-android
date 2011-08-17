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

import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeServiceImpl;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
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
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks({IOCContainer.class, SocializeApiHost.class, SocializeSession.class, SocializeLogger.class})
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
	public void testGetComment() {
		CommentGetListener listener = AndroidMock.createMock(CommentGetListener.class);
		
		final int id = 1;
		
		setupDefaultMocks();
		
		service.getComment(session, id, listener);
		
		replayDefaultMocks();
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getComment(id, listener);
		
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
		
		socialize.getComment(id, listener);
		
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
	
}
