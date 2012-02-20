/*
 * Copyright (c) 2011 Socialize Inc. 
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
package com.socialize.test.unit;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.UserAuthData;
import com.socialize.entity.UserAuthDataFactory;

/**
 * @author Jason Polites
 * 
 */
@UsesMocks(UserAuthData.class)
public class UserAuthDataFactoryTest extends AbstractSocializeObjectFactoryTest<UserAuthData, UserAuthDataFactory> {

	private Long id = 69L;
	private String type = "facebook";
	private String type_error = "no_such_provider";

	@Override
	protected void setupToJSONExpectations() throws JSONException {
		// nothing
	}

	@Override
	protected void doToJSONVerify() {
	}

	@Override
	protected void setupFromJSONExpectations() throws Exception {

		AndroidMock.expect(json.has("auth_id")).andReturn(true);
		AndroidMock.expect(json.has("auth_type")).andReturn(true);

		AndroidMock.expect(json.isNull("auth_id")).andReturn(false);
		AndroidMock.expect(json.isNull("auth_type")).andReturn(false);

		AndroidMock.expect(json.getLong("auth_id")).andReturn(id);
		AndroidMock.expect(json.getString("auth_type")).andReturn(type);

		object.setAuthProviderType(AuthProviderType.FACEBOOK);
		object.setId(id);
	}

	public void testFromJSONFail() throws JSONException {

		AndroidMock.expect(json.has("id")).andReturn(false);
		AndroidMock.expect(json.has("auth_id")).andReturn(true);
		AndroidMock.expect(json.has("auth_type")).andReturn(true);

		AndroidMock.expect(json.isNull("auth_id")).andReturn(false);
		AndroidMock.expect(json.isNull("auth_type")).andReturn(false);

		AndroidMock.expect(json.getLong("auth_id")).andReturn(id);
		AndroidMock.expect(json.getString("auth_type")).andReturn(type_error);

		AndroidMock.replay(json);

		object = factory.fromJSON(json);

		AndroidMock.verify(json);

		assertNull(object.getAuthProviderType());
	}

	@Override
	protected void doFromJSONVerify() {

	}

	@Override
	protected Class<UserAuthData> getObjectClass() {
		return UserAuthData.class;
	}

	@Override
	protected UserAuthDataFactory createFactory() {

		UserAuthDataFactory factory = new UserAuthDataFactory() {
			@Override
			public UserAuthData instantiateObject(JSONObject json) {
				return object;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}
		};

		return factory;
	}

}
