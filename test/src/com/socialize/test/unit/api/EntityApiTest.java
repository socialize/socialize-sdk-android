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
package com.socialize.test.unit.api;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.entity.SocializeEntitySystem;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class EntityApiTest extends SocializeUnitTest {

	SocializeProvider<Entity> provider;
	SocializeSession session;
	EntityListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = Mockito.mock(SocializeProvider.class);
		session = Mockito.mock(SocializeSession.class);
		listener = Mockito.mock(EntityListener.class);
	}

	public void testCreateEntity() {
		final String key = "foo";
		final String name = "bar";
		
		SocializeEntitySystem api = new SocializeEntitySystem(provider){
			
			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Entity> objects, SocializeActionListener listener) {
				addResult(objects);
			}

		};
		
		api.addEntity(session, Entity.newInstance(key, name), listener);
		
		List<Entity> result = getNextResult();
		
		assertNotNull(result);
		assertEquals(1, result.size());
		
		Entity e = result.get(0);
		
		assertNotNull(e.getName());
		assertEquals(name, e.getName());
		assertNotNull(e.getKey());
		assertEquals(key, e.getKey());
	}
	
	public void testGetEntity() {
		
		String key = "foo";
		
		SocializeEntitySystem api = new SocializeEntitySystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				addResult(key);
			}
		};
		
		api.getEntity(session, key, listener);
		
		String strId = getNextResult();
		
		assertNotNull(strId);
		assertEquals(key, strId);
	}
	
	@SuppressWarnings("unchecked")
	public void testGetEntityListenerSuccess() {
		
		String key = "foo";
		
		SocializeEntitySystem api = new SocializeEntitySystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				addResult(listener);
			}
		};
		
		Entity entity = Mockito.mock(Entity.class);
		ListResult<Entity> entities = Mockito.mock(ListResult.class);
		List<Entity> items = Mockito.mock(List.class);
		SocializeException exception = Mockito.mock(SocializeException.class);
		
		Mockito.when(entities.getItems()).thenReturn(items);
		Mockito.when(items.size()).thenReturn(1);
		Mockito.when(items.get(0)).thenReturn(entity);
		
		api.getEntity(session, key, listener);
		
		// get the listener
		EntityListListener listenerFound = getNextResult();
		
		assertNotNull(listenerFound);
		
		// Force each method call to simulate behavior.
		listenerFound.onError(exception);
		listenerFound.onList(entities);

        Mockito.verify(listener).onGet(entity);
        Mockito.verify(listener).onError(exception);

	}
	
	@SuppressWarnings("unchecked")
	public void testGetEntityListenerFail404() {
		
		String key = "foo";
		
		SocializeEntitySystem api = new SocializeEntitySystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, String key, String idKey, Map<String, String> extraParams, int startIndex, int endIndex, SocializeActionListener listener, String... ids) {
				addResult(listener);
			}
		};
		
		ListResult<Entity> entities = Mockito.mock(ListResult.class);
		List<Entity> items = Mockito.mock(List.class);
		SocializeException exception = Mockito.mock(SocializeException.class);
		
		Mockito.when(entities.getItems()).thenReturn(items);
		Mockito.when(items.size()).thenReturn(0);
		

		
		api.getEntity(session, key, listener);
		
		// get the listener
		EntityListListener listenerFound = getNextResult();
		
		assertNotNull(listenerFound);
		
		// Force each method call to simulate behavior.
		listenerFound.onError(exception);
		listenerFound.onList(entities);

        Mockito.verify(listener).onError(exception);
        Mockito.verify(listener).onError((SocializeApiError) Mockito.anyObject());
    }
	
}
