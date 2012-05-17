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
package com.socialize.test.integration.services;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.socialize.LikeUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.IsLikedListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.test.SocializeActivityTest;


/**
 * @author Jason Polites
 *
 */
public class LikeUtilsTest extends SocializeActivityTest {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Socialize.getSocialize().destroy(true);
	}

	@Override
	protected void tearDown() throws Exception {
		Socialize.getSocialize().destroy(true);
		super.tearDown();
	}

	public void testAddLike() throws InterruptedException {
		final Entity entityKey = Entity.newInstance("testAddLike", "testAddLike");
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		LikeUtils.like(getActivity(), entityKey, null, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				addResult(0, error);
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				addResult(0, like);
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Object result = getResult(0);
		
		assertNotNull(result);
		assertTrue("Result is not a like object", (result instanceof Like));
		
		Like like = (Like) result;
		assertTrue("Like ID is not greater than 0", like.getId() > 0);
		
		// Now use the normal UI to retrieve the like
		clearResults();
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		Socialize.getSocialize().getLikeById(like.getId(), new LikeGetListener() {
			
			@Override
			public void onGet(Like entity) {
				addResult(0, entity);
				latch2.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		Like after = getResult(0);
		
		assertNotNull(after);
		assertEquals(like.getId(), after.getId());
		assertEquals(entityKey.getKey(), after.getEntityKey());
	}
	
	public void testGetLikeExists() throws InterruptedException {
		
		final Entity entityKey = Entity.newInstance("testGetLikeExists", "testGetLikeExists");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
				
		
		LikeUtils.like(getActivity(), entityKey, null, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				LikeUtils.getLike(getActivity(), entityKey.getKey(), new LikeGetListener() {
					
					@Override
					public void onGet(Like entity) {
						addResult(0, entity);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		Like like = getResult(0);
		assertNotNull(like);
		assertEquals(entityKey.getKey(), like.getEntityKey());
	}
	
	public void testGetLikeDoesNotExist() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final String entityKey = "testGetLikeDoesNotExist";
		
		LikeUtils.isLiked(getActivity(), entityKey, new IsLikedListener() {
			
			@Override
			public void onLiked(Like like) {
				fail();
			}

			@Override
			public void onNotLiked() {
				addResult("success");
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		String success = getResult(0);
		assertNotNull(success);
		assertEquals("success", success);
	}
	
	public void testUnlike() throws InterruptedException {
		final Entity entityKey = Entity.newInstance("testUnlike", "testUnlike");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		LikeUtils.like(getActivity(), entityKey, null, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.unlike(getActivity(), entityKey.getKey(), new LikeDeleteListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
					
					@Override
					public void onDelete() {
						addResult("success");
						latch.countDown();
					}
				});
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		String success = getResult(0);
		assertNotNull(success);
		assertEquals("success", success);
	}
	
	public void testGetLikesByUser() throws SocializeException, InterruptedException {
		final User user = UserUtils.getCurrentUser(getActivity());
		final Entity entityKey = Entity.newInstance("testGetLikesByUser", "testGetLikesByUser");
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		LikeUtils.like(getActivity(), entityKey, null, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.getLikesByUser(getActivity(), user, 0, 100, new LikeListListener() {
					
					@Override
					public void onList(List<Like> items, int totalSize) {
						addResult(items);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
				});
				
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		List<Like> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}
	
	public void testGetLikesByEntity() throws SocializeException, InterruptedException {
		final Entity entityKey = Entity.newInstance("testGetLikesByEntity", "testGetLikesByEntity");
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		LikeUtils.like(getActivity(), entityKey, null, new LikeAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Like like) {
				
				LikeUtils.getLikesByEntity(getActivity(), entityKey.getKey(), 0, 100, new LikeListListener() {
					
					@Override
					public void onList(List<Like> items, int totalSize) {
						addResult(items);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
				});
				
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		List<Like> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}	
}
