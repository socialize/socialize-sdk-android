package com.socialize.entity.factory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.SocializeObject;

public abstract class SocializeObjectFactory<T extends SocializeObject> {
	
	// Injected
	protected FactoryService factoryService;
	
	public static final DateFormat UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
	
	public SocializeObjectFactory() {
		super();
	}

	protected SocializeObjectFactory(FactoryService factoryService) {
		super();
		this.factoryService = factoryService;
	}

	public T create(JSONObject object) throws JSONException {
		T entry = instantiate();
		
		entry.setId(object.getInt("id"));
		create(object, entry);
		
		return entry;
	}
	
	protected abstract T instantiate();
	
	protected abstract void create(JSONObject object, T entry) throws JSONException;
}
