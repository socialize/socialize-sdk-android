package com.socialize.gson;

import java.lang.reflect.Type;

import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.google.gson.InstanceCreator;
import com.socialize.google.gson.JsonDeserializationContext;
import com.socialize.google.gson.JsonDeserializer;
import com.socialize.google.gson.JsonElement;
import com.socialize.google.gson.JsonParseException;
import com.socialize.google.gson.JsonSerializationContext;
import com.socialize.google.gson.JsonSerializer;

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
