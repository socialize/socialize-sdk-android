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
package com.socialize.entity;

import com.socialize.auth.AuthProviderType;
import com.socialize.log.SocializeLogger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jason Polites
 */
public class UserAuthDataFactory extends SocializeObjectFactory<UserAuthData> {
	
	private static final String AUTH_ID = "auth_id";
	private static final String AUTH_TYPE = "auth_type";

	private SocializeLogger logger;
	
	@Override
	public Object instantiateObject(JSONObject object) {
		return new UserAuthData();
	}

	@Override
	protected void postFromJSON(JSONObject from, UserAuthData to) throws JSONException {

		if(from.has(AUTH_ID) && !from.isNull(AUTH_ID)) {
			to.setId(from.getLong(AUTH_ID));
		}
		
		if(from.has(AUTH_TYPE) && !from.isNull(AUTH_TYPE)) {
			String string = from.getString(AUTH_TYPE).trim().toUpperCase();
			try {
				AuthProviderType type = AuthProviderType.valueOf(string);
				to.setAuthProviderType(type);
			}
			catch (Exception e) {
				if(logger != null) {
					logger.error("Failed to parse auth type [" +
							string +
							"]", e);
				}
				else {
					SocializeLogger.e(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	protected void postToJSON(UserAuthData from, JSONObject to) throws JSONException {
		// Never sent
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	
	
}
