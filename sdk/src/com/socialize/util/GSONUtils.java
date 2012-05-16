/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.util;

import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.google.gson.Gson;
import com.socialize.google.gson.GsonBuilder;
import com.socialize.gson.AuthProviderInfoSerializer;
import com.socialize.gson.AuthProviderTypeSerializer;
import com.socialize.gson.UserProviderCredentialsMapSerializer;
import com.socialize.gson.UserProviderCredentialsSerializer;

/**
 * @author Jason Polites
 *
 */
public class GSONUtils implements JSONUtils {
	
	private Gson gson;
	
	public void init() {
		 gson = newGson();
	}
	
	protected Gson newGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(UserProviderCredentials.class, new UserProviderCredentialsSerializer());
		builder.registerTypeAdapter(UserProviderCredentialsMap.class, new UserProviderCredentialsMapSerializer());
		builder.registerTypeAdapter(AuthProviderType.class, new AuthProviderTypeSerializer() );
		builder.registerTypeAdapter(AuthProviderInfo.class, new AuthProviderInfoSerializer() );
		builder.enableComplexMapKeySerialization();
		return builder.create();
	}

	/* (non-Javadoc)
	 * @see com.socialize.util.JSONUtils#toJSON(java.lang.Object)
	 */
	@Override
	public synchronized String toJSON(Object object) {
		return gson.toJson(object);
	}

	/* (non-Javadoc)
	 * @see com.socialize.util.JSONUtils#fromJSON(java.lang.String)
	 */
	@Override
	public synchronized <T> T fromJSON(String json, Class<T> cls) {
		return gson.fromJson(json, cls);
	}
}
