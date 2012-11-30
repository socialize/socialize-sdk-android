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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.ConfigUtils;
import com.socialize.EntityUtils;
import com.socialize.EntityUtils.SortOrder;
import com.socialize.api.action.entity.SocializeEntityUtils;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.util.EntityLoaderUtils;


/**
 * @author Jason Polites
 *
 */
public class EntityUtilsTest extends SocializeActivityTest {
	
	public void testEntityMetaDataWithUrl() throws Exception {
		
		final String entityKey = "testEntityMetaDataWithUrl" + Math.random();
		
		Entity entity = Entity.newInstance(entityKey, "testAddEntity");
		
		String url = "http://www.getsocialize.com";
		
		JSONObject metaData = new JSONObject();
		metaData.put("url", url);
		
		String jsonString = metaData.toString();
		
		entity.setMetaData(jsonString);
		
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
		
		String metaDataAfter = entityAfter.getMetaData();
		
		assertNotNull(metaDataAfter);
		
		JSONObject json = new JSONObject(metaDataAfter);
		String urlAfter = json.getString("url");
		
		assertEquals(url, urlAfter);
	}

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
	
	public void testGetEntitiesWithSort() throws Exception {
		
		final Activity context = getContext();
		
		// 3 entities (these are created from python script)
		String[] keys = {"http://entityA.com", "http://entityB.com", "http://entityC.com"};
		
		// Get by normal means
		final CountDownLatch getLatch = new CountDownLatch(1);
		
		EntityUtils.getEntities(context, SortOrder.CREATION_DATE, new EntityListListener() {
			
			@Override
			public void onList(ListResult<Entity> entities) {
				addResult(0, entities);
				getLatch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				getLatch.countDown();
			}
		}, keys);
		
		assertTrue(getLatch.await(20, TimeUnit.SECONDS));
		
		ListResult<Entity> after = getResult(0);
		
		assertNotNull(after);
		assertEquals(3, after.size());
		
		// Check the order.. should be C/B/A
		List<Entity> items = after.getItems();
		
		assertEquals(keys[2], items.get(0).getKey());
		assertEquals(keys[1], items.get(1).getKey());
		assertEquals(keys[0], items.get(2).getKey());
		
		// Now get by activity
		final CountDownLatch activityLatch = new CountDownLatch(1);
		
		EntityUtils.getEntities(context, SortOrder.TOTAL_ACTIVITY, new EntityListListener() {
			
			@Override
			public void onList(ListResult<Entity> entities) {
				addResult(1, entities);
				activityLatch.countDown();
			}
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				activityLatch.countDown();
			}
		}, keys);
		
		assertTrue(activityLatch.await(20, TimeUnit.SECONDS));
		
		ListResult<Entity> sorted = getResult(1);
		
		assertNotNull(sorted);
		assertEquals(3, sorted.size());
		
		// Check the order.. should be A/B/C
		List<Entity> sortedItems = sorted.getItems();
		
		assertEquals(keys[0], sortedItems.get(0).getKey());
		assertEquals(keys[1], sortedItems.get(1).getKey());
		assertEquals(keys[2], sortedItems.get(2).getKey());
	}	
	
	@UsesMocks ({SocializeEntityLoader.class, EntityLoaderUtils.class})
	public void testShowEntityByKey() {
		final Entity entity = new Entity();
		
		SocializeEntityUtils utils = new SocializeEntityUtils() {
			@Override
			public void getEntity(Activity context, String key, EntityGetListener listener) {
				listener.onGet(entity);
			}
		};
		
		SocializeEntityLoader entityLoader = AndroidMock.createMock(SocializeEntityLoader.class);
		EntityLoaderUtils entityLoaderUtils = AndroidMock.createMock(EntityLoaderUtils.class);
		
		AndroidMock.expect(entityLoaderUtils.initEntityLoader()).andReturn(entityLoader);
		AndroidMock.expect(entityLoader.canLoad(TestUtils.getActivity(this), entity)).andReturn(true);
		
		entityLoader.loadEntity(TestUtils.getActivity(this), entity);
		
		AndroidMock.replay(entityLoaderUtils, entityLoader);
		
		utils.setEntityLoaderUtils(entityLoaderUtils);
		utils.showEntity(TestUtils.getActivity(this), "foobar", null);
		
		AndroidMock.verify(entityLoaderUtils, entityLoader);
	}
	
	@UsesMocks ({SocializeEntityLoader.class, EntityLoaderUtils.class})
	public void testShowEntityById() {
		final Entity entity = new Entity();
		
		SocializeEntityUtils utils = new SocializeEntityUtils() {
			@Override
			public void getEntity(Activity context, long id, EntityGetListener listener) {
				listener.onGet(entity);
			}
		};
		
		SocializeEntityLoader entityLoader = AndroidMock.createMock(SocializeEntityLoader.class);
		EntityLoaderUtils entityLoaderUtils = AndroidMock.createMock(EntityLoaderUtils.class);
		
		AndroidMock.expect(entityLoaderUtils.initEntityLoader()).andReturn(entityLoader);
		AndroidMock.expect(entityLoader.canLoad(TestUtils.getActivity(this), entity)).andReturn(true);
		
		entityLoader.loadEntity(TestUtils.getActivity(this), entity);
		
		AndroidMock.replay(entityLoaderUtils, entityLoader);
		
		utils.setEntityLoaderUtils(entityLoaderUtils);
		utils.showEntity(TestUtils.getActivity(this), 123, null);
		
		AndroidMock.verify(entityLoaderUtils, entityLoader);
	}	
	
}
