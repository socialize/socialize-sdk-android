package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Application;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeEntry;
import com.socialize.entity.User;

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
