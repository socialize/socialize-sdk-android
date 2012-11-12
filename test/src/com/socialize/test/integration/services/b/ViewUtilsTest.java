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
import com.socialize.EntityUtils;
import com.socialize.Socialize;
import com.socialize.ViewUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class ViewUtilsTest extends SocializeActivityTest {


	public void testAddView() throws InterruptedException, SocializeException {
		
		String randomKey = "testAddView";
		Entity entity = Entity.newInstance(randomKey, "testAddView");
		
		final CountDownLatch entityLatch = new CountDownLatch(1);
		
		// Get the entity to ensure there are no views
		Socialize.getSocialize().init(getContext());
		Socialize.getSocialize().authenticateSynchronous(getContext());
		
		EntityUtils.getEntity(TestUtils.getActivity(this), randomKey, new EntityGetListener() {
			
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
		
		ViewUtils.view(TestUtils.getActivity(this), entity, new ViewAddListener() {
			
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
		
//		View view = (View) result;
//		assertTrue("View ID is not greater than 0", view.getId() > 0);
		
		Entity entityAfter = getResult(1);
		assertNotNull(entityAfter);
		assertEquals(entityAfter.getKey(), randomKey);
		assertNotNull(entityAfter.getEntityStats());
		
		
		assertTrue(entityAfter.getEntityStats().getViews().intValue() >= views);
		
		// View count updates on the server asynchronously so we can't assert the count here.
//		assertEquals(views+1, entityAfter.getEntityStats().getViews().intValue());
	}
	
	public void testGetViewExists() throws InterruptedException {
		
		final Entity entity = Entity.newInstance("testGetViewExists", "testGetViewExists");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		ViewUtils.view(TestUtils.getActivity(this), entity, new ViewAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
			
			@Override
			public void onCreate(View view) {
				addResult(0, view);
			}
		});		
		
		latch.await(20, TimeUnit.SECONDS);
		
		View view = getResult(0);
		assertNotNull(view);
		assertEquals(entity.getKey(), view.getEntityKey());
	}
	
//	public void testGetViewsByUser() throws SocializeException, InterruptedException {
//		final User user = UserUtils.getCurrentUser(TestUtils.getActivity(this));
//		
//		final Entity entity = Entity.newInstance("testGetViewsByUser", "testGetViewsByUser");
//		
//		final CountDownLatch latch0 = new CountDownLatch(1);
//		final CountDownLatch latch1 = new CountDownLatch(1);
//		
//		
//		final Activity activity = TestUtils.getActivity(this);
//		
//		ViewUtils.view(activity, entity, new ViewAddListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				latch0.countDown();
//			}
//			
//			@Override
//			public void onCreate(View view) {
//				
//				addResult(0, view);
//				
//				latch0.countDown();
//			}
//		});		
//		
//		assertTrue(latch0.await(20, TimeUnit.SECONDS));
//		
//		View view = getResult(0);
//		assertNotNull("No view found for user.  This means the view was not created",  view);
//		
//		ViewUtils.getViewsByUser(activity, user, 0, 1, new ViewListListener() {
//			
//			@Override
//			public void onList(List<View> items, int totalSize) {
//				addResult(1, items);
//				latch1.countDown();
//			}
//			
//			@Override
//			public void onError(SocializeException error) {
//				addResult(2, error);
//				latch1.countDown();
//			}
//		});
//				
//		assertTrue(latch1.await(20, TimeUnit.SECONDS));
//		
//		List<View> items = getResult(1);
//		Exception error = getResult(2);
//		assertNotNull("No views found for user [" +
//				user.getId() +
//				"].  This means the view was created but there was an error retrieving the views after [" +
//				TestUtils.stackTraceToString(error) +
//				"]",  items);
//		
//		assertTrue(items.size() == 1);
//	}
	
//	public void testGetViewsByEntity() throws SocializeException, InterruptedException {
//		final Entity entity = Entity.newInstance("testGetViewsByEntity", "testGetViewsByEntity");
//		
//		final CountDownLatch latch = new CountDownLatch(1);
//		
//		ViewUtils.view(TestUtils.getActivity(this), entity, new ViewAddListener() {
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
//				ViewUtils.getViewsByEntity(TestUtils.getActivity(this), entity, 0, 100, new ViewListListener() {
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
