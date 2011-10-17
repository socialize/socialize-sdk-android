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
import com.socialize.entity.Application;
import com.socialize.entity.factory.ApplicationFactory;

/**
 * @author Jason Polites
 *
 */

public class ApplicationFactoryTest extends AbstractSocializeObjectFactoryTest<Application, ApplicationFactory> {

	final String mockName = "test name";
	
	@Override
	protected void setupToJSONExpectations() throws JSONException {
		AndroidMock.expect(object.getName()).andReturn(mockName);
		AndroidMock.expect(json.put("name", mockName)).andReturn(json);
	}

	@Override
	protected void doToJSONVerify() {}

	@Override
	protected void setupFromJSONExpectations() throws Exception {
		AndroidMock.expect(json.has("name")).andReturn(true);
		AndroidMock.expect(json.getString("name")).andReturn(mockName);
		object.setName(mockName);
	}

	@Override
	protected void doFromJSONVerify() {}

	@UsesMocks(Application.class)
	@Override
	protected Class<Application> getObjectClass() {
		return Application.class;
	}

	@Override
	protected ApplicationFactory createFactory() {
		return new ApplicationFactory() {
			@Override
			public Application instantiateObject(JSONObject json) {
				return object;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}
		};
	}

}
