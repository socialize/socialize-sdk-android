package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Application;

public class ApplicationFactory extends SocializeObjectFactory<Application> {
	
	public ApplicationFactory() {
		super();
	}

	protected ApplicationFactory(FactoryService factoryService) {
		super(factoryService);
	}

	@Override
	public Application instantiateObject() {
		return new Application();
	}

	@Override
	protected void fromJSON(JSONObject object, Application entry) throws JSONException {
		entry.setName(object.getString("name"));
	}

	@Override
	protected void toJSON(Application entry, JSONObject object) throws JSONException {
		object.put("name", entry.getName());
	}
}
