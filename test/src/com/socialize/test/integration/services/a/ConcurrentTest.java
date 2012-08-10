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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.ConfigUtils;
import com.socialize.LikeUtils;
import com.socialize.Socialize;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class ConcurrentTest extends SocializeActivityTest {
	
	@Override
	protected void tearDown() throws Exception {
		System.clearProperty("debug");
		super.tearDown();
	}

	public void testConcurrentLikeIsSameUser() throws Throwable {
		
		System.setProperty("debug", "true");
		
		final Entity entityKey = Entity.newInstance("ConcurrentTest", "ConcurrentTest");
		
		final Activity context = getContext();
		
		// Force no notifications
		ConfigUtils.getConfig(getContext()).setProperty(SocializeConfig.SOCIALIZE_NOTIFICATIONS_ENABLED, "false");
		
		// Make sure we don't have a cached session
		Socialize.getSocialize().init(context);
		Socialize.getSocialize().clearSessionCache(context);
		Socialize.getSocialize().destroy(true);
		
		TestUtils.waitForIdle(this, 5000);
		
		// Force no auth
		final LikeOptions options = LikeUtils.getUserLikeOptions(getContext());
		options.setShowAuthDialog(false);
		options.setShowShareDialog(false);
		
		// Create a latch to wait for completion
		final CountDownLatch completeLatch = new CountDownLatch(2);
		
		// Create two threads which perform full init/auth/like
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				LikeUtils.like(context, entityKey, options, new LikeAddListener() {
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						addResult(3, error);
						completeLatch.countDown();
					}
					
					@Override
					public void onCreate(Like entity) {
						addResult(0, entity);
						completeLatch.countDown();
					}
				});		
			}
		});
		
		// Create two threads which perform full init/auth/like
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				LikeUtils.like(context, entityKey, options, new LikeAddListener() {
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						addResult(3, error);
						completeLatch.countDown();
					}
					
					@Override
					public void onCreate(Like entity) {
						addResult(1, entity);
						completeLatch.countDown();
					}
				});		
			}
		});
		
		// Wait for threads to finish
		assertTrue(completeLatch.await(30, TimeUnit.SECONDS));
		
		SocializeException error = getResult(3);
		
		if(error != null) {
			fail("Like was not posted.  Last error was " + error.getMessage());
		}
		
		Like like0 = getResult(0);
		Like like1 = getResult(1);
		
		// Users should be the same
		assertNotNull(like0);
		assertNotNull(like1);
		
		assertEquals(like0.getUser(), like1.getUser());
	}
	
	
}
