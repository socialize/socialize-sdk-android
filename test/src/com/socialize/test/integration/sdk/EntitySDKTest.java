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
package com.socialize.test.integration.sdk;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;



/**
 * @author Jason Polites
 *
 */
@Deprecated
public class EntitySDKTest extends SDKIntegrationTest {

	public void testCreateEntity() throws Throwable {
		final Entity e = Entity.newInstance("testCreateEntity", "testCreateEntity");
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().addEntity(context, e, new EntityAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
					
					@Override
					public void onCreate(Entity entity) {
						// Now do a get
						addResult(0, entity);
						
						Socialize.getSocialize().getEntity("testCreateEntity", new EntityGetListener() {
							
							@Override
							public void onError(SocializeException error) {
								error.printStackTrace();
								latch.countDown();
							}
							
							@Override
							public void onGet(Entity entity) {
								addResult(1, entity);
								latch.countDown();
							}
						});
						
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Entity entity1 = getResult(0);
		Entity entity2 = getResult(1);
		assertNotNull(entity1);
		assertNotNull(entity2);
		assertEquals(entity1, entity2);
	}

}
