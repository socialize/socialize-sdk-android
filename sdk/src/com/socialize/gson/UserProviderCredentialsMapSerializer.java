package com.socialize.gson;

import com.socialize.auth.DefaultUserProviderCredentialsMap;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.google.gson.*;

import java.lang.reflect.Type;

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
