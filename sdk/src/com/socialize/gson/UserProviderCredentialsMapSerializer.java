package com.socialize.gson;

import java.lang.reflect.Type;

import com.socialize.auth.DefaultUserProviderCredentialsMap;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.google.gson.InstanceCreator;
import com.socialize.google.gson.JsonDeserializationContext;
import com.socialize.google.gson.JsonDeserializer;
import com.socialize.google.gson.JsonElement;
import com.socialize.google.gson.JsonParseException;
import com.socialize.google.gson.JsonSerializationContext;
import com.socialize.google.gson.JsonSerializer;

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
