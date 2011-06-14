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
package com.socialize.test.unit.api;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.CommentApi;
import com.socialize.entity.Comment;
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
		final String key = "foo";
		final String comment = "bar";
		
		CommentApi api = new CommentApi(provider){
			@Override
			public void putAsync(SocializeSession session, String endpoint, Comment object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		api.addComment(session, key, comment, listener);
		
		Comment result = (Comment) getResult();
		assertNotNull(result);
		assertEquals(comment, result.getText());
		assertNotNull(result.getEntityKey());
		assertEquals(key, result.getEntityKey());
	}
	
	public void testGetCommentsByEntity() {
		
		final String key = "foo";
		
		CommentApi api = new CommentApi(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
				addResult(key);
			}
		};
		
		api.getCommentsByEntity(session, key, listener);
		
		String after = getResult();
		
		assertNotNull(after);
		assertEquals(key, after);
	}
	
	public void testGetCommentsById() {
		
		int[] ids = {1,2,3};
		
		CommentApi api = new CommentApi(provider) {

			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
				addResult(ids);
			}
		};
		
		api.getCommentsById(session, listener, ids);
		
		String[] after = getResult();
		
		assertNotNull(after);
		
		for (int i = 0; i < after.length; i++) {
			assertEquals(String.valueOf(ids[i]), after[i]);
		}
		
	}
	
	public void testGetComment() {
		
		int id = 69;
		
		CommentApi api = new CommentApi(provider) {

			@Override
			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.getComment(session, id, listener);
		
		String strId = getResult();
		
		assertNotNull(strId);
		assertEquals(String.valueOf(id), strId);
	}
}
