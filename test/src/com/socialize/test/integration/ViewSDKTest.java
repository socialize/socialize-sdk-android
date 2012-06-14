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
package com.socialize.test.integration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.view.ViewAddListener;



/**
 * @author Jason Polites
 *
 */
@Deprecated
public class ViewSDKTest extends SDKIntegrationTest {

	public void testCreateView() throws Throwable {
		final Entity e = Entity.newInstance(DEFAULT_GET_ENTITY, DEFAULT_GET_ENTITY);
		
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		Socialize.getSocialize().init(context);
		Socialize.getSocialize().authenticateSynchronous(context);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				// Get the entity to find its stats
				Socialize.getSocialize().getEntity(e.getKey(), new EntityGetListener() {
					
					@Override
					public void onGet(Entity entity) {
						addResult(0, entity);
						
						Socialize.getSocialize().view(context, e, new ViewAddListener() {
							
							@Override
							public void onError(SocializeException error) {
								error.printStackTrace();
								latch.countDown();
							}
							
							@Override
							public void onCreate(View entity) {
								addResult(1, entity);
								latch.countDown();
							}
						});
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Entity entity = getResult(0);
		View view = getResult(1);
		
		assertNotNull(entity);
		assertNotNull(view);
		
		assertEquals(entity.getKey(), view.getEntityKey());
		
		assertEquals(entity.getEntityStats().getViews().intValue()+1, view.getEntity().getEntityStats().getViews().intValue());
	}

}
