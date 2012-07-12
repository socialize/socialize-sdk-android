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
import com.socialize.api.action.comment.SocializeCommentSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.comment.CommentListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, CommentListener.class, SocializeProvider.class})
public class CommentApiTest extends SocializeUnitTest {

	SocializeProvider<Comment> provider;
	SocializeSession session;
	CommentListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(CommentListener.class);
	}

	/**
	 * More specific test to ensure the comment is actually set.
	 */
	public void testAddComment() {
		final Entity key = Entity.newInstance("foo", "foo");
		final String comment = "bar";
		
		SocializeCommentSystem api = new SocializeCommentSystem(provider){
			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Comment> object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		api.addComment(session, key, comment, null, listener);
		
		List<Comment> list = getNextResult();
		assertNotNull(list);
		assertEquals(1, list.size());
		
		Comment result = list.get(0);
		assertNotNull(result);
		assertEquals(comment, result.getText());
		assertNotNull(result.getEntityKey());
		assertEquals(key.getKey(), result.getEntityKey());
	}
	
	public void testGetCommentsByEntity() {
		
		final String key = "foo";
		
		int startIndex = 0, endIndex = 10;
		
		SocializeCommentSystem api = new SocializeCommentSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.getCommentsByEntity(session, key, startIndex, endIndex, listener);
		
		String after = getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after);
		
		Integer afterStartIndex = getNextResult();
		Integer afterEndIndex = getNextResult();
		
		assertEquals(startIndex, afterStartIndex.intValue());
		assertEquals(endIndex, afterEndIndex.intValue());
	}
	
	public void testGetCommentsByUserPaginated() {
		
		final long key = 69L;
		
		int startIndex = 0, endIndex = 10;
		
		SocializeCommentSystem api = new SocializeCommentSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(endpoint);
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.getCommentsByUser(session, key, startIndex, endIndex, listener);
		
		String endPointAfter = getNextResult();
		Long keyAfter = getNextResult();
		Integer afterStartIndex = getNextResult();
		Integer afterEndIndex = getNextResult();
		
		assertNotNull(keyAfter);
		assertEquals(key, keyAfter.longValue());
		assertEquals("/user/" + key + "/comment/", endPointAfter);
		assertEquals(startIndex, afterStartIndex.intValue());
		assertEquals(endIndex, afterEndIndex.intValue());
	}	
	
	public void testGetCommentsByUser() {
		
		final long key = 69L;
		
		int startIndex = 0, endIndex = SocializeConfig.MAX_LIST_RESULTS;
		
		SocializeCommentSystem api = new SocializeCommentSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(endpoint);
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.getCommentsByUser(session, key, listener);
		
		String endPointAfter = getNextResult();
		Long keyAfter = getNextResult();
		Integer afterStartIndex = getNextResult();
		Integer afterEndIndex = getNextResult();
		
		assertNotNull(keyAfter);
		assertEquals(key, keyAfter.longValue());
		assertEquals("/user/" + key + "/comment/", endPointAfter);
		assertEquals(startIndex, afterStartIndex.intValue());
		assertEquals(endIndex, afterEndIndex.intValue());
	}	
		
	
	public void testGetCommentsById() {
		
		long[] ids = {1,2,3};
		
		SocializeCommentSystem api = new SocializeCommentSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, int startIndex, int endIndex, SocializeActionListener listener, String...ids) {
				addResult(ids);
			}
		};
		
		api.getCommentsById(session, listener, ids);
		
		String[] after = getNextResult();
		
		assertNotNull(after);
		
		for (int i = 0; i < after.length; i++) {
			assertEquals(String.valueOf(ids[i]), after[i]);
		}
		
	}
	
	public void testGetComment() {
		
		int id = 69;
		
		SocializeCommentSystem api = new SocializeCommentSystem(provider) {

			@Override
			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.getComment(session, id, listener);
		
		String strId = getNextResult();
		
		assertNotNull(strId);
		assertEquals(String.valueOf(id), strId);
	}
}
