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
package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Application;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeEntry;
import com.socialize.entity.User;

/**
 * @author Jason Polites
 *
 * @param <T>
 */
public abstract class EntryFactory<T extends SocializeEntry> extends SocializeObjectFactory<T> {
	
	public EntryFactory() {
		super();
	}

	protected EntryFactory(FactoryService factoryService) {
		super(factoryService);
	}

	@Override
	protected void toJSON(T from, JSONObject to) throws JSONException {
		try {
			SocializeObjectFactory<Application> applicationFactory = factoryService.getFactoryFor(Application.class);
			SocializeObjectFactory<User> userFactory = factoryService.getFactoryFor(User.class);
			SocializeObjectFactory<Entity> entityFactory = factoryService.getFactoryFor(Entity.class);
			
			JSONObject application = applicationFactory.toJSON(from.getApplication());
			JSONObject user = userFactory.toJSON(from.getUser());
			JSONObject entity = entityFactory.toJSON(from.getEntity());
			
			to.put("application", application);
			to.put("user", user);
			to.put("entity", entity);
			
			to.put("lat", from.getLat());
			to.put("lon", from.getLon());
			to.put("date", UTC_FORMAT.format(from.getDate()));
			
		}
		catch (Exception e) {
			throw new JSONException(e.getMessage());
		}
		
		postToJSON(from, to);
	}

	@Override
	protected void fromJSON(JSONObject from, T to) throws JSONException {

		try {
			SocializeObjectFactory<Application> applicationFactory = factoryService.getFactoryFor(Application.class);
			SocializeObjectFactory<User> userFactory = factoryService.getFactoryFor(User.class);
			SocializeObjectFactory<Entity> entityFactory = factoryService.getFactoryFor(Entity.class);
			
			to.setApplication(applicationFactory.fromJSON(from));
			to.setUser(userFactory.fromJSON(from));
			to.setEntity(entityFactory.fromJSON(from));
			
			to.setLat((float)from.getDouble("lat"));
			to.setLon((float)from.getDouble("lon"));
			to.setDate(UTC_FORMAT.parse(from.getString("date")));
			
		}
		catch (Exception e) {
			throw new JSONException(e.getMessage());
		}
		
		postFromJSON(from, to);
	}
	
	protected abstract void postToJSON(T from, JSONObject to) throws JSONException;
	
	protected abstract void postFromJSON(JSONObject from, T to) throws JSONException;
	
}
