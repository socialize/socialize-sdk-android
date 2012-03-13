/*
 * Copyright (c) 2011 Socialize Inc.
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

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;

import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.LikeSystem;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.like.LikeListener;
import com.socialize.networks.ShareOptions;
import com.socialize.sample.mocks.MockLikeSystem;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.actionbar.ActionBarLayoutView;

/**
 * @author Jason Polites
 *
 */
public class ActionBarTestUtils2 {
	
	public void testLikeCallsApiHost(final ActivityInstrumentationTestCase2<?> testCase) throws Throwable {
		
		TestUtils.setupSocializeOverrides(true, true);
		
		Entity entity = Entity.newInstance("http://entity1.com", "no name");
		
		final MockLikeSystem mockLikeSystem = new MockLikeSystem() {
			@Override
			public void addLike(SocializeSession session, Entity entity, Location location, ShareOptions options, LikeListener listener) {
				TestUtils.addResult("success");
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
				ProxyObject<LikeSystem> proxy = container.getProxy("likeSystem");
				if(proxy != null) {
					proxy.setDelegate(mockLikeSystem);
				}
				else {
					System.err.println("Proxy is null!!");
				}
			}
		});
		
		Intent intent = new Intent();
		intent.putExtra(Socialize.ENTITY_OBJECT, entity);
		testCase.setActivityIntent(intent);
		
		testCase.getInstrumentation().waitForIdleSync();
		
		final ActionBarLayoutView actionBar = TestUtils.findView(testCase.getActivity(), ActionBarLayoutView.class, 25000);	
		
		ActivityInstrumentationTestCase2.assertNotNull(actionBar);
		
		testCase.runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivityInstrumentationTestCase2.assertTrue(actionBar.getLikeButton().performClick());
				TestUtils.sleep(1000);
				String result = TestUtils.getNextResult();
				ActivityInstrumentationTestCase2.assertNotNull(result);
				ActivityInstrumentationTestCase2.assertEquals("success", result);
			}
		});		
	}
	
}
