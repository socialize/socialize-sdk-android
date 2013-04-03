package com.socialize.gson;

import com.socialize.auth.AuthProviderInfo;
import com.socialize.google.gson.*;

import java.lang.reflect.Type;

public class AuthProviderInfoSerializer implements JsonDeserializer<AuthProviderInfo>, JsonSerializer<AuthProviderInfo> {
	final String CLASS_META_KEY = "gson-class";

	@Override
	public AuthProviderInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObj = jsonElement.getAsJsonObject();
		String className = jsonObj.get(CLASS_META_KEY).getAsString();
		try {
			Class<?> clz = Class.forName(className);
			return context.deserialize(jsonElement, clz);
		} 
		catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public JsonElement serialize(AuthProviderInfo object, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
		jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());
		return jsonEle;
	}
}
