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
package com.socialize.test.ui.integrationtest.actionbar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.api.action.like.LikeSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.mock.MockLikeSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.auth.AuthPanelView;


/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactoryTest extends ActionBarAutoTest {

	public void testAuthRequestDialog() throws Throwable {
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
		
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		extras.putSerializable(Socialize.ENTITY_OBJECT, entity);
		intent.putExtras(extras);
		setActivityIntent(intent);
		
		TestUtils.waitForIdle(this, 5000);
		
		final ActionBarLayoutView actionBar = TestUtils.findView(getActivity(), ActionBarLayoutView.class, 20000);	
		final ActionBarView actionBarView = TestUtils.findView(getActivity(), ActionBarView.class, 20000);	
		
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
		
		AuthPanelView shareView = TestUtils.findViewInDialog(getActivity(), AuthPanelView.class, 10000);	
		assertNotNull(shareView);
		assertTrue(shareView.isShown());
	}
	
}
