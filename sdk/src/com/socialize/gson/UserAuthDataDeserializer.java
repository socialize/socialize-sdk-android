package com.socialize.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.socialize.auth.DefaultUserAuthData;
import com.socialize.auth.UserAuthData;

public class UserAuthDataDeserializer implements JsonDeserializer<UserAuthData> ,  JsonSerializer<UserAuthData> {
	@Override
	public UserAuthData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		return context.deserialize(jsonElement, DefaultUserAuthData.class);
	}

	@Override
	public JsonElement serialize(UserAuthData src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src, DefaultUserAuthData.class);
	}
}
