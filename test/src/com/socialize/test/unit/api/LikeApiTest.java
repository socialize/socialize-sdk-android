/*
 * Copyright (c) 2012 Socialize Inc. 
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
package com.socialize.test.unit.api;

import java.util.List;
import java.util.Map;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.like.SocializeLikeSystem;
import com.socialize.entity.ActionError;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, LikeListener.class, SocializeProvider.class})
public class LikeApiTest extends SocializeUnitTest {

	SocializeProvider<Like> provider;
	SocializeSession session;
	LikeListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(LikeListener.class);
	}

	/**
	 * More specific test to ensure the like is actually set.
	 */
	public void testAddLike() {
		final Entity key = Entity.newInstance("foo", "foo");
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider){

			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Like> objects, SocializeActionListener listener) {
				addResult(objects);
			}

		};
		
		api.addLike(session, key, null, listener);
		
		List<Like> likes = getNextResult();
		
		assertNotNull(likes);
		assertEquals(1, likes.size());
		
		Like result = likes.get(0);
		
		assertNotNull(result);
		assertNotNull(result.getEntityKey());
		assertEquals(key.getKey(), result.getEntityKey());
	}
	
	public void testGetLikesByEntity() {
		
		final String key = "foo";
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, SocializeActionListener listener, String...ids) {
				addResult(key);
			}
		};
		
		api.getLikesByEntity(session, key, listener);
		
		String after = getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after);
	}
	
	public void testGetLikesByEntityPaginated() {
		
		final String key = "foo";
		int startIndex = 0, endIndex = 10;
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.getLikesByEntity(session, key, startIndex, endIndex, listener);
		
		String after = getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after);
		
		int afterStartIndex = (Integer) getNextResult();
		int afterEndIndex = (Integer) getNextResult();
		
		assertEquals(startIndex, afterStartIndex);
		assertEquals(endIndex, afterEndIndex);
	}
	
	public void testGetLikesByUser() {
		
		final int key = 69;
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(key);
			}
		};
		
		api.getLikesByUser(session, key, listener);
		
		Integer after = (Integer) getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after.intValue());
	}
	
	public void testGetLikesByUserPaginated() {
		
		final int key = 69;
		int startIndex = 0, endIndex = 10;
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.getLikesByUser(session, key, startIndex, endIndex, listener);
		
		Integer after = (Integer) getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after.intValue());
		
		int afterStartIndex = (Integer) getNextResult();
		int afterEndIndex = (Integer) getNextResult();
		
		assertEquals(startIndex, afterStartIndex);
		assertEquals(endIndex, afterEndIndex);
	}	
	
	public void testGetLikesById() {
		
		long[] ids = {1,2,3};
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, int startIndex, int endIndex, SocializeActionListener listener, String...ids) {
				addResult(ids);
			}
		};
		
		api.getLikesById(session, listener, ids);
		
		String[] after = getNextResult();
		
		assertNotNull(after);
		
		for (int i = 0; i < after.length; i++) {
			assertEquals(String.valueOf(ids[i]), after[i]);
		}
		
	}
	
	public void testGetLike() {
		
		int id = 69;
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {

			@Override
			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.getLike(session, id, listener);
		
		String strId = getNextResult();
		
		assertNotNull(strId);
		assertEquals(String.valueOf(id), strId);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks({ListResult.class, LikeListener.class, Like.class, List.class, User.class, SocializeSession.class})
	public void testGetLikeByKeyWithResults() {

		String key = "foobar";
		Long userId = 69L;
		
		final Like like = AndroidMock.createMock(Like.class);
		final ListResult<Like> listResult = (ListResult<Like>) AndroidMock.createMock(ListResult.class);
		List<Like> items = (List<Like>) AndroidMock.createMock(List.class);
		
		User user = AndroidMock.createMock(User.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				LikeListener ll = (LikeListener) listener;
				ll.onList(listResult);
			}
		};
		
		LikeListener likeListener = new LikeListener() {
			
			@Override
			public void onError(SocializeException error) {}
			
			@Override
			public void onUpdate(Like entity) {}
			
			@Override
			public void onList(ListResult<Like> entities) {}
			
			@Override
			public void onGet(Like entity) {
				assertNotNull(entity);
				assertSame(like, entity);
			}
			
			@Override
			public void onDelete() {}
			
			@Override
			public void onCreate(Like entity) {}
		};

		AndroidMock.expect(listResult.getTotalCount()).andReturn(1);
		AndroidMock.expect(listResult.getItems()).andReturn(items);
		AndroidMock.expect(items.size()).andReturn(1);
		AndroidMock.expect(items.get(0)).andReturn(like);
		
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(user.getId()).andReturn(userId);
		
		AndroidMock.replay(listResult);
		AndroidMock.replay(items);
		AndroidMock.replay(session);
		AndroidMock.replay(user);
		
		api.getLike(session, key, likeListener);
		
		AndroidMock.verify(listResult);
		AndroidMock.verify(items);
		AndroidMock.verify(session);
		AndroidMock.verify(user);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks({ListResult.class, LikeListener.class, ActionError.class, Like.class, List.class, SocializeSession.class, User.class})
	public void testGetLikeByKeyWithNoResults() {

		String key = "foobar";
		
		Long userId = 69L;
		
		User user = AndroidMock.createMock(User.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
		final ListResult<Like> listResult = (ListResult<Like>) AndroidMock.createMock(ListResult.class);
		List<Like> items = (List<Like>) AndroidMock.createMock(List.class);
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				LikeListener ll = (LikeListener) listener;
				ll.onList(listResult);
			}
		};
		
		LikeListener likeListener = new LikeListener() {
			
			@Override
			public void onError(SocializeException error) {
				assertNotNull(error);
				assertTrue(error instanceof SocializeApiError);
				assertEquals(404, ((SocializeApiError)error).getResultCode());
			}
			
			@Override
			public void onUpdate(Like entity) {}
			
			@Override
			public void onList(ListResult<Like> entities) {}
			
			@Override
			public void onGet(Like entity) {}
			
			@Override
			public void onDelete() {}
			
			@Override
			public void onCreate(Like entity) {}
		};

		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(user.getId()).andReturn(userId);
		
		AndroidMock.expect(listResult.getTotalCount()).andReturn(0);
		AndroidMock.expect(listResult.getItems()).andReturn(items);
		AndroidMock.expect(items.size()).andReturn(0);
		
		AndroidMock.replay(user);
		AndroidMock.replay(session);
		AndroidMock.replay(listResult);
		AndroidMock.replay(items);
		
		api.getLike(session, key, likeListener);
		
		AndroidMock.verify(user);
		AndroidMock.verify(session);
		AndroidMock.verify(listResult);
		AndroidMock.verify(items);
	}
	
	public void testGetLikeByKeyWithUsesCorrectPagination() {

		String key = "foobar";
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void getLikesByEntity(SocializeSession session, String key, int startIndex, int endIndex, LikeListener listener) {
				assertEquals(0, startIndex);
				assertEquals(1, endIndex);
			}
		};
		
		api.getLike(session, key, listener);
	}
	
	
	public void testDeleteLike() {
		
		int id = 69;
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void deleteAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.deleteLike(session, id, listener);
		
		String strId = getNextResult();
		
		assertNotNull(strId);
		assertEquals(String.valueOf(id), strId);
	}
	
	@UsesMocks ({SocializeSession.class, User.class})
	public void testGetLikeByKey() {
		
		String key = "foobar";
		Long userId = 69L;
		
		User user = AndroidMock.createMock(User.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(user.getId()).andReturn(userId);
		
		AndroidMock.replay(user);
		AndroidMock.replay(session);
		
		SocializeLikeSystem api = new SocializeLikeSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				addResult(key);
				addResult(endpoint);
			}
		};
		
		api.getLike(session, key, listener);
		
		AndroidMock.verify(user);
		AndroidMock.verify(session);
		
		String strId = getNextResult();
		String endpoint = getNextResult();
		
		assertNotNull(strId);
		assertEquals(key, strId);
		
		assertNotNull(endpoint);
		assertEquals("/user/69/like/", endpoint);
	}
}
