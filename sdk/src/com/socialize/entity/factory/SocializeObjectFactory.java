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
	
	public JSONObject toJSON(T entry) throws JSONException {
		JSONObject json = instantiateJSON();
		json.put("id", entry.getId());
		toJSON(entry, json);
		return json;
	}

	public T fromJSON(JSONObject object) throws JSONException {
		T entry = instantiateObject();
		
		entry.setId(object.getInt("id"));
		
		fromJSON(object, entry);
		
		return entry;
	}
	
	public abstract T instantiateObject();
	
	public JSONObject instantiateJSON() {
		return new JSONObject();
	}
	
	protected abstract void fromJSON(JSONObject from, T to) throws JSONException;
	protected abstract void toJSON(T from, JSONObject to) throws JSONException;
}
