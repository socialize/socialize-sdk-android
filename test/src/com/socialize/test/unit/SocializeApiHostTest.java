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
import com.socialize.api.SocializeApiHost;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.CommentApi;
import com.socialize.api.action.EntityApi;
import com.socialize.api.action.LikeApi;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.listener.like.LikeListener;
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
	@UsesMocks ({SocializeProvider.class, CommentApi.class, DeviceUtils.class, SocializeSession.class, SocializeAuthListener.class, SocializeSessionConsumer.class})
	public void testAuthenticate() throws SocializeException, InterruptedException {
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		CommentApi commentApi = AndroidMock.createMock(CommentApi.class, provider);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		SocializeSessionConsumer mockSessionConsumer = AndroidMock.createMock(SocializeSessionConsumer.class);
		
		final String udid = "foobar";
		final String consumerKey = "foobar_consumerKey";
		final String consumerSecret = "foobar_consumerSecret";
		
		AndroidMock.expect(deviceUtils.getUDID(getContext())).andReturn(udid);
		
		commentApi.authenticateAsync(consumerKey, consumerSecret, udid, null, null, AuthProviderType.SOCIALIZE, null, listener, mockSessionConsumer, false);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
		service.setDeviceUtils(deviceUtils);
		service.setCommentApi(commentApi);
		
		service.authenticate(consumerKey, consumerSecret, listener, mockSessionConsumer);
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(commentApi);
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
		
		commentApi.addComment(session, key, comment, null, listener);
		
		AndroidMock.replay(commentApi);
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
		service.setCommentApi(commentApi);
		
		service.addComment(session, key, comment, null, listener);
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
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
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		
		service.setLikeApi(api);
		
		service.listLikesById(session, listener, ids);
		
		AndroidMock.verify(api);
	}
	
	@UsesMocks ({HttpClientFactory.class})
	public void testDestroy() {
		HttpClientFactory factory = AndroidMock.createMock(HttpClientFactory.class);
		factory.destroy();
		
		AndroidMock.replay(factory);
		
		SocializeApiHost service = new SocializeApiHost(getContext());
		service.setClientFactory(factory);
		
		service.destroy();
		
		AndroidMock.verify(factory);
	}
}
