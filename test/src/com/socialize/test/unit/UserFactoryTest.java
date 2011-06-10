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
import com.socialize.entity.Stats;
import com.socialize.entity.User;
import com.socialize.entity.factory.StatsFactory;
import com.socialize.entity.factory.UserFactory;

/**
 * @author Jason Polites
 *
 */
public class UserFactoryTest extends AbstractSocializeObjectFactoryTest<User, UserFactory> {

	final String first_name = "mock_first_name";
	final String last_name = "mock_last_name";
	final String description = "mock_description";
	final String location = "mock_location";
	final String username = "mock_username";
	final String small_image_uri = "mock_small_image_uri";
	final String medium_image_uri = "mock_medium_image_uri";
	final String large_image_uri = "mock_large_image_uri";
	
	@Override
	protected void setupToJSONExpectations() throws JSONException {
		AndroidMock.expect(object.getFirstName()).andReturn(first_name);
		AndroidMock.expect(object.getLastName()).andReturn(last_name);
		AndroidMock.expect(object.getDescription()).andReturn(description);
		AndroidMock.expect(object.getLocation()).andReturn(location);
//		AndroidMock.expect(object.getImage()).andReturn(null);
		
//		if(user.getImage() != null) {
//			object.put("picture", imageUtils.encode(user.getImage()));
//		}
		
		AndroidMock.expect(json.put("first_name", first_name)).andReturn(json);
		AndroidMock.expect(json.put("last_name", last_name)).andReturn(json);
		AndroidMock.expect(json.put("description", description)).andReturn(json);
		AndroidMock.expect(json.put("location", location)).andReturn(json);
		
	}

	@Override
	protected void doToJSONVerify() {}

	@UsesMocks({Stats.class})
	@Override
	protected void setupFromJSONExpectations() throws Exception {
		
		Stats stats = AndroidMock.createMock(Stats.class);
		
		AndroidMock.expect(json.getString("first_name")).andReturn(first_name);
		AndroidMock.expect(json.getString("last_name")).andReturn(last_name);
		AndroidMock.expect(json.getString("username")).andReturn(username);
		AndroidMock.expect(json.getString("description")).andReturn(description);
		AndroidMock.expect(json.getString("location")).andReturn(location);
		AndroidMock.expect(json.getString("small_image_uri")).andReturn(small_image_uri);
		AndroidMock.expect(json.getString("medium_image_uri")).andReturn(medium_image_uri);
		AndroidMock.expect(json.getString("large_image_uri")).andReturn(large_image_uri);
		AndroidMock.expect(json.getJSONObject("stats")).andReturn(json);
		AndroidMock.expect(factory.getStatsFactory().fromJSON(json)).andReturn(stats);
	
		object.setFirstName(first_name);
		object.setLastName(last_name);
		object.setUsername(username);
		object.setDescription(description);
		object.setLocation(location);
		object.setSmallImageUri(small_image_uri);
		object.setMediumImageUri(medium_image_uri);
		object.setLargeImageUri(large_image_uri);
		object.setStats(stats);
		
		AndroidMock.replay(factory.getStatsFactory());
	}

	@Override
	protected void doFromJSONVerify() {
		AndroidMock.verify(factory.getStatsFactory());
	}

	@Override
	protected Class<User> getObjectClass() {
		return User.class;
	}

	@UsesMocks({StatsFactory.class})
	@Override
	protected UserFactory createFactory() {
		
		final StatsFactory statsFactory = AndroidMock.createMock(StatsFactory.class);
		
		UserFactory factory = new UserFactory() {
			@Override
			public User instantiateObject() {
				return object;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}
		};
		
		factory.setStatsFactory(statsFactory);
		
		return factory;
	}

}
