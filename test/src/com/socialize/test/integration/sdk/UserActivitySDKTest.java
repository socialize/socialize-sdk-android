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
package com.socialize.test.integration.sdk;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;



/**
 * @author Jason Polites
 *
 */
@Deprecated
public class UserActivitySDKTest extends SDKIntegrationTest {

	public void testListUserActivity() throws Throwable {
		final Entity e = Entity.newInstance(DEFAULT_GET_ENTITY, DEFAULT_GET_ENTITY);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		final SocializeSession session = Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final long userId = session.getUser().getId();
				
				// first make a comment
				Socialize.getSocialize().addComment(context, e, "Test Comment", new CommentAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCreate(Comment comment) {
						addResult(0, comment);
						
						// now get activity
						Socialize.getSocialize().listActivityByUser(userId, new ActionListListener() {
							
							@Override
							public void onList(ListResult<SocializeAction> entities) {
								addResult(1, entities);
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
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Comment comment = getResult(0);
		ListResult<SocializeAction> entities = getResult(1);
		
		assertNotNull(comment);
		assertNotNull(entities);
		
		List<SocializeAction> items = entities.getItems();
		
		assertNotNull(items);
		
		boolean found = false;
		
		for (SocializeAction socializeAction : items) {
			if(socializeAction.equals(comment)) {
				found = true;
				break;
			}
		}
		
		assertTrue("Did not find expected comment in activity for user", found);
	}

	public void testDeleteLike() throws Throwable {
		
		final Entity e = Entity.newInstance(DEFAULT_GET_ENTITY, DEFAULT_GET_ENTITY);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().like(getContext(), e, new LikeAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCreate(Like like) {
						
						// Now delete
						Socialize.getSocialize().unlike(like.getId(), new LikeDeleteListener() {
							
							@Override
							public void onError(SocializeException error) {
								error.printStackTrace();
								latch.countDown();
							}
							
							@Override
							public void onDelete() {
								// Now get
								Socialize.getSocialize().getLike(e.getKey(), new LikeGetListener() {
									
									@Override
									public void onGet(Like entity) {
										latch.countDown();
									}
									
									@Override
									public void onError(SocializeException error) {
										addResult(0, error);
										latch.countDown();
									}
								});
							}
						});
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		SocializeException error = getResult(0);
		assertNotNull(error);
		assertTrue((error instanceof SocializeApiError));
		assertEquals(404, ((SocializeApiError)error).getResultCode());
	}

	public void testListLikesForUserSameEntity() throws Throwable {
		final Entity e = Entity.newInstance(DEFAULT_GET_ENTITY, DEFAULT_GET_ENTITY);
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		final SocializeSession session = Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final long userId = session.getUser().getId();
				
				// Make sure we have a like
				Socialize.getSocialize().like(context, e, new LikeAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCreate(Like entity) {
						// now list
						Socialize.getSocialize().listLikesByUser(userId, new LikeListListener() {
							
							@Override
							public void onError(SocializeException error) {
								error.printStackTrace();
								latch.countDown();
							}
							
							@Override
							public void onList(List<Like> items, int totalSize) {
								
								// This is the size before
								addResult(0, totalSize);
								
								// Now add a like for an entity we have already liked
								Socialize.getSocialize().like(context, e, new LikeAddListener() {
									
									@Override
									public void onError(SocializeException error) {
										error.printStackTrace();
										latch.countDown();
										
									}
									
									@Override
									public void onCreate(Like entity) {
										// Now list again
										Socialize.getSocialize().listLikesByUser(userId, new LikeListListener() {
											
											@Override
											public void onError(SocializeException error) {
												error.printStackTrace();
												latch.countDown();
											}
											
											@Override
											public void onList(List<Like> items, int totalSize) {
												// This is the size after
												addResult(1, totalSize);
												latch.countDown();
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Integer before = getResult(0);
		Integer after = getResult(1);
		assertNotNull(before);
		assertNotNull(after);
		assertEquals(after.intValue(), before.intValue());
	}
	
	public void testListLikesForUserDiffEntity() throws Throwable {
		final Entity e = Entity.newInstance(DEFAULT_GET_ENTITY, DEFAULT_GET_ENTITY);
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		final SocializeSession session = Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				final long userId = session.getUser().getId();
				
				// Make sure we have a like
				Socialize.getSocialize().like(context, e, new LikeAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCreate(Like entity) {
						// now list
						Socialize.getSocialize().listLikesByUser(userId, new LikeListListener() {
							
							@Override
							public void onError(SocializeException error) {
								error.printStackTrace();
								latch.countDown();
							}
							
							@Override
							public void onList(List<Like> items, int totalSize) {
								
								// This is the size before
								addResult(0, totalSize);
								
								// Now add a like for an entity we have NOT already liked
								Entity other = Entity.newInstance("Random" + Math.random(), "Random");
								Socialize.getSocialize().like(context, other, new LikeAddListener() {
									
									@Override
									public void onError(SocializeException error) {
										error.printStackTrace();
										latch.countDown();
									}
									
									@Override
									public void onCreate(Like entity) {
										// Now list again
										Socialize.getSocialize().listLikesByUser(userId, new LikeListListener() {
											
											@Override
											public void onError(SocializeException error) {
												error.printStackTrace();
												latch.countDown();
											}
											
											@Override
											public void onList(List<Like> items, int totalSize) {
												// This is the size after
												addResult(1, totalSize);
												latch.countDown();
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Integer before = getResult(0);
		Integer after = getResult(1);
		assertNotNull(before);
		assertNotNull(after);
		assertEquals(after.intValue(), before.intValue()+1);
	}
}
