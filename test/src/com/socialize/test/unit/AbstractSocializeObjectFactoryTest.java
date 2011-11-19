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
package com.socialize.test.unit;

import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public abstract class AbstractSocializeObjectFactoryTest<T extends SocializeObject, F extends SocializeObjectFactory<T>> extends SocializeUnitTest {

	protected F factory;
	protected JSONObject json;
	protected Long id = new Long(9999);
	protected T object;
	
	@UsesMocks({SocializeConfig.class, JSONObject.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		json = AndroidMock.createMock(JSONObject.class);
		object = AndroidMock.createMock(getObjectClass());
		
		factory = createFactory();
	}
	
	
	public void testToJSON() throws Exception {
		
		AndroidMock.expect(object.getId()).andReturn(id);
		AndroidMock.expect(json.put("id", id)).andReturn(json);
		
		setupToJSONExpectations();
		
		AndroidMock.replay(json);
		AndroidMock.replay(object);
		
		factory.toJSON(object);
		
		AndroidMock.verify(json);
		AndroidMock.verify(object);
		
		doToJSONVerify();
	}
	

	public void testFromJSON() throws Exception {
		
		AndroidMock.expect(json.has("id")).andReturn(true);
		AndroidMock.expect(json.isNull("id")).andReturn(false);
		AndroidMock.expect(json.getLong("id")).andReturn(id);
		object.setId(id);
		
		setupFromJSONExpectations();
		
		AndroidMock.replay(json);
		AndroidMock.replay(object);
		
		factory.fromJSON(json);
		
		AndroidMock.verify(json);
		AndroidMock.verify(object);
		
		doFromJSONVerify();
		
	}
	
	public void testInstantiate() {
		T instantiateObject = factory.instantiateObject(json);
		assertTrue(getObjectClass().isAssignableFrom(instantiateObject.getClass()));
	}
	
	protected abstract void setupToJSONExpectations() throws Exception;
	
	protected abstract void doToJSONVerify();
	
	protected abstract void setupFromJSONExpectations() throws Exception;
	
	protected abstract void doFromJSONVerify();
	
	protected abstract Class<T> getObjectClass();
	
	protected abstract F createFactory();
}
