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
package com.socialize.test.integration.services.b;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.SubscriptionUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Subscription;
import com.socialize.error.SocializeException;
import com.socialize.listener.subscription.SubscriptionCheckListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.notifications.SubscriptionType;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class SubscriptionUtilsTest extends SocializeActivityTest {

	public void testSubscribeNewComments() throws Exception {
		doTestForType(SubscriptionType.NEW_COMMENTS);
	}
	
	public void testSubscribeEntity() throws Exception {
		doTestForType(SubscriptionType.ENTITY_NOTIFICATION);
	}
	
	protected void doTestForType(SubscriptionType type) throws Exception {

		Activity context = TestUtils.getActivity(this);
		Entity entity = Entity.newInstance("http://www.entity1.com" + Math.random(), "");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		SubscriptionUtils.isSubscribed(context, entity, type, new SubscriptionCheckListener() {
			
			@Override
			public void onSubscribed(Subscription subscription) {
				latch.countDown();
			}



			@Override
			public void onNotSubscribed() {
				latch.countDown();
			}



			@Override
			public void onError(SocializeException error) {
				addResult(0, error);
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		Exception result = getResult(0);
		assertNull(result);
		
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		SubscriptionUtils.subscribe(context, entity, type, new SubscriptionResultListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch1.countDown();
			}
			
			@Override
			public void onCreate(Subscription result) {
				addResult(1, result);
				latch1.countDown();
			}
		});
		
		
		assertTrue(latch1.await(20, TimeUnit.SECONDS));
		Subscription result1 = getResult(1);
		
		assertNotNull(result1);
		assertTrue(result1.isSubscribed());
		
		SubscriptionUtils.unsubscribe(context, entity, type, new SubscriptionResultListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
			
			@Override
			public void onCreate(Subscription result) {
				addResult(2, result);
				latch2.countDown();
			}
		});
		
		
		assertTrue(latch2.await(10, TimeUnit.SECONDS));
		Subscription result2 = getResult(2);
		
		assertNotNull(result2);
		assertTrue(result2.isSubscribed() != result1.isSubscribed());				
		
	}
	
	
}
