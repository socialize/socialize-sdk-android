/*
 * Copyright (c) 2011 Socialize Inc.
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

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.EntityApi;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.entity.EntityListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, EntityListener.class, SocializeProvider.class})
public class EntityApiTest extends SocializeUnitTest {

	SocializeProvider<Entity> provider;
	SocializeSession session;
	EntityListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(EntityListener.class);
	}

	public void testCreateEntity() {
		final String key = "foo";
		final String name = "bar";
		
		EntityApi api = new EntityApi(provider){
			@Override
			public void putAsync(SocializeSession session, String endpoint, Entity object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		api.createEntity(session, key, name, listener);
		
		Entity result = (Entity) getResult();
		assertNotNull(result);
		
		assertNotNull(result.getName());
		assertEquals(name, result.getName());
		assertNotNull(result.getKey());
		assertEquals(key, result.getKey());
	}
	
//	public void testGetEntity() {
//		
//		int id = 69;
//		
//		EntityApi api = new EntityApi(provider) {
//
//			@Override
//			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
//				addResult(id);
//			}
//		};
//		
//		api.getEntity(session, id, listener);
//		
//		String strId = getResult();
//		
//		assertNotNull(strId);
//		assertEquals(String.valueOf(id), strId);
//	}
}
