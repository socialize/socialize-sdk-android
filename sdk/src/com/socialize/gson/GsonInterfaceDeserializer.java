package com.socialize.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonInterfaceDeserializer implements JsonDeserializer<Object>, JsonSerializer<Object> {

	public static final String CLASS_META_KEY = "gson-class";

	@Override
	public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		JsonObject jsonObj = jsonElement.getAsJsonObject();
		String className = jsonObj.get(CLASS_META_KEY).getAsString();
		try {
			Class<?> clz = Class.forName(className);
			return jsonDeserializationContext.deserialize(jsonElement, clz);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}
	

	@Override
	public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonEle = jsonSerializationContext.serialize(object, type);
		jsonEle.getAsJsonObject().addProperty(GsonInterfaceDeserializer.CLASS_META_KEY, object.getClass().getCanonicalName());
		return jsonEle;
	}
}
