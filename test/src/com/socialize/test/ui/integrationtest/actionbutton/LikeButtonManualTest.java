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
package com.socialize.test.ui.integrationtest.actionbutton;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.action.EntitySystem;
import com.socialize.api.action.LikeSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStats;
import com.socialize.entity.Like;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.sample.EmptyActivity;
import com.socialize.sample.mocks.MockEntitySystem;
import com.socialize.sample.mocks.MockLikeSystem;
import com.socialize.sample.ui.ActionButtonActivity;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbutton.ActionButtonHandler;
import com.socialize.ui.actionbutton.ActionButtonHandlers;
import com.socialize.ui.actionbutton.ActionButtonLayoutView;
import com.socialize.ui.actionbutton.SocializeActionButton;
import com.socialize.ui.actionbutton.SocializeActionButtonAccess;

/**
 * @author Jason Polites
 *
 */
public class LikeButtonManualTest extends ActivityInstrumentationTestCase2<EmptyActivity> {
	public LikeButtonManualTest() {
		super("com.socialize.sample.ui", EmptyActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestUtils.setUp(this);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtils.tearDown();
		super.tearDown();
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({Entity.class, EntityStats.class, ActionButtonHandlers.class, ActionButtonHandler.class})
	public void testListButtonRenderAndClick() throws Throwable {
		TestUtils.setupSocializeOverrides(true, true);
		TestUtils.setUpActivityMonitor(ActionButtonActivity.class);
		
		final int likes = 69;
		final Like action = new Like();
		
		action.setId(100L);
		
		final Entity entity = AndroidMock.createMock(Entity.class);
		final EntityStats stats = AndroidMock.createMock(EntityStats.class);
		
		AndroidMock.expect(entity.getKey()).andReturn("foobar");
		AndroidMock.expect(entity.getEntityStats()).andReturn(stats).times(2);
		AndroidMock.expect(stats.getLikes()).andReturn(likes).times(2);
		
		AndroidMock.replay(entity, stats);
		
		action.setEntity(entity);
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem();
		final MockEntitySystem mockEntitySystem = new MockEntitySystem();
		
		mockLikeSystem.setAction(action);
		mockEntitySystem.setEntity(entity);
		
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
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
		
		Intent intent = new Intent(getActivity(), ActionButtonActivity.class);
		intent.putExtra("manual", true);
		
		getActivity().startActivity(intent);
		
		Activity activity = TestUtils.waitForActivity(5000);
		
		assertNotNull(activity);
		
		SocializeActionButton<SocializeAction> button = TestUtils.findViewWithText(activity, SocializeActionButton.class, "Superman (69)", 5000);
		
		assertNotNull(button);
		
		final ActionButtonLayoutView<SocializeAction> layoutView = SocializeActionButtonAccess.getLayoutView(button);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Click it
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				assertTrue(layoutView.performClick());
				latch.countDown();
			}
		});
		
		latch.await(5, TimeUnit.SECONDS);
		
		button = TestUtils.findViewWithText(activity, SocializeActionButton.class, "Bizzaro (69)", 5000); // should be 68 but 69 because entity mock is the same
		
		assertNotNull(button);
		
		AndroidMock.verify(entity, stats);
	}
}
