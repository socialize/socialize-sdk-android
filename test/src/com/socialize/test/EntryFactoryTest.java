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

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Application;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeEntry;
import com.socialize.entity.User;
import com.socialize.entity.factory.EntryFactory;
import com.socialize.entity.factory.FactoryService;
import com.socialize.entity.factory.SocializeObjectFactory;

public class EntryFactoryTest extends SocializeActivityTest {

	private EntryFactory<SocializeEntry> factory;
	private SocializeEntry entry;
	private FactoryService factoryService;
	private SocializeConfig config;
	
	private Application application;
	private User user;
	private Entity entity;
	
	private JSONObject json;
	
	private SocializeObjectFactory<Application> appFactoryMock;
	private SocializeObjectFactory<User> userFactoryMock;
	private SocializeObjectFactory<Entity> entityFactoryMock;

	
	@SuppressWarnings("unchecked")
	@UsesMocks({FactoryService.class, Application.class, User.class, Entity.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		config = AndroidMock.createMock(SocializeConfig.class);
		
		application = AndroidMock.createMock(Application.class);
		user = AndroidMock.createMock(User.class);
		entity = AndroidMock.createMock(Entity.class);
		
		AndroidMock.expect(config.getProperties()).andReturn(null);
		AndroidMock.replay(config);
		
		AndroidMock.replay(application);
		AndroidMock.replay(user);
		AndroidMock.replay(entity);
		
		factoryService = AndroidMock.createMock(FactoryService.class, config);
		
		appFactoryMock = AndroidMock.createMock(SocializeObjectFactory.class, factoryService);
		userFactoryMock = AndroidMock.createMock(SocializeObjectFactory.class, factoryService);
		entityFactoryMock = AndroidMock.createMock(SocializeObjectFactory.class, factoryService);
	
		entry = new SocializeEntry();
		entry.setLat(10);
		entry.setLon(20);
		entry.setDate(new Date());
		entry.setApplication(application);
		entry.setUser(user);
		entry.setEntity(entity);
		
		json = AndroidMock.createNiceMock(JSONObject.class);
		
		factory = new EntryFactory<SocializeEntry>(factoryService) {
			
			@Override
			public SocializeEntry instantiateObject() {
				return entry;
			}
			
			@Override
			public JSONObject instantiateJSON() {
				return json;
			}

			@Override
			protected void postToJSON(SocializeEntry from, JSONObject to) throws JSONException {}
			
			@Override
			protected void postFromJSON(JSONObject from, SocializeEntry to) throws JSONException {}
		};
	}

	@UsesMocks({SocializeObjectFactory.class, JSONObject.class})
	public void testToJSON() throws JSONException {

		JSONObject jsonApplication = AndroidMock.createNiceMock(JSONObject.class);
		JSONObject jsonUser = AndroidMock.createNiceMock(JSONObject.class);
		JSONObject jsonEntity = AndroidMock.createNiceMock(JSONObject.class);

		AndroidMock.expect(factoryService.getFactoryFor(Application.class)).andReturn(appFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(User.class)).andReturn(userFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(Entity.class)).andReturn(entityFactoryMock);
		
		AndroidMock.expect(appFactoryMock.toJSON(this.application)).andReturn(jsonApplication);
		AndroidMock.expect(userFactoryMock.toJSON(this.user)).andReturn(jsonUser);
		AndroidMock.expect(entityFactoryMock.toJSON(this.entity)).andReturn(jsonEntity);
		
		AndroidMock.expect(json.put("application", jsonApplication)).andReturn(json);
		AndroidMock.expect(json.put("user", jsonUser)).andReturn(json);
		AndroidMock.expect(json.put("entity", jsonEntity)).andReturn(json);
		
		AndroidMock.expect(json.put("lat", entry.getLat())).andReturn(json);
		AndroidMock.expect(json.put("lon", entry.getLon())).andReturn(json);
		AndroidMock.expect(json.put("date",SocializeObjectFactory.UTC_FORMAT.format(entry.getDate()))).andReturn(json);
		
		AndroidMock.replay(jsonApplication);
		AndroidMock.replay(jsonUser);
		AndroidMock.replay(jsonEntity);
		AndroidMock.replay(factoryService);
		AndroidMock.replay(appFactoryMock);
		AndroidMock.replay(userFactoryMock);
		AndroidMock.replay(entityFactoryMock);
		AndroidMock.replay(json);
		
		factory.toJSON(entry);
		
		AndroidMock.verify(factoryService);
		AndroidMock.verify(appFactoryMock);
		AndroidMock.verify(userFactoryMock);
		AndroidMock.verify(entityFactoryMock);
		AndroidMock.verify(json);
	}
	
	public void testFromJSON() throws JSONException, ParseException {
		
		AndroidMock.expect(factoryService.getFactoryFor(Application.class)).andReturn(appFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(User.class)).andReturn(userFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(Entity.class)).andReturn(entityFactoryMock);
		
		AndroidMock.expect(appFactoryMock.fromJSON(json)).andReturn(application);
		AndroidMock.expect(userFactoryMock.fromJSON(json)).andReturn(user);
		AndroidMock.expect(entityFactoryMock.fromJSON(json)).andReturn(entity);
		
		entry.setApplication(application);
		entry.setUser(user);
		entry.setEntity(entity);
		
		AndroidMock.expect((float)json.getDouble("lat")).andReturn(entry.getLat());
		AndroidMock.expect((float)json.getDouble("lon")).andReturn(entry.getLon());
		AndroidMock.expect(json.getString("date")).andReturn(SocializeObjectFactory.UTC_FORMAT.format(entry.getDate()));
		
		AndroidMock.replay(factoryService);
		AndroidMock.replay(appFactoryMock);
		AndroidMock.replay(userFactoryMock);
		AndroidMock.replay(entityFactoryMock);
		AndroidMock.replay(json);
		
		factory.fromJSON(json);
		
		AndroidMock.verify(factoryService);
		AndroidMock.verify(appFactoryMock);
		AndroidMock.verify(userFactoryMock);
		AndroidMock.verify(entityFactoryMock);
		AndroidMock.verify(json);
	}
	
}
