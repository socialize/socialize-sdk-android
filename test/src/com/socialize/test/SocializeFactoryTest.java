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
package com.socialize.test;

import java.util.Properties;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Application;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.ApplicationFactory;
import com.socialize.entity.factory.CommentFactory;
import com.socialize.entity.factory.FactoryService;
import com.socialize.entity.factory.SocializeObjectFactory;

public class SocializeFactoryTest extends SocializeActivityTest {

	@UsesMocks({SocializeConfig.class})
	public void testDefaultFactoriesIntializedWithNoExternalConfig() {

		SocializeConfig conf = AndroidMock.createMock(SocializeConfig.class);

		AndroidMock.expect(conf.getProperties()).andReturn(null);

		AndroidMock.replay(conf);

		FactoryService service = new FactoryService(conf);

		Assert.assertNotNull(service.getFactoryFor(Application.class));
		Assert.assertEquals(ApplicationFactory.class, service.getFactoryFor(Application.class).getClass());
		Assert.assertNotNull(service.getFactoryFor(Comment.class));
		Assert.assertEquals(CommentFactory.class, service.getFactoryFor(Comment.class).getClass());
	}
	
	/**
	 * Tests factory creation with manually configured props
	 */
	@UsesMocks({SocializeConfig.class})
	public void testDefaultFactoriesIntializedWithManualConfig() {

		SocializeConfig conf = AndroidMock.createMock(SocializeConfig.class);
		Properties props = new Properties();
		props.put("factory.com.socialize.entity.Comment", "com.socialize.entity.factory.CommentFactory");

		AndroidMock.expect(conf.getProperties()).andReturn(props);

		AndroidMock.replay(conf);

		FactoryService service = new FactoryService(conf);

		Assert.assertNotNull(service.getFactoryFor(Comment.class));
		Assert.assertEquals(CommentFactory.class, service.getFactoryFor(Comment.class).getClass());
	}


	@UsesMocks({SocializeObjectFactory.class, JSONObject.class, SocializeObject.class})
	public void testSocializeObjectFactoryFromJSONMethod() throws JSONException {
		
		final SocializeObject entry = AndroidMock.createMock(SocializeObject.class);
		final JSONObject json = AndroidMock.createNiceMock(JSONObject.class);
		final int id = 1;
		
		SocializeObjectFactory<SocializeObject> factory = new SocializeObjectFactory<SocializeObject>() {
			@Override
			public SocializeObject instantiateObject() {
				return entry;
			}

			@Override
			protected void fromJSON(JSONObject from, SocializeObject to) throws JSONException {
				// Just a dummy to make it easier to assert that create() was called
				from.put("foo", "bar");
			}

			@Override
			protected void toJSON(SocializeObject from, JSONObject to) throws JSONException {}

		};
		
		entry.setId(id);
		
		AndroidMock.expect(json.getInt("id")).andReturn(id);
		AndroidMock.expect(json.put("foo", "bar")).andReturn(json);
		
		AndroidMock.replay(entry);
		AndroidMock.replay(json);
		
		factory.fromJSON(json);
		
		AndroidMock.verify(entry);
		AndroidMock.verify(json);
		
	}
	
	
	@UsesMocks({SocializeObjectFactory.class, JSONObject.class, Entity.class})
	public void testSocializeObjectFactoryToJSONMethod() throws JSONException {
		
		final Entity entry = AndroidMock.createMock(Entity.class);
		final JSONObject json = AndroidMock.createNiceMock(JSONObject.class);
		final int id = 1;
		
		SocializeObjectFactory<Entity> factory = new SocializeObjectFactory<Entity>() {
			@Override
			public Entity instantiateObject() {
				return entry;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}

			@Override
			protected void fromJSON(JSONObject from, Entity to) throws JSONException {
			
			}

			@Override
			protected void toJSON(Entity from, JSONObject to) throws JSONException {
				// Just a dummy to make it easier to assert that create() was called
				from.setKey("foobar");
			}

		};
		
		
		entry.setKey("foobar");
		
		AndroidMock.expect(json.put("id", id)).andReturn(json);
		AndroidMock.expect(entry.getId()).andReturn(id);
		
		AndroidMock.replay(entry);
		AndroidMock.replay(json);
		
		factory.toJSON(entry);
		
		AndroidMock.verify(entry);
		AndroidMock.verify(json);
		
	}

}
