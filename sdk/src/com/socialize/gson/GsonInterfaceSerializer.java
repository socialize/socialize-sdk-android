package com.socialize.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonInterfaceSerializer implements JsonSerializer<Object> {

	@Override
	public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonElement jsonEle = jsonSerializationContext.serialize(object, type);
		jsonEle.getAsJsonObject().addProperty(GsonInterfaceDeserializer.CLASS_META_KEY, object.getClass().getCanonicalName());
		return jsonEle;
	}

}
