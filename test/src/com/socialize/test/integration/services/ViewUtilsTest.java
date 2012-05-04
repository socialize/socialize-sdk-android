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
package com.socialize.test.integration.services;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.ViewUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.listener.view.ViewGetListener;
import com.socialize.listener.view.ViewListListener;
import com.socialize.test.SocializeActivityTest;


/**
 * @author Jason Polites
 *
 */
public class ViewUtilsTest extends SocializeActivityTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Socialize.getSocialize().destroy(true);
	}

	@Override
	protected void tearDown() throws Exception {
		Socialize.getSocialize().destroy(true);
		super.tearDown();
	}

	public void testAddView() throws InterruptedException {
		
		String randomKey = "testAddView";
		Entity entity = Entity.newInstance(randomKey, "testAddView");
		
		final CountDownLatch entityLatch = new CountDownLatch(1);
		
		// Get the entity to ensure there are no views
		Socialize.getSocialize().init(getContext());
		Socialize.getSocialize().authenticateSynchronous(getContext());
		
		Socialize.getSocialize().getEntity(randomKey, new EntityGetListener() {
			
			@Override
			public void onGet(Entity entity) {
				addResult(0, entity.getEntityStats().getViews());
				entityLatch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				addResult(0, 0);
				entityLatch.countDown();
			}
		});
		
		entityLatch.await(20, TimeUnit.SECONDS);
		
		Integer views = getResult(0);
		
		assertNotNull(views);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		clearResults();
		
		ViewUtils.view(getActivity(), entity, new ViewAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				addResult(0, error);
				latch.countDown();
			}
			
			@Override
			public void onCreate(View view) {
				addResult(0, view);
				addResult(1, view.getEntity());
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Object result = getResult(0);
		
		assertNotNull(result);
		assertTrue("Result is not a view object", (result instanceof View));
		
		View view = (View) result;
		assertTrue("View ID is not greater than 0", view.getId() > 0);
		
		Entity entityAfter = getResult(1);
		assertNotNull(entityAfter);
		assertEquals(entityAfter.getKey(), randomKey);
		assertNotNull(entityAfter.getEntityStats());
		assertEquals(views+1, entityAfter.getEntityStats().getViews().intValue());
	}
	
	public void testGetViewExists() throws InterruptedException {
		
		final Entity entity = Entity.newInstance("testGetViewExists", "testGetViewExists");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ViewUtils.view(getActivity(), entity, new ViewAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(View view) {
				ViewUtils.getView(getActivity(), entity, new ViewGetListener() {
					
					@Override
					public void onGet(View entity) {
						addResult(0, entity);
						latch.countDown();
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
		
		View view = getResult(0);
		assertNotNull(view);
		assertEquals(entity.getKey(), view.getEntityKey());
	}
	
	public void testGetViewsByUser() throws SocializeException, InterruptedException {
		final User user = UserUtils.getCurrentUser(getActivity());
		
		final Entity entity = Entity.newInstance("testGetViewsByUser", "testGetViewsByUser");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ViewUtils.view(getActivity(), entity, new ViewAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(View view) {
				
				ViewUtils.getViewsByUser(getActivity(), user, 0, 100, new ViewListListener() {
					
					@Override
					public void onList(List<View> items, int totalSize) {
						addResult(items);
						latch.countDown();
					}
					
					@Override
					public void onError(SocializeException error) {
						error.printStackTrace();
						fail();
					}
				});
				
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		List<View> items = getResult(0);
		assertNotNull(items);
		assertTrue(items.size() >= 1);
	}
	
//	public void testGetViewsByEntity() throws SocializeException, InterruptedException {
//		final Entity entity = Entity.newInstance("testGetViewsByEntity", "testGetViewsByEntity");
//		
//		final CountDownLatch latch = new CountDownLatch(1);
//		
//		ViewUtils.view(getActivity(), entity, new ViewAddListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				latch.countDown();
//			}
//			
//			@Override
//			public void onCreate(View view) {
//				
//				ViewUtils.getViewsByEntity(getActivity(), entity, 0, 100, new ViewListListener() {
//					
//					@Override
//					public void onList(List<View> items, int totalSize) {
//						addResult(items);
//						latch.countDown();
//					}
//					
//					@Override
//					public void onError(SocializeException error) {
//						error.printStackTrace();
//						fail();
//					}
//				});
//				
//			}
//		});		
//		
//		latch.await(20, TimeUnit.SECONDS);
//		
//		List<View> items = getResult(0);
//		assertNotNull(items);
//		assertTrue(items.size() >= 1);
//	}	
}
