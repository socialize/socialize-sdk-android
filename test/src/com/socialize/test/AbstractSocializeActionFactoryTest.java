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
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.entity.factory.FactoryService;
import com.socialize.entity.factory.SocializeActionFactory;
import com.socialize.entity.factory.SocializeObjectFactory;

public abstract class AbstractSocializeActionFactoryTest<T extends SocializeAction, F extends SocializeActionFactory<T>> extends SocializeActivityTest {

	protected F factory;
	protected T action;
	protected FactoryService factoryService;
	protected SocializeConfig config;
	
	protected Application application;
	protected User user;
	protected Entity entity;
	
	protected JSONObject json;
	protected JSONObject jsonApplication;
	protected JSONObject jsonUser;
	protected JSONObject jsonEntity;
	
	protected SocializeObjectFactory<Application> appFactoryMock;
	protected SocializeObjectFactory<User> userFactoryMock;
	protected SocializeObjectFactory<Entity> entityFactoryMock;
	
	protected float lat = 10;
	protected float lon = 20;
	protected Date date = new Date();
	protected int id = 10000;
	
	@SuppressWarnings("unchecked")
	@UsesMocks({FactoryService.class, Application.class, User.class, Entity.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		config = AndroidMock.createMock(SocializeConfig.class);
		
		application = AndroidMock.createMock(Application.class);
		user = AndroidMock.createMock(User.class);
		entity = AndroidMock.createMock(Entity.class);
		
		jsonApplication = AndroidMock.createNiceMock(JSONObject.class);
		jsonUser = AndroidMock.createNiceMock(JSONObject.class);
		jsonEntity = AndroidMock.createNiceMock(JSONObject.class);
		
		AndroidMock.expect(config.getProperties()).andReturn(null);
		AndroidMock.replay(config);
		
		AndroidMock.replay(application);
		AndroidMock.replay(user);
		AndroidMock.replay(entity);
		
		factoryService = AndroidMock.createMock(FactoryService.class);
		
		appFactoryMock = AndroidMock.createMock(SocializeObjectFactory.class);
		userFactoryMock = AndroidMock.createMock(SocializeObjectFactory.class);
		entityFactoryMock = AndroidMock.createMock(SocializeObjectFactory.class);
		
		action = AndroidMock.createNiceMock(getActionClass());

		json = AndroidMock.createNiceMock(JSONObject.class);
		
		factory = createFactory();
		factory.setFactoryService(factoryService);
	}

	@UsesMocks({SocializeObjectFactory.class, JSONObject.class})
	public void testToJSON() throws JSONException {

		AndroidMock.expect(factoryService.getFactoryFor(Application.class)).andReturn(appFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(User.class)).andReturn(userFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(Entity.class)).andReturn(entityFactoryMock);
		
		AndroidMock.expect(appFactoryMock.toJSON(this.application)).andReturn(jsonApplication);
		AndroidMock.expect(userFactoryMock.toJSON(this.user)).andReturn(jsonUser);
		AndroidMock.expect(entityFactoryMock.toJSON(this.entity)).andReturn(jsonEntity);
		
		AndroidMock.expect(json.put("application", jsonApplication)).andReturn(json);
		AndroidMock.expect(json.put("user", jsonUser)).andReturn(json);
		AndroidMock.expect(json.put("entity", jsonEntity)).andReturn(json);
		
		AndroidMock.expect(json.put("id", id)).andReturn(json);
		AndroidMock.expect(json.put("lat", lat)).andReturn(json);
		AndroidMock.expect(json.put("lon", lon)).andReturn(json);
		AndroidMock.expect(json.put("date",SocializeObjectFactory.UTC_FORMAT.format(date))).andReturn(json);
		
		AndroidMock.expect(action.getId()).andReturn(id);
		AndroidMock.expect(action.getApplication()).andReturn(application);
		AndroidMock.expect(action.getUser()).andReturn(user);
		AndroidMock.expect(action.getEntity()).andReturn(entity);
		AndroidMock.expect(action.getLat()).andReturn(lat);
		AndroidMock.expect(action.getLon()).andReturn(lon);
		AndroidMock.expect(action.getDate()).andReturn(date);

		AndroidMock.replay(jsonApplication);
		AndroidMock.replay(jsonUser);
		AndroidMock.replay(jsonEntity);
		AndroidMock.replay(factoryService);
		AndroidMock.replay(appFactoryMock);
		AndroidMock.replay(userFactoryMock);
		AndroidMock.replay(entityFactoryMock);
		AndroidMock.replay(json);
		
		setupToJSONExpectations();
		
		factory.toJSON(action);
		
		doToJSONVerify();
	}
	
	public void testFromJSON() throws JSONException, ParseException {
		
		AndroidMock.expect(factoryService.getFactoryFor(Application.class)).andReturn(appFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(User.class)).andReturn(userFactoryMock);
		AndroidMock.expect(factoryService.getFactoryFor(Entity.class)).andReturn(entityFactoryMock);
		
		AndroidMock.expect(appFactoryMock.fromJSON(json)).andReturn(application);
		AndroidMock.expect(userFactoryMock.fromJSON(json)).andReturn(user);
		AndroidMock.expect(entityFactoryMock.fromJSON(json)).andReturn(entity);
		
		action.setApplication(application);
		action.setUser(user);
		action.setEntity(entity);
		
		AndroidMock.expect((float)json.getDouble("lat")).andReturn(lat);
		AndroidMock.expect((float)json.getDouble("lon")).andReturn(lon);
		AndroidMock.expect(json.getString("date")).andReturn(SocializeObjectFactory.UTC_FORMAT.format(date));
		
		AndroidMock.replay(factoryService);
		AndroidMock.replay(appFactoryMock);
		AndroidMock.replay(userFactoryMock);
		AndroidMock.replay(entityFactoryMock);
		
		setupFromJSONExpectations();
		
		factory.fromJSON(json);
		
		doFromJSONVerify();
	}
	
	
	protected abstract void setupFromJSONExpectations() throws JSONException;
	
	protected abstract void doFromJSONVerify();
	
	protected abstract void setupToJSONExpectations() throws JSONException;
	
	protected abstract void doToJSONVerify();
	
	protected abstract Class<T> getActionClass();
	
	protected abstract F createFactory();
	
	
}
