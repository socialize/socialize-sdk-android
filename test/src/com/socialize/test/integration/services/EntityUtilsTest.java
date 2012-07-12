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
import org.json.JSONArray;
import org.json.JSONObject;
import com.socialize.ConfigUtils;
import com.socialize.EntityUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class EntityUtilsTest extends SocializeActivityTest {
	

	public void testAddEntity() throws Exception {
		final String entityKey = "testAddEntity" + Math.random();
		Entity entity = Entity.newInstance(entityKey, "testAddEntity");
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		// Force no config
		ConfigUtils.getConfig(getContext()).setProperty(SocializeConfig.SOCIALIZE_REQUIRE_AUTH, "false");
		
		EntityUtils.saveEntity(TestUtils.getActivity(this), entity, new EntityAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				addResult(0, error);
				latch.countDown();
			}
			
			@Override
			public void onCreate(Entity entity) {
				addResult(0, entity);
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Object result = getResult(0);
		
		assertNotNull(result);
		assertTrue("Result is not a entity object", (result instanceof Entity));
		
		Entity entityAfter = (Entity) result;
		assertTrue("Entity ID is not greater than 0", entityAfter.getId() > 0);
		
		// Now use the normal UI to retrieve the entity
		clearResults();
		final CountDownLatch latch2 = new CountDownLatch(1);
		
		
		EntityUtils.getEntity(TestUtils.getActivity(this), entityAfter.getId(), new EntityGetListener() {
			
			@Override
			public void onGet(Entity entity) {
				addResult(0, entity);
				latch2.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch2.countDown();
			}
		});
		
		latch2.await(20, TimeUnit.SECONDS);
		
		Entity after = getResult(0);
		
		assertNotNull(after);
		assertEquals(entityAfter.getId(), after.getId());
		assertEquals(entityAfter.getKey(), after.getKey());
	}
	
	public void testGetEntity() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		EntityUtils.getEntity(getContext(), "http://entity1.com", new EntityGetListener() {
			
			@Override
			public void onGet(Entity entity) {
				addResult(0, entity);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		Entity after = getResult(0);
		
		assertNotNull(after);
		assertEquals("http://entity1.com", after.getKey());
		
		assertNotNull(after.getEntityStats());
		assertNotNull(after.getUserEntityStats());
	}	
	

	public void testGetEntitiesByKey() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		
		EntityUtils.getEntities(getContext(), new EntityListListener() {
			
			@Override
			public void onList(ListResult<Entity> entities) {
				addResult(0, entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		}, "http://entity1.com", "http://entity2.com");
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Entity> after = getResult(0);
		
		assertNotNull(after);
		assertEquals(2, after.size());
		
		// Make sure it contains what we expect.
		JSONObject json = TestUtils.getJSON(getContext(), "entities.json");
		JSONArray jsonArray = json.getJSONArray("items");
		
		JSONObject jsonObject0 = (JSONObject) jsonArray.get(0);
		JSONObject jsonObject1 = (JSONObject) jsonArray.get(1);
		
		String key0 = jsonObject0.getString("key");
		String key1 = jsonObject1.getString("key");
		
		List<Entity> items = after.getItems();
		
		int found = 0;
		
		for (Entity entity : items) {
			if(entity.getKey().equals(key0) || entity.getKey().equals(key1)) {
				found++;
			}
			
			if(found >= 2) {
				break;
			}
		}
		
		assertEquals(2, found);
	}	
	
	public void testGetEntities() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);
		
		EntityUtils.getEntities(getContext(), 0, 100, new EntityListListener() {
			
			@Override
			public void onList(ListResult<Entity> entities) {
				addResult(0, entities);
				latch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		ListResult<Entity> after = getResult(0);
		
		assertNotNull(after);
		assertTrue(after.size() >= 2);
		
		// Make sure it contains what we expect.
		JSONObject json = TestUtils.getJSON(getContext(), "entities.json");
		JSONArray jsonArray = json.getJSONArray("items");
		
		
		JSONObject jsonObject0 = (JSONObject) jsonArray.get(0);
		JSONObject jsonObject1 = (JSONObject) jsonArray.get(1);
		
		String key0 = jsonObject0.getString("key");
		String key1 = jsonObject1.getString("key");
		
		List<Entity> items = after.getItems();
		
		int found = 0;
		
		for (Entity entity : items) {
			if(entity.getKey().equals(key0) || entity.getKey().equals(key1)) {
				found++;
			}
			
			if(found >= 2) {
				break;
			}
		}
		
		assertEquals(2, found);
	}
	
//	public void testGetEntitiesWithSort() throws Exception {
//		
//		final Activity context = getContext();
//		
//		// 3 entities
//		String[] keys = {"sortEntityA-" + Math.random(), "sortEntityB-" + Math.random(), "sortEntityC-" + Math.random()};
//
//		// Create a latch for each item so we guarantee order
//		final CountDownLatch[] createLatches = new CountDownLatch[] {
//			new CountDownLatch(1),
//			new CountDownLatch(1),
//			new CountDownLatch(1),
//		};
//		
//		final List<Entity> entities = new ArrayList<Entity>(keys.length);
//		
//		for (int i = 0; i < createLatches.length; i++) {
//			
//			final int count = i;
//	
//			EntityUtils.saveEntity(context, Entity.newInstance(keys[i], keys[i]), new EntityAddListener() {
//				@Override
//				public void onError(SocializeException error) {
//					error.printStackTrace();
//					createLatches[count].countDown();
//				}
//				
//				@Override
//				public void onCreate(Entity result) {
//					
//					Log.e("Socialize", "created entity [" +
//							result.getKey() +
//							"] at " + System.currentTimeMillis());
//					
//					entities.add(result);
//					createLatches[count].countDown();
//				}
//			});
//			
//			assertTrue(createLatches[i].await(10, TimeUnit.SECONDS));
//		}
//		
//		assertEquals(3, entities.size());
//		
//		// Get by normal means
//		final CountDownLatch getLatch = new CountDownLatch(1);
//		
//		EntityUtils.getEntities(context, SortOrder.CREATION_DATE, new EntityListListener() {
//			
//			@Override
//			public void onList(ListResult<Entity> entities) {
//				addResult(0, entities);
//				getLatch.countDown();
//			}
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				getLatch.countDown();
//			}
//		}, keys);
//		
//		assertTrue(getLatch.await(20, TimeUnit.SECONDS));
//		
//		ListResult<Entity> after = getResult(0);
//		
//		assertNotNull(after);
//		assertEquals(3, after.size());
//		
//		// Check the order.. should be C/B/A
//		List<Entity> items = after.getItems();
//		
//		assertEquals(keys[2], items.get(0).getKey());
//		assertEquals(keys[1], items.get(1).getKey());
//		assertEquals(keys[0], items.get(2).getKey());
//		
//		// Now make comments/likes
//		final CountDownLatch actionLatch = new CountDownLatch(3);
//		
//		// Make sure we're not interrupted
//		CommentOptions commentOptions = CommentUtils.getUserCommentOptions(context);
//		commentOptions.setShowAuthDialog(false);
//		commentOptions.setShowShareDialog(false);		
//		
//		LikeOptions likeOptions = LikeUtils.getUserLikeOptions(context);
//		likeOptions.setShowAuthDialog(false);
//		likeOptions.setShowShareDialog(false);		
//		
//		// A
//		CommentUtils.addComment(context, entities.get(0), "sortEntityA comment", commentOptions, new CommentAddListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				actionLatch.countDown();
//			}
//			
//			@Override
//			public void onCreate(Comment result) {
//				actionLatch.countDown();
//			}
//		});
//		
//		LikeUtils.like(context, entities.get(0), likeOptions, new LikeAddListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				actionLatch.countDown();
//			}
//			
//			@Override
//			public void onCreate(Like result) {
//				actionLatch.countDown();
//			}
//		});
//		
//		// B
//		CommentUtils.addComment(context, entities.get(1), "sortEntityB comment", commentOptions, new CommentAddListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				actionLatch.countDown();
//			}
//			
//			@Override
//			public void onCreate(Comment result) {
//				actionLatch.countDown();
//			}
//		});		
//		
//		assertTrue(actionLatch.await(20, TimeUnit.SECONDS));
//		
//		// Now get by activity
//		final CountDownLatch activityLatch = new CountDownLatch(1);
//		
//		EntityUtils.getEntities(context, SortOrder.TOTAL_ACTIVITY, new EntityListListener() {
//			
//			@Override
//			public void onList(ListResult<Entity> entities) {
//				addResult(1, entities);
//				activityLatch.countDown();
//			}
//			
//			@Override
//			public void onError(SocializeException error) {
//				error.printStackTrace();
//				activityLatch.countDown();
//			}
//		}, keys);
//		
//		assertTrue(activityLatch.await(20, TimeUnit.SECONDS));
//		
//		ListResult<Entity> sorted = getResult(1);
//		
//		assertNotNull(after);
//		assertEquals(3, sorted.size());
//		
//		// Check the order.. should be A/B/C
//		List<Entity> sortedItems = after.getItems();
//		
//		assertEquals(keys[0], sortedItems.get(0).getKey());
//		assertEquals(keys[1], sortedItems.get(1).getKey());
//		assertEquals(keys[2], sortedItems.get(2).getKey());
//	}	
	
}
