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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Application;
import com.socialize.entity.ApplicationFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityFactory;
import com.socialize.entity.Propagation;
import com.socialize.entity.PropagationFactory;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.PropagationInfoResponseFactory;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeActionFactory;
import com.socialize.entity.SocializeObjectFactory;
import com.socialize.entity.User;
import com.socialize.entity.UserFactory;
import com.socialize.test.SocializeActivityTest;

public abstract class AbstractSocializeActionFactoryTest<T extends SocializeAction, F extends SocializeActionFactory<T>> extends SocializeActivityTest {

	protected F factory;
	protected T action;
	protected SocializeConfig config;

	protected Application application;
	protected User user;
	protected Entity entity;
	protected Propagation propagation;
	protected PropagationInfoResponse propagationInfoResponse;

	protected JSONObject json;
	protected JSONObject jsonApplication;
	protected JSONObject jsonPropagation;
	protected JSONObject jsonUser;
	protected JSONObject jsonEntity;
	protected JSONObject jsonPropagationResponse;

	protected ApplicationFactory appFactoryMock;
	protected UserFactory userFactoryMock;
	protected EntityFactory entityFactoryMock;
	protected PropagationFactory propagatorFactoryMock;
	protected PropagationInfoResponseFactory propagationInfoResponseFactory;

	protected Double lat = new Double(10);
	protected Double lon = new Double(20);
	protected Date date = new Date(1311193971000L);
	protected Long id = new Long(10000);
	protected String entity_key = "foo";
	protected String entity_name = "bar";

	protected final DateFormat DATE_FORMAT_STRING = new SimpleDateFormat(SocializeObjectFactory.DATE_FORMAT_STRING);

	@UsesMocks({ 
		Application.class, 
		User.class, 
		Entity.class, 
		Propagation.class, 
		PropagationInfoResponse.class,
		ApplicationFactory.class, 
		UserFactory.class, 
		EntityFactory.class, 
		PropagationFactory.class,
		PropagationInfoResponseFactory.class,
		JSONArray.class,
		JSONObject.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		config = AndroidMock.createMock(SocializeConfig.class);

		application = AndroidMock.createMock(Application.class);
		user = AndroidMock.createMock(User.class);
		entity = AndroidMock.createMock(Entity.class);
		propagation = AndroidMock.createMock(Propagation.class);
		propagationInfoResponse = AndroidMock.createMock(PropagationInfoResponse.class);
		jsonPropagation = AndroidMock.createNiceMock(JSONObject.class);
		jsonPropagationResponse = AndroidMock.createNiceMock(JSONObject.class);
		jsonApplication = AndroidMock.createNiceMock(JSONObject.class);
		jsonUser = AndroidMock.createNiceMock(JSONObject.class);
		jsonEntity = AndroidMock.createNiceMock(JSONObject.class);
		
		AndroidMock.expect(config.getProperty((String) AndroidMock.anyObject())).andReturn(null);
		AndroidMock.replay(config);

		AndroidMock.replay(application);
		AndroidMock.replay(user);

		appFactoryMock = AndroidMock.createMock(ApplicationFactory.class);
		userFactoryMock = AndroidMock.createMock(UserFactory.class);
		entityFactoryMock = AndroidMock.createMock(EntityFactory.class);
		propagatorFactoryMock = AndroidMock.createMock(PropagationFactory.class);
		propagationInfoResponseFactory = AndroidMock.createMock(PropagationInfoResponseFactory.class);

		json = AndroidMock.createMock(JSONObject.class);
		action = AndroidMock.createMock(getActionClass());

		factory = createFactory();
		factory.setApplicationFactory(appFactoryMock);
		factory.setEntityFactory(entityFactoryMock);
		factory.setUserFactory(userFactoryMock);
		factory.setPropagationFactory(propagatorFactoryMock);
		factory.setPropagationInfoResponseFactory(propagationInfoResponseFactory);
	}

	@UsesMocks({ SocializeObjectFactory.class, JSONObject.class })
	public void testToJSON() throws JSONException {

		AndroidMock.expect(appFactoryMock.toJSON(this.application)).andReturn(jsonApplication);
		AndroidMock.expect(userFactoryMock.toJSON(this.user)).andReturn(jsonUser);
		AndroidMock.expect(entityFactoryMock.toJSON(this.entity)).andReturn(jsonEntity);
		AndroidMock.expect(propagatorFactoryMock.toJSON(this.propagation)).andReturn(jsonPropagation).times(2);

		AndroidMock.expect(json.put("application", jsonApplication)).andReturn(json);
		AndroidMock.expect(json.put("user", jsonUser)).andReturn(json);
		AndroidMock.expect(json.put("entity", jsonEntity)).andReturn(json);
		AndroidMock.expect(json.put("propagation", jsonPropagation)).andReturn(json);
		AndroidMock.expect(json.put("propagation_info_request", jsonPropagation)).andReturn(json);

		AndroidMock.expect(json.put("id", id)).andReturn(json);
		AndroidMock.expect(json.put("lat", lat)).andReturn(json);
		AndroidMock.expect(json.put("lng", lon)).andReturn(json);
		AndroidMock.expect(json.put("date", DATE_FORMAT_STRING.format(date))).andReturn(json);
		AndroidMock.expect(json.put("share_location", true)).andReturn(json);

		AndroidMock.expect(action.getId()).andReturn(id);
		AndroidMock.expect(action.getApplication()).andReturn(application);
		AndroidMock.expect(action.getPropagation()).andReturn(propagation);
		AndroidMock.expect(action.getUser()).andReturn(user);
		AndroidMock.expect(action.getEntity()).andReturn(entity);
		AndroidMock.expect(action.getEntityKey()).andReturn(entity_key);
		AndroidMock.expect(action.getLat()).andReturn(lat);
		AndroidMock.expect(action.getLon()).andReturn(lon);
		AndroidMock.expect(action.getDate()).andReturn(date.getTime());
		AndroidMock.expect(action.isLocationShared()).andReturn(true);
		AndroidMock.expect(action.getPropagationInfoRequest()).andReturn(propagation);

		AndroidMock.expect(entity.getKey()).andReturn(entity_key);
		AndroidMock.expect(entity.getName()).andReturn(entity_name);

		AndroidMock.replay(entity,jsonApplication,jsonUser,jsonEntity,appFactoryMock,userFactoryMock,entityFactoryMock,propagatorFactoryMock);

		setupToJSONExpectations();

		AndroidMock.replay(json);
		AndroidMock.replay(action);

		factory.toJSON(action);
		
		AndroidMock.verify(entity,jsonApplication,jsonUser,jsonEntity,appFactoryMock,userFactoryMock,entityFactoryMock,propagatorFactoryMock);

		doToJSONVerify();

		AndroidMock.verify(json);
		AndroidMock.verify(action);
	}

	public void testFromJSON() throws JSONException, ParseException {

		action.setId(id);
		action.setLat(lat);
		action.setLon(lon);
		action.setDate(date.getTime());
		action.setLocationShared(true);

		AndroidMock.expect(json.has("share_location")).andReturn(true);
		AndroidMock.expect(json.isNull("share_location")).andReturn(false);
		AndroidMock.expect(json.getBoolean("share_location")).andReturn(true);

		AndroidMock.expect(json.has("application")).andReturn(true);
		AndroidMock.expect(json.isNull("application")).andReturn(false);
		
		AndroidMock.expect(json.has("propagation_info_response")).andReturn(true);
		AndroidMock.expect(json.isNull("propagation_info_response")).andReturn(false);

		AndroidMock.expect(json.has("user")).andReturn(true);
		AndroidMock.expect(json.isNull("user")).andReturn(false);

		AndroidMock.expect(json.has("entity")).andReturn(true);
		AndroidMock.expect(json.isNull("entity")).andReturn(false);

		AndroidMock.expect(json.has("entity_key")).andReturn(false);

		AndroidMock.expect(json.has("id")).andReturn(true);
		AndroidMock.expect(json.isNull("id")).andReturn(false);
		AndroidMock.expect(json.getLong("id")).andReturn(id);

		AndroidMock.expect(json.getJSONObject("application")).andReturn(jsonApplication);
		AndroidMock.expect(json.getJSONObject("user")).andReturn(jsonUser);
		AndroidMock.expect(json.getJSONObject("entity")).andReturn(jsonEntity);
		AndroidMock.expect(json.getJSONObject("propagation_info_response")).andReturn(jsonPropagationResponse);

		AndroidMock.expect(appFactoryMock.fromJSON(jsonApplication)).andReturn(application);
		AndroidMock.expect(userFactoryMock.fromJSON(jsonUser)).andReturn(user);
		AndroidMock.expect(entityFactoryMock.fromJSON(jsonEntity)).andReturn(entity);
		AndroidMock.expect(propagationInfoResponseFactory.fromJSON(jsonPropagationResponse)).andReturn(propagationInfoResponse);

		action.setApplication(application);
		action.setUser(user);
		action.setEntity(entity);
		action.setEntityKey(null);
		action.setPropagationInfoResponse(propagationInfoResponse);

		AndroidMock.expect(json.has("lat")).andReturn(true);
		AndroidMock.expect(json.has("lng")).andReturn(true);
		AndroidMock.expect(json.has("date")).andReturn(true);

		AndroidMock.expect(json.isNull("lat")).andReturn(false);
		AndroidMock.expect(json.isNull("lng")).andReturn(false);
		AndroidMock.expect(json.isNull("date")).andReturn(false);

		AndroidMock.expect((Double) json.getDouble("lat")).andReturn(lat);
		AndroidMock.expect((Double) json.getDouble("lng")).andReturn(lon);
		AndroidMock.expect(json.getString("date")).andReturn(DATE_FORMAT_STRING.format(date));

		AndroidMock.replay(appFactoryMock, userFactoryMock, entityFactoryMock, propagatorFactoryMock, propagationInfoResponseFactory);

		setupFromJSONExpectations();

		AndroidMock.replay(json);
		AndroidMock.replay(action);

		factory.fromJSON(json);

		doFromJSONVerify();
		
		AndroidMock.verify(appFactoryMock, userFactoryMock, entityFactoryMock, propagatorFactoryMock, propagationInfoResponseFactory);

		AndroidMock.verify(json);
		AndroidMock.verify(action);
	}

	protected abstract void setupFromJSONExpectations() throws JSONException;

	protected abstract void doFromJSONVerify();

	protected abstract void setupToJSONExpectations() throws JSONException;

	protected abstract void doToJSONVerify();

	protected abstract Class<T> getActionClass();

	protected abstract F createFactory();

}
