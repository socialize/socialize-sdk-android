package com.socialize.test.ui.integrationtest.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.entity.EntitySystem;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.ShareOptions;
import com.socialize.sample.ui.ActionBarManualActivity2;
import com.socialize.test.mock.MockEntitySystem;
import com.socialize.test.mock.MockLikeSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarButton;
import com.socialize.ui.actionbar.ActionBarItem;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.comment.CommentActivity;

public class ActionBarLikeStateTest extends ActivityInstrumentationTestCase2<ActionBarManualActivity2> {

	public ActionBarLikeStateTest() {
		super("com.socialize.sample.ui", ActionBarManualActivity2.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.setUp(this);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown();
		super.tearDown();
	}

	public void testLikeStateIsRetained() throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		TestUtils.setUpActivityMonitor(CommentActivity.class);
		
		Entity entity = Entity.newInstance("http://entity23.com", "no name");
		
		final MockEntitySystem mockEntitySystem = new MockEntitySystem();
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			
			int callCount = 0;

			@Override
			public void addLike(SocializeSession session, Entity entity, ShareOptions options, LikeListener listener) {
				super.addLike(session, entity, options, listener);
			}

			@Override
			public void deleteLike(SocializeSession session, long id, LikeListener listener) {
				super.deleteLike(session, id, listener);
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				if(callCount == 0) {
					callCount++;
					if(listener != null) listener.onError(new SocializeApiError(404, "MOCK ERROR"));
				}
				else {
					super.getLike(session, entityKey, listener);
				}
			}
		};
		
		Like like = new Like();
		like.setId(69L);
		like.setEntity(entity);
		
		mockLikeSystem.setAction(like);
		mockEntitySystem.setEntity(entity);
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				
				// Disable auth prompt for this test
				SocializeConfig bean = container.getBean("config");
				bean.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");			
				
				ProxyObject<LikeSystem> likeSystemProxy = container.getProxy("likeSystem");
				ProxyObject<EntitySystem> entitySystemProxy = container.getProxy("entitySystem");
				if(likeSystemProxy != null) {
					likeSystemProxy.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("likeSystemProxy is null!!");
				}
				
				if(entitySystemProxy != null) {
					entitySystemProxy.setDelegate(mockEntitySystem);
				}
				else {
					System.err.println("entitySystemProxy is null!!");
				}		
			}
		});			
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		setActivityIntent(intent);
		
		ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 20000);
		
		assertNotNull(actionBar);
		
		final ActionBarButton likeButton = actionBar.getLikeButton();
		final ActionBarItem actionBarItem = likeButton.getActionBarItem();
		
		final ActionBarButton commentButton = actionBar.getCommentButton();
		
		// Sleep to allow the bar to load
		TestUtils.sleep(500);
		
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
		actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 5000);
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
				boolean performClick = actionBar.getLikeButton().performClick();
				latch.countDown();
				assertTrue(performClick);
			}
		});				
		
		latch.await(5, TimeUnit.SECONDS);	
	}
}
