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
package com.socialize.test.integration.services.a;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import com.socialize.CommentUtils;
import com.socialize.SocializeAccess;
import com.socialize.SubscriptionUtils;
import com.socialize.UserUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.ShareableActionOptions;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.api.action.user.SocializeUserUtils;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionCheckListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.notifications.SubscriptionType;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class CommentUtilsTest extends SocializeActivityTest {

	public void test_getComments() throws Exception {

		// Create two comments.
		final Entity entityKey = Entity.newInstance("test_getComments" + Math.random(), "test_getComments");
		final CountDownLatch latch = new CountDownLatch(1);

		// Set auto auth off
		final CommentOptions options = CommentUtils.getUserCommentOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		CommentUtils.addComment(TestUtils.getActivity(this), entityKey, "foobar0", options, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(TestUtils.getActivity(CommentUtilsTest.this), entityKey, "foobar1", options, new CommentAddListener() {

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

		CommentUtils.getComments(TestUtils.getActivity(this), new CommentListListener() {

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
		final Entity entityKey = Entity.newInstance("test_getCommentsByEntity" + Math.random(), "test_getCommentsByEntity");
		final CountDownLatch latch = new CountDownLatch(1);

		// Set auto auth off
		final CommentOptions options = CommentUtils.getUserCommentOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		CommentUtils.addComment(TestUtils.getActivity(this), entityKey, "foobar0", options, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(TestUtils.getActivity(CommentUtilsTest.this), entityKey, "foobar1", options, new CommentAddListener() {

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

		CommentUtils.getCommentsByEntity(TestUtils.getActivity(this), entityKey.getKey(), 0, 2, new CommentListListener() {

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
		final Entity entityKey = Entity.newInstance("test_getCommentsByUser" + Math.random(), "test_getCommentsByUser");
		final CountDownLatch latch = new CountDownLatch(1);

		// Set auto auth off
		final CommentOptions options = CommentUtils.getUserCommentOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		CommentUtils.addComment(TestUtils.getActivity(this), entityKey, "foobar0", options, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(TestUtils.getActivity(CommentUtilsTest.this), entityKey, "foobar1", options, new CommentAddListener() {

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

		CommentUtils.getCommentsByUser(TestUtils.getActivity(this), UserUtils.getCurrentUser(TestUtils.getActivity(this)), 0, 2, new CommentListListener() {

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
	
	public void test_getCommentsByApplication() throws Exception {
		// Create two comments.
		final Entity entityKey = Entity.newInstance("test_getCommentsByApplication" + Math.random(), "test_getCommentsByApplication");
		final CountDownLatch latch = new CountDownLatch(1);

		// Set auto auth off
		final CommentOptions options = CommentUtils.getUserCommentOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		CommentUtils.addComment(TestUtils.getActivity(this), entityKey, "foobar0", options, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}

			@Override
			public void onCreate(Comment entity) {
				addResult(0, entity);
				CommentUtils.addComment(TestUtils.getActivity(CommentUtilsTest.this), entityKey, "foobar1", options, new CommentAddListener() {

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

		CommentUtils.getCommentsByApplication(TestUtils.getActivity(this), 0, 2, new CommentListListener() {

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
		assertEquals(entities.size(), 2);
		assertTrue(entities.getTotalCount() >= 2);

		List<Comment> items = entities.getItems();
		assertNotNull(items);

		assertTrue(items.contains(result0));
		assertTrue(items.contains(result1));
	}	
	

	public void test_subscribe_unsubscribe() throws Exception {
		final Entity e = Entity.newInstance("test_unsubscribe" + Math.random(),"test_unsubscribe");
		final CountDownLatch latch = new CountDownLatch(1);
		SubscriptionUtils.subscribe(TestUtils.getActivity(this), e, SubscriptionType.NEW_COMMENTS, new SubscriptionResultListener() {
			
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
		SubscriptionUtils.isSubscribed(TestUtils.getActivity(this), e, SubscriptionType.NEW_COMMENTS, new SubscriptionCheckListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
			
			@Override
			public void onSubscribed(Subscription subscription) {
				addResult(0, subscription);
				latch2.countDown();
			}

			@Override
			public void onNotSubscribed() {}

		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		subscription = getResult(0);
		assertNotNull(subscription);
		assertNotNull(subscription.getEntity());
		assertEquals(e.getKey(), subscription.getEntity().getKey());
		assertTrue(subscription.isSubscribed());			
		
		final CountDownLatch latch3 = new CountDownLatch(1);
		SubscriptionUtils.unsubscribe(TestUtils.getActivity(this), e, SubscriptionType.NEW_COMMENTS, new SubscriptionResultListener() {
			
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
	
	public void testCommentIsSharedWithAutoPost() {
		final Entity entity = Entity.newInstance("testCommentIsSharedWithAutoPost", "testCommentIsSharedWithAutoPost");
		
		final SocialNetwork[] autoPost = {
			SocialNetwork.TWITTER,
			SocialNetwork.FACEBOOK,
		};
		
		SocializeCommentUtils commentUtils = new SocializeCommentUtils() {
			@Override
			protected void doCommentWithoutShareDialog(Activity context, SocializeSession session, Entity entity, String text, CommentOptions likeOptions, CommentAddListener listener, SocialNetwork... networks) {
				TestUtils.addResult(0, networks);
			}

			@Override
			protected boolean isDisplayAuthDialog(Context context, SocializeSession session, ActionOptions options, SocialNetwork... networks) {
				return false;
			}

			@Override
			protected boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
				return false;
			}
			
		};
		
		SocializeUserUtils userUtils = new SocializeUserUtils() {
			@Override
			public SocialNetwork[] getAutoPostSocialNetworks(Context context) {
				return autoPost;
			}
		};
		
		SocializeAccess.setUserUtilsProxy(userUtils);
		
		commentUtils.addComment(TestUtils.getActivity(this), entity, "foobar", null);
		
		SocialNetwork[] networks = TestUtils.getResult(0);
		
		assertNotNull(networks);
		assertEquals(2, networks.length);
	}	
}
