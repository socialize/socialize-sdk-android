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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.socialize.entity.factory.ApplicationFactory;
import com.socialize.entity.factory.EntityFactory;
import com.socialize.entity.factory.SocializeActionFactory;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.entity.factory.UserFactory;
import com.socialize.test.SocializeActivityTest;

public abstract class AbstractSocializeActionFactoryTest<T extends SocializeAction, F extends SocializeActionFactory<T>> extends SocializeActivityTest {

	protected F factory;
	protected T action;
	protected SocializeConfig config;
	
	protected Application application;
	protected User user;
	protected Entity entity;
	
	protected JSONObject json;
	protected JSONObject jsonApplication;
	protected JSONObject jsonUser;
	protected JSONObject jsonEntity;
	
	protected ApplicationFactory appFactoryMock;
	protected UserFactory userFactoryMock;
	protected EntityFactory entityFactoryMock;
	
	protected Double lat = new Double(10);
	protected Double lon = new Double(20);
	protected Date date = new Date();
	protected Integer id = new Integer(10000);
	
	protected final DateFormat UTC_FORMAT = new SimpleDateFormat(SocializeObjectFactory.DATE_FORMAT_STRING);
	
	@UsesMocks({Application.class, User.class, Entity.class, ApplicationFactory.class, UserFactory.class, EntityFactory.class})
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
		
		AndroidMock.expect(config.getProperty((String)AndroidMock.anyObject())).andReturn(null);
		AndroidMock.replay(config);
		
		AndroidMock.replay(application);
		AndroidMock.replay(user);
		AndroidMock.replay(entity);
		
		appFactoryMock = AndroidMock.createMock(ApplicationFactory.class);
		userFactoryMock = AndroidMock.createMock(UserFactory.class);
		entityFactoryMock = AndroidMock.createMock(EntityFactory.class);
		
		action = AndroidMock.createNiceMock(getActionClass());

		json = AndroidMock.createNiceMock(JSONObject.class);
		
		factory = createFactory();
		factory.setApplicationFactory(appFactoryMock);
		factory.setEntityFactory(entityFactoryMock);
		factory.setUserFactory(userFactoryMock);
	}

	@UsesMocks({SocializeObjectFactory.class, JSONObject.class})
	public void testToJSON() throws JSONException {

		AndroidMock.expect(appFactoryMock.toJSON(this.application)).andReturn(jsonApplication);
		AndroidMock.expect(userFactoryMock.toJSON(this.user)).andReturn(jsonUser);
		AndroidMock.expect(entityFactoryMock.toJSON(this.entity)).andReturn(jsonEntity);
		
		AndroidMock.expect(json.put("application", jsonApplication)).andReturn(json);
		AndroidMock.expect(json.put("user", jsonUser)).andReturn(json);
		AndroidMock.expect(json.put("entity", jsonEntity)).andReturn(json);
		
		AndroidMock.expect(json.put("id", id)).andReturn(json);
		AndroidMock.expect(json.put("lat", lat)).andReturn(json);
		AndroidMock.expect(json.put("lon", lon)).andReturn(json);
		AndroidMock.expect(json.put("date",UTC_FORMAT.format(date))).andReturn(json);
		
		AndroidMock.expect(action.getId()).andReturn(id);
		AndroidMock.expect(action.getApplication()).andReturn(application);
		AndroidMock.expect(action.getUser()).andReturn(user);
		AndroidMock.expect(action.getEntity()).andReturn(entity);
		AndroidMock.expect(action.getLat()).andReturn(lat);
		AndroidMock.expect(action.getLon()).andReturn(lon);
		AndroidMock.expect(action.getDate()).andReturn(date.getTime());

		AndroidMock.replay(jsonApplication);
		AndroidMock.replay(jsonUser);
		AndroidMock.replay(jsonEntity);
		AndroidMock.replay(appFactoryMock);
		AndroidMock.replay(userFactoryMock);
		AndroidMock.replay(entityFactoryMock);
		
		setupToJSONExpectations();
		
		factory.toJSON(action);
		
		doToJSONVerify();
	}
	
	public void testFromJSON() throws JSONException, ParseException {
		
		AndroidMock.expect(json.has("application")).andReturn(true);
		AndroidMock.expect(json.has("user")).andReturn(true);
		AndroidMock.expect(json.has("entity")).andReturn(true);
		
		AndroidMock.expect(json.getJSONObject("application")).andReturn(jsonApplication);
		AndroidMock.expect(json.getJSONObject("user")).andReturn(jsonUser);
		AndroidMock.expect(json.getJSONObject("entity")).andReturn(jsonEntity);
		
		AndroidMock.expect(appFactoryMock.fromJSON(jsonApplication)).andReturn(application);
		AndroidMock.expect(userFactoryMock.fromJSON(jsonUser)).andReturn(user);
		AndroidMock.expect(entityFactoryMock.fromJSON(jsonEntity)).andReturn(entity);
		
		action.setApplication(application);
		action.setUser(user);
		action.setEntity(entity);
		
		AndroidMock.expect(json.has("lat")).andReturn(true);
		AndroidMock.expect(json.has("lon")).andReturn(true);
		AndroidMock.expect(json.has("date")).andReturn(true);
		
		AndroidMock.expect((Double)json.getDouble("lat")).andReturn(lat);
		AndroidMock.expect((Double)json.getDouble("lon")).andReturn(lon);
		AndroidMock.expect(json.getString("date")).andReturn(UTC_FORMAT.format(date));
		
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
