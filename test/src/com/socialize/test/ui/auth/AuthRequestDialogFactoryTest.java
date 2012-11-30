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
package com.socialize.test.ui.auth;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.entity.SocializeEntityUtils;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.api.action.view.SocializeViewUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStatsImpl;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.sample.ui.ActionBarAutoActivity2;
import com.socialize.test.SocializeManagedActivityTest;
import com.socialize.test.PublicEntity;
import com.socialize.test.mock.MockLikeSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.auth.AuthPanelView;


/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactoryTest extends SocializeManagedActivityTest<ActionBarAutoActivity2> {
	
	final EntityStatsImpl entityStats = new EntityStatsImpl();
	final PublicEntity entity = new PublicEntity("http://entity1.com", "http://entity1.com");
	
	
	public AuthRequestDialogFactoryTest() {
		super("com.socialize.sample.ui", ActionBarAutoActivity2.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		entityStats.setComments(0);
		entityStats.setLikes(0);
		entityStats.setShares(0);
		entityStats.setViews(0);
		entity.setEntityStats(entityStats);
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		extras.putSerializable(Socialize.ENTITY_OBJECT, entity);
		intent.putExtras(extras);
		setActivityIntent(intent);	
	}

	public void testAuthRequestDialog() throws Throwable {
		
		final ActionBarAutoActivity2 context = (ActionBarAutoActivity2) TestUtils.getActivity(this);
		
		assertTrue(context.launchLock.await(10, TimeUnit.SECONDS));
		
		TestUtils.setupSocializeOverrides(true, true);
		
		// Ensure there is no like
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			
			@Override
			public void addLike(SocializeSession session, Entity entity, LikeOptions shareOptions, LikeListener listener, SocialNetwork... networks) {
				TestUtils.addResult("fail");
			}

			@Override
			public void getLike(SocializeSession session, String entityKey, LikeListener listener) {
				listener.onGet(null); // not liked
			}
		};		
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				ActivityInstrumentationTestCase2.fail();
			}
			@Override
			public void onInit(Context context, IOCContainer container) {
				
				// Enable auth prompt for this test
				SocializeConfig bean = container.getBean("config");
				bean.setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "true");
				
				ProxyObject<LikeSystem> proxyLike = container.getProxy("likeSystem");
				if(proxyLike != null) {
					proxyLike.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("proxyLike is null!!");
				}
			}
		});	
		
		// Ensure there is no like
		final View mockView = new View();
		mockView.setEntity(entity);
		
		// Don't record a view
		SocializeViewUtils mockViewUtils = new SocializeViewUtils() {
			@Override
			public void view(Activity context, Entity e, ViewAddListener listener) {
				listener.onCreate(mockView);
			}
		};
		
		SocializeEntityUtils mockEntityUtils = new SocializeEntityUtils() {
			@Override
			public void getEntity(Activity context, String key, EntityGetListener listener) {
				listener.onGet(entity);
			}
		};
		
		SocializeAccess.setViewUtilsProxy(mockViewUtils);
		SocializeAccess.setEntityUtilsProxy(mockEntityUtils);
		
		TestUtils.waitForIdle(this, 5000);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(context, ActionBarLayoutView.class, 20000);	
		final ActionBarView actionBarView = TestUtils.findView(context, ActionBarView.class, 20000);	
		
		assertNotNull(actionBar);
		assertNotNull(actionBarView);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Junit test runs in non-ui thread
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				assertTrue(actionBar.getLikeButton().performClick());
				latch.countDown();
			}
		});	
		
		latch.await(10, TimeUnit.SECONDS);
		
		AuthPanelView shareView = TestUtils.findViewInDialog(context, AuthPanelView.class, 10000);
		assertNotNull(shareView);
		assertTrue(shareView.isShown());
	}
	
}
