package com.socialize.gson;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;

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
