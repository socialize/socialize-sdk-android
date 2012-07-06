package com.socialize.test.ui.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarButton;
import com.socialize.ui.actionbar.ActionBarItem;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.IAuthDialogFactory;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.share.IShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;


public class ActionBarLikeTest extends ActionBarTest {
	

	@Override
	public boolean isManual() {
		return false;
	}

	public void testLikePromptsForAuth() throws Throwable {
		Activity activity = TestUtils.getActivity(this);
		assertTrue(globalLatch.await(10, TimeUnit.SECONDS));
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		IAuthDialogFactory mockFactory = new IAuthDialogFactory() {
			
			@Override
			public void show(Context context, AuthDialogListener listener) {
				latch.countDown();
			}
		};
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		mockLikeUtils.setAuthDialogFactory(mockFactory);
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);		
		
		final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);	
		
		assertNotNull(actionBar);
		
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
			}
		});	
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}
	
	public void testLikeCallsApiHost() throws Throwable {
		
		
		Activity activity = TestUtils.getActivity(this);
		assertTrue(globalLatch.await(10, TimeUnit.SECONDS));
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Ensure FB/TW are not supported
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void like(Activity context, Entity entity, LikeAddListener listener) {
				latch.countDown();
			}

			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);	
		
		final ActionBarLayoutView actionBar = TestUtils.findView(TestUtils.getActivity(this), ActionBarLayoutView.class, 25000);	
		
		assertNotNull(actionBar);
		
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
			}
		});	
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}
	
	@UsesMocks ({IAuthDialogFactory.class})
	public void testLikeDoesNotPromptForAuthWhenNetworksNotSupported() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);
		assertTrue(globalLatch.await(10, TimeUnit.SECONDS));
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Ensure FB/TW are not supported
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");

		IAuthDialogFactory mockFactory = AndroidMock.createMock(IAuthDialogFactory.class);
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void like(Activity context, Entity entity, LikeAddListener listener) {
				latch.countDown();
			}

			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		AndroidMock.replay(mockFactory);
		
		mockLikeUtils.setAuthDialogFactory(mockFactory);
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);	
		
		final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 25000);	
		
		assertNotNull(actionBar);
		
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
			}
		});	
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		AndroidMock.verify(mockFactory);
	}	
	
	@UsesMocks ({IAuthDialogFactory.class})
	public void testLikeDoesNotPromptForAuthWhenUserIsAuthenticated() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);
		assertTrue(globalLatch.await(10, TimeUnit.SECONDS));
		
		final UserProviderCredentials creds = AndroidMock.createMock(UserProviderCredentials.class);
		final CountDownLatch latch = new CountDownLatch(1);

		IAuthDialogFactory mockFactory = AndroidMock.createMock(IAuthDialogFactory.class);
		IShareDialogFactory mockShareFactory = new IShareDialogFactory() {
			
			@Override
			public void show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener shareDialoglistener, int displayOptions) {
				latch.countDown();
			}
		};
		
		AndroidMock.replay(mockFactory);
		
		mockLikeUtils.setAuthDialogFactory(mockFactory);
		mockLikeUtils.setShareDialogFactory(mockShareFactory);
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);	
		
		final ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);	
		
		assertNotNull(actionBar);
		
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Dummy session
				Socialize.getSocialize().getSession().getUserProviderCredentials().put(AuthProviderType.TWITTER, creds);
				assertTrue(actionBar.getLikeButton().performClick());
			}
		});	
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		AndroidMock.verify(mockFactory);
		
		Socialize.getSocialize().getSession().getUserProviderCredentials().remove(AuthProviderType.TWITTER);
	}			
	
	public void testLikeStateIsRetained() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);
		assertTrue(globalLatch.await(10, TimeUnit.SECONDS));
		
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		final Like like = new Like();
		like.setId(69L);
		like.setEntity(entity);
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}

			@Override
			public void like(Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork... networks) {
				listener.onCreate(like); // Do the like
			}
		};		
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);		

		ActionBarLayoutView actionBar = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);
		
		assertNotNull(actionBar);
		
		final ActionBarButton likeButton = actionBar.getLikeButton();
		final ActionBarItem actionBarItem = likeButton.getActionBarItem();
		
		final ActionBarButton commentButton = actionBar.getCommentButton();
		
		String text = actionBarItem.getText();
		
		// Make sure we are not liked
		text = actionBarItem.getText();

		assertEquals("Like", text);
		
		doLike(actionBar);
		
		// Sleep to allow the like to post
		TestUtils.sleep(500);
		
		// Make sure we are liked
		text = actionBarItem.getText();

		assertEquals("Unlike", text);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(commentButton.performClick());
			}
		});
		
		Activity waitForActivity = TestUtils.waitForActivity(5000);
		
		assertNotNull(waitForActivity);
		
		waitForActivity.finish();
		
		// Now make sure we are still liked
		actionBar = TestUtils.findView(TestUtils.getActivity(this), ActionBarLayoutView.class, 5000);
		assertNotNull(actionBar);
		
		text = actionBarItem.getText();

		assertEquals("Unlike", text);
	}
	
	protected void doLike(final ActionBarLayoutView actionBar) throws Throwable {
		// We are already liked.. we need to unlike
		final CountDownLatch latch = new CountDownLatch(1);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				latch.countDown();
			}
		});				
		
		latch.await(5, TimeUnit.SECONDS);	
	}	
}
