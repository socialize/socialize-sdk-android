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
package com.socialize.test.ui.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.ConfigUtils;
import com.socialize.SocializeAccess;
import com.socialize.api.action.entity.SocializeEntityUtils;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.SocializeLikeUtils;
import com.socialize.api.action.view.SocializeViewUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.View;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarReloadListener;


/**
 * @author Jason Polites
 *
 */
public class ActionBarReloadTest extends ActionBarTest {
	
	public void testReloadRace() throws Throwable {
		Activity activity = TestUtils.getActivity(this);

		// Ensure FB/TW are not supported
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		
		final Like like = new Like();
		like.setId(-1L);
		
		final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
		final ActionBarLayoutView actionBarView = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);	

		assertNotNull(actionBar);
		
		final int num = 10;
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}

			@Override
			public void like(Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork... networks) {
				TestUtils.addResult(entity);
				like.setEntity(entity);
				listener.onCreate(like);
			}
		};
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Simulate likes
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < num; i++) {
					actionBar.setEntity(Entity.newInstance("reloadTest" + i, "reloadTest" + i));
					actionBar.refresh();
					assertTrue(actionBarView.getLikeButton().performClick());
				}
				
				latch.countDown();
			}
		});	
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		
		for (int i = 0; i < num; i++) {
			Entity result = (Entity) TestUtils.getResult(i);
			assertNotNull(result);
			assertEquals("reloadTest" + i, result.getKey());
		}
	}
	
	public void testReloadListener() throws Throwable {
		Activity activity = TestUtils.getActivity(this);

		// Ensure FB/TW are not supported
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);

		assertNotNull(actionBar);
		
		final SocializeLikeUtils mockLikeUtils = new SocializeLikeUtils() {
			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}
		};
		
		final SocializeEntityUtils mockEntityUtils = new SocializeEntityUtils() {
			@Override
			public void getEntity(Activity context, String key, EntityGetListener listener) {
				if(listener != null) {
					listener.onGet(Entity.newInstance(key, key));
				}
			}
		};
	
		
		SocializeAccess.setLikeUtilsProxy(mockLikeUtils);
		SocializeAccess.setEntityUtilsProxy(mockEntityUtils);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final OnActionBarReloadListener reloadListener = new OnActionBarReloadListener() {
			@Override
			public void onReload(Entity entity) {
				TestUtils.addResult(0, entity);
				latch.countDown();
			}
		};
		
		final String name = "foobar111";
		
		// Simulate likes
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				actionBar.setEntity(Entity.newInstance(name, name));
				actionBar.refresh(reloadListener);
			}
		});	
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		Entity result = (Entity) TestUtils.getResult(0);
		assertNotNull(result);
		assertEquals(name, result.getKey());
	}	
	
	// Tests that reloading with a delay for the view does not override
	// The entity if the entity has changed.
	public void testReloadWithDelay() throws Throwable {
		
		Activity activity = TestUtils.getActivity(this);

		// Ensure FB/TW are not supported
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.FACEBOOK_APP_ID, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_KEY, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.TWITTER_CONSUMER_SECRET, "");
		ConfigUtils.getConfig(activity).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		final CountDownLatch viewLatch = new CountDownLatch(1);
		final CountDownLatch likeLatch = new CountDownLatch(1);
		
		final View view = new View();
		final Like like = new Like();
		
		final Entity entity0 = Entity.newInstance("reloadTest0", "reloadTest0");
		final Entity entity1 = Entity.newInstance("reloadTest1", "reloadTest1");
		
		view.setId(-1L);
		view.setEntity(entity0);
		like.setId(-1L);
		
		SocializeViewUtils viewUtils = new SocializeViewUtils() {
			@Override
			public void view(Activity context, Entity e, final ViewAddListener listener) {
				// run in separate thread
				try {
					runTestOnUiThread(
						new Runnable() {

							@Override
							public void run() {
								try {
									viewLatch.await();
								}
								catch (InterruptedException ignore) {}
								finally {
									listener.onCreate(view);
								}
							}

						});
				}
				catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		};
		
		final SocializeLikeUtils likeUtils = new SocializeLikeUtils() {
			@Override
			public void getLike(Activity context, String entityKey, LikeGetListener listener) {
				listener.onGet(null); // not liked
			}

			@Override
			public void like(Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork... networks) {
				TestUtils.addResult(0, entity);
				like.setEntity(entity);
				listener.onCreate(like);
			}
		};		
		
		SocializeAccess.setViewUtilsProxy(viewUtils);
		SocializeAccess.setLikeUtilsProxy(likeUtils);
		
		final ActionBarView actionBar = TestUtils.findView(activity, ActionBarView.class, 5000);
		final ActionBarLayoutView actionBarView = TestUtils.findView(activity, ActionBarLayoutView.class, 10000);	

		assertNotNull(actionBar);	
		
		// Set the first entity
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				actionBar.setEntity(entity0);
				actionBar.refresh();
			}
		});					
				
		// Don't count down the latch yet
		// Set the second entity
		actionBar.setEntity(entity1);
		
		// Count down the first latch
		viewLatch.countDown();
		
		this.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBarView.getLikeButton().performClick());
				likeLatch.countDown();
			}
		});	
		
		likeLatch.await(5, TimeUnit.SECONDS);
		
		Entity entity = TestUtils.getResult(0);
		
		// It should be the second entity
		assertNotNull(entity);
		assertEquals(entity1.getKey(), entity.getKey());
	}
	

	/* (non-Javadoc)
	 * @see com.socialize.test.ui.actionbar.ActionBarTest#isManual()
	 */
	@Override
	public boolean isManual() {
		return false;
	}

}
