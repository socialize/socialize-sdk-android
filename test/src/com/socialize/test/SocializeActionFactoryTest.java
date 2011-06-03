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
package com.socialize.test;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.factory.SocializeActionFactory;

public class SocializeActionFactoryTest extends AbstractSocializeActionFactoryTest<SocializeAction, SocializeActionFactory<SocializeAction>> {

	@Override
	protected SocializeActionFactory<SocializeAction> createFactory() {
		return new SocializeActionFactory<SocializeAction>() {
			
			@Override
			public SocializeAction instantiateObject() {
				return action;
			}
			
			@Override
			public JSONObject instantiateJSON() {
				return json;
			}

			@Override
			protected void postToJSON(SocializeAction from, JSONObject to) throws JSONException {}
			
			@Override
			protected void postFromJSON(JSONObject from, SocializeAction to) throws JSONException {}
		};
	}

	@UsesMocks(SocializeAction.class)
	@Override
	protected Class<SocializeAction> getActionClass() {
		return SocializeAction.class;
	}

	@Override
	protected void setupFromJSONExpectations() {
		AndroidMock.replay(json);
	}

	@Override
	protected void doFromJSONVerify() {
		AndroidMock.verify(factoryService);
		AndroidMock.verify(appFactoryMock);
		AndroidMock.verify(userFactoryMock);
		AndroidMock.verify(entityFactoryMock);
		AndroidMock.verify(json);
	}

	@Override
	protected void setupToJSONExpectations() {
		AndroidMock.replay(json);
		AndroidMock.replay(action);
	}

	@Override
	protected void doToJSONVerify() {
		AndroidMock.verify(factoryService);
		AndroidMock.verify(appFactoryMock);
		AndroidMock.verify(userFactoryMock);
		AndroidMock.verify(entityFactoryMock);
		AndroidMock.verify(json);
		AndroidMock.verify(action);
	}
}
