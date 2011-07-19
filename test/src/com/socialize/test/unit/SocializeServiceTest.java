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
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityCreateListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.log.SocializeLogger;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks({IOCContainer.class, SocializeApiHost.class, SocializeSession.class, SocializeLogger.class})
public class SocializeServiceTest extends SocializeUnitTest {
	
	public void testSocializeInitDestroy() {
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		container.destroy();
		
		AndroidMock.replay(container);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.destroy();
		
		assertFalse(socialize.isInitialized());
		
		AndroidMock.verify(container);
	}
	
	public void testSocializeMultiInitDestroy() {
		
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		
		 //Only once
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(null);

		container.destroy();
		
		AndroidMock.replay(container);
		
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
		
		AndroidMock.verify(container);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeLogger.class})
	public void testInitFail() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeLogger logger = AndroidMock.createMock(SocializeLogger.class);
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
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		CommentAddListener listener = AndroidMock.createMock(CommentAddListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo", comment = "bar";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		service.addComment(session, key, comment, null, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addComment(key, comment, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({LikeAddListener.class})
	public void testAddLike() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		LikeAddListener listener = AndroidMock.createMock(LikeAddListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		service.addLike(session, key, null, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.like(key, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({EntityCreateListener.class})
	public void testCreateEntitySuccess() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		EntityCreateListener listener = AndroidMock.createMock(EntityCreateListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo", name = "bar";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		service.createEntity(session, key, name, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.createEntity(key, name, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByEntity() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listCommentsByEntity(session, key, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByEntity(key,listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByEntityPaginated() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo";
		final int start = 0, end = 10;
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listCommentsByEntity(session, key, start, end, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByEntity(key, start, end,listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({CommentListListener.class})
	public void testListCommentsByIds() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		CommentListListener listener = AndroidMock.createMock(CommentListListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final int[] ids = {1,2,3};
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listCommentsById(session, listener, ids);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsById(listener, ids);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({CommentGetListener.class})
	public void testGetComment() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		CommentGetListener listener = AndroidMock.createMock(CommentGetListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final int id = 1;
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.getComment(session, id, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getComment(id, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({EntityGetListener.class})
	public void testGetEntity() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		EntityGetListener listener = AndroidMock.createMock(EntityGetListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.getEntity(session, key, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getEntity(key, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({LikeGetListener.class})
	public void testGetLikeById() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		LikeGetListener listener = AndroidMock.createMock(LikeGetListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final int id = 1;
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.getLike(session, id, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getLikeById(id, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({LikeGetListener.class})
	public void testGetLikeByKey() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		LikeGetListener listener = AndroidMock.createMock(LikeGetListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foobar";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.getLike(session, key, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getLike(key, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({LikeDeleteListener.class})
	public void testDeleteLike() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		LikeDeleteListener listener = AndroidMock.createMock(LikeDeleteListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final int id = 1;
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.deleteLike(session, id, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.unlike(id, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	
	@UsesMocks ({SocializeAuthListener.class})
	public void testAuthenticate() throws SocializeException {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo", secret = "bar";
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		AndroidMock.replay(container);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		
		service.authenticate(key, secret, listener, socialize);

		AndroidMock.replay(service);
		
		assertTrue(socialize.isInitialized());
		
		socialize.authenticate(key, secret, listener);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	public void testAddCommentFail() {
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
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
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(null);
		
		AndroidMock.replay(container);
		
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
		
	}
	
	
	@UsesMocks ({EntityListListener.class})
	public void testListEntities() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		EntityListListener listener = AndroidMock.createMock(EntityListListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String[] ids = {"A","B","C"};
		
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listEntitiesByKey(session, listener, ids);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listEntitiesByKey(listener, ids);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeConfig.class})
	public void testInitAndGetConfig() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		AndroidMock.expect(container.getBean("socializeApiHost")).andReturn(null);
		AndroidMock.expect(container.getBean("logger")).andReturn(null);
		AndroidMock.expect(container.getBean("config")).andReturn(config);
	
		AndroidMock.replay(container);
		
		SocializeServiceImpl socialize = new SocializeServiceImpl();
		socialize.init(new MockContext(), container);
		
		SocializeConfig gotten = socialize.getConfig();
		
		AndroidMock.verify(container);
		
		assertSame(config, gotten);
	}
	
}
