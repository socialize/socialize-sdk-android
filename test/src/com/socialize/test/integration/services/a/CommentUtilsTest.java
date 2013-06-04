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

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import com.socialize.CommentUtils;
import com.socialize.SocializeAccess;
import com.socialize.SubscriptionUtils;
import com.socialize.UserUtils;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.ShareableActionOptions;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.api.action.comment.SocializeSubscriptionUtils;
import com.socialize.api.action.comment.SubscriptionUtilsProxy;
import com.socialize.api.action.user.SocializeUserUtils;
import com.socialize.entity.*;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionCheckListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.notifications.NotificationRegistrationSystem;
import com.socialize.notifications.SocializeNotificationRegistrationSystem;
import com.socialize.notifications.SubscriptionType;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.ui.comment.*;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.slider.OnActionBarSliderMoveListener;
import com.socialize.util.AppUtils;
import com.socialize.util.DefaultAppUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 *
 */
public class CommentUtilsTest extends SocializeActivityTest {

	public void testCommentViewDisplaysCorrectData() throws InterruptedException {

		// The data we assert is taken from the default data created sdk-cleanup.py
		Activity context = TestUtils.getActivity(this);

		// Setup the comment Activity for monitoring
		TestUtils.setUpActivityMonitor(CommentActivity.class);

		final int grabLength = 3;

		SocializeAccess.setBeanOverrides("socialize_proxy_beans.xml");

		// Stub out app utils to ensure notifications are marked as enabled
		final DefaultAppUtils defaultAppUtilsMock = new DefaultAppUtils() {
			@Override
			public boolean isNotificationsAvailable(Context context) {
				return true;
			}

			@Override
			public boolean isLocationAvailable(Context context) {
				return true;
			}
		};

		// We want notifications enabled, but we don't want to try to register
		final NotificationRegistrationSystem notificationRegistrationSystem = new SocializeNotificationRegistrationSystem() {
			@Override
			public boolean isRegisteredSocialize(Context context, User user) {
				return true;
			}

			@Override
			public boolean isRegisteredC2DM(Context context) {
				return true;
			}

			@Override
			public void registerC2DMAsync(Context context) {
				// Do nothing
			}

			@Override
			public synchronized void registerC2DM(Context context) {
				// Do nothing
			}

			@Override
			public void registerSocialize(Context context, String registrationId) {
				// Do nothing
			}

			@Override
			public void registerC2DMFailed(Context context, String cause) {
				// Do nothing
			}
		};

		final SocializeSubscriptionUtils socializeSubscriptionUtils = new SocializeSubscriptionUtils() {
			@Override
			public void isSubscribed(Activity context, Entity e, SubscriptionType type, SubscriptionCheckListener listener) {
				listener.onNotSubscribed();
			}
		};

		// Setup the proxies
		SocializeAccess.setInitListener(new SocializeInitListener() {
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				ProxyObject<AppUtils> appUtils = SocializeAccess.getProxy("appUtils");
				ProxyObject<NotificationRegistrationSystem> registrationSystem = SocializeAccess.getProxy("notificationRegistrationSystem");
				ProxyObject<SubscriptionUtilsProxy> subscriptionUtils = SocializeAccess.getProxy("subscriptionUtils");

				appUtils.setDelegate(defaultAppUtilsMock);
				registrationSystem.setDelegate(notificationRegistrationSystem);
				subscriptionUtils.setDelegate(socializeSubscriptionUtils);
			}
		});

		final CountDownLatch latch = new CountDownLatch(1);

		CommentUtils.showCommentView(context, entity, new OnCommentViewActionListener() {
			@Override
			public void onCreate(CommentListView view) {
				view.setDefaultGrabLength(grabLength);
			}

			@Override
			public void onRender(CommentListView view) {}

			@Override
			public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {
				addResult(0, view);
				latch.countDown();
			}

			@Override
			public void onBeforeSetComment(Comment comment, CommentListItem item) {}

			@Override
			public void onAfterSetComment(Comment comment, CommentListItem item) {}

			@Override
			public void onReload(CommentListView view) {}

			@Override
			public void onPostComment(Comment comment) {}

			@Override
			public void onError(SocializeException error) {}

			@Override
			public boolean onRefreshMenuItemClick(MenuItem item) {
				return false;
			}

			@Override
			public boolean onSettingsMenuItemClick(MenuItem item) {
				return false;
			}
		});


		Activity commentActivity = TestUtils.waitForActivity(10000);

		assertTrue(latch.await(20, TimeUnit.SECONDS));

		final CommentListView view = getResult(0);

		assertNotNull(view);

		CommentAdapter commentAdapter = view.getCommentAdapter();

		assertNotNull(commentAdapter);

		List<Comment> comments = commentAdapter.getComments();

		assertNotNull(comments);
		assertEquals(grabLength, comments.size());

		int count = commentAdapter.getCount();

		if(commentAdapter.isDisplayLoading()) {
			count--;
		}

		assertEquals(grabLength, count);

		for (int i = 0; i < count; i++) {
			assertNotNull(commentAdapter.getItem(i));
		}

		// Get next set
		final CountDownLatch latch2 = new CountDownLatch(1);
		view.setDefaultGrabLength(grabLength*2);
		view.setOnCommentViewActionListener(new OnCommentViewActionListener() {
			@Override
			public void onCreate(CommentListView view) {}

			@Override
			public void onRender(CommentListView view) {}

			@Override
			public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {
				addResult(0, view);
				latch2.countDown();
			}

			@Override
			public void onBeforeSetComment(Comment comment, CommentListItem item) {}

			@Override
			public void onAfterSetComment(Comment comment, CommentListItem item) {}

			@Override
			public void onReload(CommentListView view) {}

			@Override
			public void onPostComment(Comment comment) {}

			@Override
			public void onError(SocializeException error) {}

			@Override
			public boolean onRefreshMenuItemClick(MenuItem item) {
				return false;
			}

			@Override
			public boolean onSettingsMenuItemClick(MenuItem item) {
				return false;
			}
		});

		view.getNextSet();

		assertTrue(latch2.await(20, TimeUnit.SECONDS));

		comments = commentAdapter.getComments();

		assertNotNull(comments);
		assertEquals(grabLength*3, comments.size());

		final CountDownLatch latch3 = new CountDownLatch(1);

		view.getCommentEntryViewSlider().onActionBarSliderMoveListener = new OnActionBarSliderMoveListener() {
			@Override
			public void onOpen(ActionBarSliderView view) {
				latch3.countDown();
			}

			@Override
			public void onClose(ActionBarSliderView view) {

			}
		};

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				view.getCommentEntryField().performClick();
			}
		});

		assertTrue(latch3.await(20, TimeUnit.SECONDS));

		// Check the comment entry slider
		assertEquals(ActionBarSliderView.DisplayState.MAXIMIZE, view.getCommentEntryViewSlider().getDisplayState());

		commentActivity.finish();
	}

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
