/*
 * Copyright (c) 2012 Socialize Inc. 
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
package com.socialize.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * @author Jason Polites
 *
 */
public abstract class JSONFactory<T extends Object> {
	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ssZZ";
	
	protected final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
	
	public JSONFactory() {
		super();
	}
	
	public JSONArray toJSON(Collection<T> objects) throws JSONException {
		JSONArray array = instantiateJSONArray();
		for (T t : objects) {
			array.put(toJSON(t));
		}
		return array;
	}

	public JSONObject toJSON(T object) throws JSONException {
		JSONObject json = instantiateJSON();
		toJSON(object, json);
		return json;
	}

	@SuppressWarnings("unchecked")
	public T fromJSON(JSONObject json) throws JSONException {
		T object = (T) instantiateObject(json);
		if(object != null) {
			fromJSON(json, object);
		}
		return object;
	}
	
	public JSONObject instantiateJSON() {
		return new JSONObject();
	}
	
	public JSONArray instantiateJSONArray() {
		return new JSONArray();
	}
	
	protected boolean exists(JSONObject obj, String key) throws JSONException {
		return (obj.has(key) && !obj.isNull(key));
	}

	protected String getString(JSONObject obj, String key) throws JSONException {
		if(exists(obj, key)) {
			return obj.getString(key);
		}
		return null;
	}
	
	protected Double getDouble(JSONObject obj, String key) throws JSONException {
		if(exists(obj, key)) {
			return obj.getDouble(key);
		}
		return null;
	}
	
	protected boolean getBoolean(JSONObject obj, String key, boolean defaultValue) throws JSONException {
		if(exists(obj, key)) {
			return obj.getBoolean(key);
		}
		return defaultValue;
	}
	
	protected JSONObject getJSONObject(JSONObject obj, String key) throws JSONException {
		if(exists(obj, key)) {
			return obj.getJSONObject(key);
		}
		return null;
	}	
	
	protected int getInt(JSONObject obj, String key) throws JSONException {
		if(exists(obj, key)) {
			return obj.getInt(key);
		}
		return 0;
	}
	
	protected long getLong(JSONObject obj, String key) throws JSONException {
		return getLong(obj, key, 0);
	}	
	
	protected long getLong(JSONObject obj, String key, long defaultValue) throws JSONException {
		if(exists(obj, key)) {
			return obj.getLong(key);
		}
		return defaultValue;
	}	
	
	protected Long getLongObject(JSONObject obj, String key) throws JSONException {
		if(exists(obj, key)) {
			return Long.valueOf(obj.getLong(key));
		}
		return null;
	}	
	
	// This SHOULD return the generic type "T" but because javassist (via AndroidMock) doesn't seem to 
	// want to create methods with generic return types we can't do it :/
	public abstract Object instantiateObject(JSONObject object);
	
	protected abstract void fromJSON(JSONObject from, T to) throws JSONException;
	protected abstract void toJSON(T from, JSONObject to) throws JSONException;
}
