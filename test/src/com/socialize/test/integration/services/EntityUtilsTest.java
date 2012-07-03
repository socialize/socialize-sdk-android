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
		
		EntityUtils.saveEntity(getActivity(), entity, new EntityAddListener() {
			
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
		
		
		EntityUtils.getEntity(getActivity(), entityAfter.getId(), new EntityGetListener() {
			
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
	
}
