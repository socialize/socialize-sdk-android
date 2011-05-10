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
	public Application instantiate() {
		return new Application();
	}

	@Override
	public void create(JSONObject object, Application entry) throws JSONException {
		entry.setName(object.getString("name"));
	}
}
