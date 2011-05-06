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
	protected Application instantiate() {
		return new Application();
	}

	@Override
	protected void create(JSONObject object, Application entry) throws JSONException {
		entry.setName(object.getString("name"));
	}
}
