package com.socialize.gson;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.socialize.auth.DefaultUserProviderCredentialsMap;
import com.socialize.auth.UserProviderCredentialsMap;

public class UserProviderCredentialsMapSerializer implements JsonDeserializer<UserProviderCredentialsMap>, JsonSerializer<UserProviderCredentialsMap>, InstanceCreator<UserProviderCredentialsMap> {
	@Override
	public UserProviderCredentialsMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		return context.deserialize(jsonElement, DefaultUserProviderCredentialsMap.class);
	}

	@Override
	public JsonElement serialize(UserProviderCredentialsMap src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src, DefaultUserProviderCredentialsMap.class);
	}

	@Override
	public UserProviderCredentialsMap createInstance(Type type) {
		return new DefaultUserProviderCredentialsMap();
	}
}
