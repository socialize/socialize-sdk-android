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
	protected void create(JSONObject object, T entry) throws JSONException {

		try {
			SocializeObjectFactory<Application> applicationFactory = factoryService.getFactoryFor(Application.class);
			SocializeObjectFactory<User> userFactory = factoryService.getFactoryFor(User.class);
			SocializeObjectFactory<Entity> entityFactory = factoryService.getFactoryFor(Entity.class);
			
			
			entry.setApplication(applicationFactory.create(object));
			entry.setUser(userFactory.create(object));
			entry.setEntity(entityFactory.create(object));
			
			entry.setLat((float)object.getDouble("lat"));
			entry.setLon((float)object.getDouble("lon"));
			entry.setDate(UTC_FORMAT.parse(object.getString("date")));
			
		}
		catch (Exception e) {
			throw new JSONException(e.getMessage());
		}
		
		postCreate(object, entry);
	}
	
	protected abstract void postCreate(JSONObject object, T instance) throws JSONException;
	
}
