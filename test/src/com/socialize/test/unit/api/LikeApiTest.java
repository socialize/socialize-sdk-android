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

import java.util.List;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.LikeApi;
import com.socialize.entity.Like;
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
		final String key = "foo";
		
		LikeApi api = new LikeApi(provider){

			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Like> objects, SocializeActionListener listener) {
				addResult(objects);
			}

		};
		
		api.addLike(session, key, null, listener);
		
		
		List<Like> likes = getResult();
		
		assertNotNull(likes);
		assertEquals(1, likes.size());
		
		Like result = likes.get(0);
		
		assertNotNull(result);
		assertNotNull(result.getEntityKey());
		assertEquals(key, result.getEntityKey());
	}
	
	public void testGetLikesByEntity() {
		
		final String key = "foo";
		
		LikeApi api = new LikeApi(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
				addResult(key);
			}
		};
		
		api.getLikesByEntity(session, key, listener);
		
		String after = getResult();
		
		assertNotNull(after);
		assertEquals(key, after);
	}
	
	public void testGetLikesById() {
		
		int[] ids = {1,2,3};
		
		LikeApi api = new LikeApi(provider) {

			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
				addResult(ids);
			}
		};
		
		api.getLikesById(session, listener, ids);
		
		String[] after = getResult();
		
		assertNotNull(after);
		
		for (int i = 0; i < after.length; i++) {
			assertEquals(String.valueOf(ids[i]), after[i]);
		}
		
	}
	
	public void testGetLike() {
		
		int id = 69;
		
		LikeApi api = new LikeApi(provider) {

			@Override
			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.getLike(session, id, listener);
		
		String strId = getResult();
		
		assertNotNull(strId);
		assertEquals(String.valueOf(id), strId);
	}
	
	public void testDeleteLike() {
		
		int id = 69;
		
		LikeApi api = new LikeApi(provider) {
			@Override
			public void deleteAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.deleteLike(session, id, listener);
		
		String strId = getResult();
		
		assertNotNull(strId);
		assertEquals(String.valueOf(id), strId);
	}
	
	public void testGetLikeByKey() {
		
		String key = "foobar";
		
		LikeApi api = new LikeApi(provider) {

			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
				addResult(key);
			}

		};
		
		api.getLike(session, key, listener);
		
		String strId = getResult();
		
		assertNotNull(strId);
		assertEquals(key, strId);
	}
}
