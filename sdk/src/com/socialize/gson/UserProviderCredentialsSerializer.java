package com.socialize.gson;

import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.google.gson.*;

import java.lang.reflect.Type;

public class UserProviderCredentialsSerializer implements JsonDeserializer<UserProviderCredentials>, JsonSerializer<UserProviderCredentials>, InstanceCreator<UserProviderCredentials> {
	@Override
	public UserProviderCredentials deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		return context.deserialize(jsonElement, DefaultUserProviderCredentials.class);
	}

	@Override
	public JsonElement serialize(UserProviderCredentials src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src, DefaultUserProviderCredentials.class);
	}

	@Override
	public UserProviderCredentials createInstance(Type type) {
		return new DefaultUserProviderCredentials();
	}
}
