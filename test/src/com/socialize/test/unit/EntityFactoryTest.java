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
import com.socialize.entity.AbstractEntityFactory;
import com.socialize.entity.EntityStatsImpl;
import com.socialize.sample.mocks.MockEntity;

/**
 * @author Jason Polites
 */
@UsesMocks({EntityStatsImpl.class})
public class EntityFactoryTest extends AbstractSocializeObjectFactoryTest<MockEntity, AbstractEntityFactory<MockEntity>> {

	final String mockName = "test name";
	final String mockKey = "test key";
	final Integer mockLikes = new Integer(1);
	final Integer mockShares = new Integer(2);
	final Integer mockComments = new Integer(3);
	final Integer mockViews = new Integer(4);
	
	protected EntityStatsImpl stats;
	
	@Override
	protected void setupToJSONExpectations() throws JSONException {
		AndroidMock.expect(object.getName()).andReturn(mockName);
		AndroidMock.expect(object.getKey()).andReturn(mockKey);
		AndroidMock.expect(json.put("name", mockName)).andReturn(json);
		AndroidMock.expect(json.put("key", mockKey)).andReturn(json);
	}

	@Override
	protected void doToJSONVerify() {}

	
	@Override
	protected void setupFromJSONExpectations() throws Exception {
		
		stats = AndroidMock.createMock(EntityStatsImpl.class);

		AndroidMock.expect(json.has("name")).andReturn(true);
		AndroidMock.expect(json.has("key")).andReturn(true);
		AndroidMock.expect(json.has("likes")).andReturn(true);
		AndroidMock.expect(json.has("shares")).andReturn(true);
		AndroidMock.expect(json.has("comments")).andReturn(true);
		AndroidMock.expect(json.has("views")).andReturn(true);
		
		AndroidMock.expect(json.isNull("name")).andReturn(false);
		AndroidMock.expect(json.isNull("key")).andReturn(false);
		AndroidMock.expect(json.isNull("likes")).andReturn(false);
		AndroidMock.expect(json.isNull("shares")).andReturn(false);
		AndroidMock.expect(json.isNull("comments")).andReturn(false);
		AndroidMock.expect(json.isNull("views")).andReturn(false);
		
		AndroidMock.expect(json.getString("name")).andReturn(mockName);
		AndroidMock.expect(json.getString("key")).andReturn(mockKey);
		AndroidMock.expect(json.getInt("likes")).andReturn(mockLikes);
		AndroidMock.expect(json.getInt("shares")).andReturn(mockShares);
		AndroidMock.expect(json.getInt("views")).andReturn(mockViews);
		AndroidMock.expect(json.getInt("comments")).andReturn(mockComments);
		
		object.setName(mockName);
		object.setKey(mockKey);
		
		stats.setLikes(mockLikes);
		stats.setShares(mockShares);
		stats.setViews(mockViews);
		stats.setComments(mockComments);
	}

	@Override
	protected void doFromJSONVerify() {}

	@UsesMocks(MockEntity.class)
	@Override
	protected Class<MockEntity> getObjectClass() {
		return MockEntity.class;
	}

	@Override
	protected AbstractEntityFactory<MockEntity> createFactory() {
		return new AbstractEntityFactory<MockEntity>() {
			@Override
			public MockEntity instantiateObject(JSONObject json) {
				return object;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}

			@Override
			protected EntityStatsImpl newEntityStatsImpl() {
				return stats;
			}
		};
	}

}
