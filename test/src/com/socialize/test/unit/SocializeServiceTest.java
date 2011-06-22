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
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.entity.EntityCreateListener;
import com.socialize.listener.entity.EntityListListener;
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		container.destroy();
		
		AndroidMock.replay(container);
		
		SocializeService socialize = new SocializeService();
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		
		socialize.destroy();
		
		assertFalse(socialize.isInitialized());
		
		AndroidMock.verify(container);
	}
	
	@UsesMocks ({CommentAddListener.class})
	public void testAddCommentSuccess() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		CommentAddListener listener = AndroidMock.createMock(CommentAddListener.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeLogger logger = AndroidMock.createNiceMock(SocializeLogger.class);
		
		final String key = "foo", comment = "bar";
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		service.addComment(session, key, comment, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeService socialize = new SocializeService();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.addComment(key, comment, listener);
		
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		service.createEntity(session, key, name, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeService socialize = new SocializeService();
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listCommentsByEntity(session, key, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeService socialize = new SocializeService();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listCommentsByEntity(key,listener);
		
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listCommentsById(session, listener, ids);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeService socialize = new SocializeService();
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.getComment(session, id, listener);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeService socialize = new SocializeService();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.getComment(id, listener);
		
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);
		
		AndroidMock.replay(container);
		
		SocializeService socialize = new SocializeService();
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

		SocializeService socialize = new SocializeService();
		socialize.setSession(session);
		
		assertFalse(socialize.isInitialized());
		
		socialize.addComment(key, comment, listener);
		
		Exception error = getResult();
		
		assertNotNull(error);
		assertTrue(error instanceof SocializeException);
		
	}
	
	public void testNotAuthenticated() {
		IOCContainer container = AndroidMock.createMock(IOCContainer.class);
		SocializeApiHost service = AndroidMock.createMock(SocializeApiHost.class, getContext());
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
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

		SocializeService socialize = new SocializeService();
		socialize.init(getContext(), container);
		
		assertTrue(socialize.isInitialized());
		assertFalse(socialize.isAuthenticated());
		
		socialize.addComment(key, comment, listener);
		
		Exception error = getResult();
		
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
		
		AndroidMock.expect(container.getBean("socializeService")).andReturn(service);
		AndroidMock.expect(container.getBean("logger")).andReturn(logger);

		service.listEntitiesByKey(session, listener, ids);
		
		AndroidMock.replay(container);
		AndroidMock.replay(service);
		
		SocializeService socialize = new SocializeService();
		socialize.init(getContext(), container);
		socialize.setSession(session);
		
		assertTrue(socialize.isInitialized());
		
		socialize.listEntitiesByKey(listener, ids);
		
		AndroidMock.verify(container);
		AndroidMock.verify(service);
	}
	
}
