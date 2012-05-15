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
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.test.SocializeActivityTest;


/**
 * @author Jason Polites
 *
 */
public class CommentUtilsTest extends SocializeActivityTest {



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

	// Tests get comment too.
	public void test_addComment() throws Exception {

		final String entityKey = "test_addComment";
		final CountDownLatch latch = new CountDownLatch(1);

		CommentUtils.addComment(getActivity(), entityKey, "foobar", new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();

			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				// Now get
				CommentUtils.getComment(getActivity(), new CommentGetListener() {

					@Override
					public void onGet(Comment entity) {
						addResult(1, entity);
						latch.countDown();
					}

					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				}, entity.getId());
			}
		});


		latch.await(20, TimeUnit.SECONDS);

		Comment result0 = getResult(0);
		Comment result1 = getResult(1);

		assertNotNull(result0);
		assertNotNull(result1);
		assertEquals(result0, result1);
		assertEquals("foobar", result0.getText());
	}

	public void test_getComments() throws Exception {

		// Create two comments.
		final String entityKey = "test_getComments" + Math.random();
		final CountDownLatch latch = new CountDownLatch(1);

		CommentUtils.addComment(getActivity(), entityKey, "foobar0", new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(getActivity(), entityKey, "foobar1", new CommentAddListener() {

					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}

					@Override
					public void onCreate(Comment entity) {
						addResult(1, entity);
						latch.countDown();
					}
				});	
			}
		});	

		latch.await(20, TimeUnit.SECONDS);

		Comment result0 = getResult(0);
		Comment result1 = getResult(1);

		assertNotNull(result0);
		assertNotNull(result1);

		final CountDownLatch latch2 = new CountDownLatch(1);


		CommentUtils.getComments(getActivity(), new CommentListListener() {

			@Override
			public void onList(ListResult<Comment> entities) {
				addResult(3, entities);
				latch2.countDown();
			}

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();

			}
		}, result0.getId(), result1.getId());


		latch2.await(20, TimeUnit.SECONDS);

		ListResult<Comment> entities = getResult(3);

		assertNotNull(entities);
		assertEquals(entities.getTotalCount(), 2);

		List<Comment> items = entities.getItems();
		assertNotNull(items);
		assertEquals(items.size(), 2);

		assertTrue(items.contains(result0));
		assertTrue(items.contains(result1));

	}


	public void test_getCommentsByEntity() throws Exception {
		// Create two comments.
		final String entityKey = "test_getCommentsByEntity" + Math.random();
		final CountDownLatch latch = new CountDownLatch(1);

		CommentUtils.addComment(getActivity(), entityKey, "foobar0", new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(getActivity(), entityKey, "foobar1", new CommentAddListener() {

					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}

					@Override
					public void onCreate(Comment entity) {
						addResult(1, entity);
						latch.countDown();
					}
				});	
			}
		});	

		latch.await(20, TimeUnit.SECONDS);

		Comment result0 = getResult(0);
		Comment result1 = getResult(1);

		assertNotNull(result0);
		assertNotNull(result1);

		final CountDownLatch latch2 = new CountDownLatch(1);

		CommentUtils.getCommentsByEntity(getActivity(), entityKey, 0, 2, new CommentListListener() {

			@Override
			public void onList(ListResult<Comment> entities) {
				addResult(3, entities);
				latch2.countDown();
			}

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();

			}
		});


		latch2.await(20, TimeUnit.SECONDS);

		ListResult<Comment> entities = getResult(3);

		assertNotNull(entities);
		assertEquals(entities.getTotalCount(), 2);

		List<Comment> items = entities.getItems();
		assertNotNull(items);
		assertEquals(items.size(), 2);

		assertTrue(items.contains(result0));
		assertTrue(items.contains(result1));
	}

	public void test_getCommentsByUser() throws Exception {
		// Create two comments.
		final String entityKey = "test_getCommentsByUser" + Math.random();
		final CountDownLatch latch = new CountDownLatch(1);

		CommentUtils.addComment(getActivity(), entityKey, "foobar0", new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(getActivity(), entityKey, "foobar1", new CommentAddListener() {

					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}

					@Override
					public void onCreate(Comment entity) {
						addResult(1, entity);
						latch.countDown();
					}
				});	
			}
		});	

		latch.await(20, TimeUnit.SECONDS);

		Comment result0 = getResult(0);
		Comment result1 = getResult(1);

		assertNotNull(result0);
		assertNotNull(result1);

		final CountDownLatch latch2 = new CountDownLatch(1);

		CommentUtils.getCommentsByUser(getActivity(), UserUtils.getCurrentUser(getActivity()), 0, 2, new CommentListListener() {

			@Override
			public void onList(ListResult<Comment> entities) {
				addResult(3, entities);
				latch2.countDown();
			}

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();

			}
		});


		latch2.await(20, TimeUnit.SECONDS);

		ListResult<Comment> entities = getResult(3);

		assertNotNull(entities);
		assertTrue(entities.getTotalCount() >= 2);

		List<Comment> items = entities.getItems();
		assertNotNull(items);

		assertTrue(items.contains(result0));
		assertTrue(items.contains(result1));
	}

	public void test_subscribe_unsubscribe() throws Exception {
		final Entity e = Entity.newInstance("test_unsubscribe" + Math.random(),"test_unsubscribe");
		final CountDownLatch latch = new CountDownLatch(1);
		CommentUtils.subscribe(getActivity(), e, new SubscriptionResultListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(Subscription entity) {
				addResult(0, entity);
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Subscription subscription = getResult(0);
		assertNotNull(subscription);
		assertNotNull(subscription.getEntity());
		assertEquals(e.getKey(), subscription.getEntity().getKey());
		assertTrue(subscription.isSubscribed());
		
		final CountDownLatch latch2 = new CountDownLatch(1);
		CommentUtils.isSubscribed(getActivity(), e, new SubscriptionGetListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
			
			@Override
			public void onGet(Subscription entity) {
				addResult(0, entity);
				latch2.countDown();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		subscription = getResult(0);
		assertNotNull(subscription);
		assertNotNull(subscription.getEntity());
		assertEquals(e.getKey(), subscription.getEntity().getKey());
		assertTrue(subscription.isSubscribed());			
		
		final CountDownLatch latch3 = new CountDownLatch(1);
		CommentUtils.unsubscribe(getActivity(), e, new SubscriptionResultListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch3.countDown();
			}
			
			@Override
			public void onCreate(Subscription entity) {
				addResult(0, entity);
				latch3.countDown();
			}
		});
		
		latch3.await(20, TimeUnit.SECONDS);
		
		subscription = getResult(0);
		assertNotNull(subscription);
		assertNotNull(subscription.getEntity());
		assertEquals(e.getKey(), subscription.getEntity().getKey());
		assertFalse(subscription.isSubscribed());	
	}
}
