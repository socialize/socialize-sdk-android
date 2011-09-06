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
package com.socialize.entity.factory;

import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.Stats;
import com.socialize.entity.User;
import com.socialize.entity.UserAuthData;

/**
 * @author Jason Polites
 *
 */
public class UserFactory extends SocializeObjectFactory<User> {
	
	private StatsFactory statsFactory;
	private UserAuthDataFactory userAuthDataFactory;
	
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String USERNAME = "username";
	public static final String DESCRIPTION = "description";
	public static final String LOCATION = "location";
	public static final String IMAGE_DATA = "image_data";
	public static final String SMALL_IMAGE_URI = "small_image_uri";
	public static final String MEDIUM_IMAGE_URI = "medium_image_uri";
	public static final String LARGE_IMAGE_URI = "large_image_uri";
	public static final String STATS = "stats";
	public static final String THIRD_PARTY_AUTH = "third_party_auth";

	@Override
	public User instantiateObject() {
		return new User();
	}

	@Override
	protected void postFromJSON(JSONObject object, User user) throws JSONException {
		
		user.setFirstName(getString(object,FIRST_NAME));
		user.setLastName(getString(object,LAST_NAME));
		user.setUsername(getString(object,USERNAME));
		user.setDescription(getString(object,DESCRIPTION));
		user.setLocation(getString(object,LOCATION));
		user.setSmallImageUri(getString(object,SMALL_IMAGE_URI));
		user.setMediumImageUri(getString(object,MEDIUM_IMAGE_URI));
		user.setLargeImageUri(getString(object,LARGE_IMAGE_URI));
		user.setProfilePicData(getString(object,IMAGE_DATA));
		
		if(object.has(STATS) && !object.isNull(STATS)) {
			JSONObject statsJson = object.getJSONObject(STATS);
			
			if(statsJson != null && statsFactory != null) {
				Stats stats = statsFactory.fromJSON(statsJson);
				user.setStats(stats);
			}
		}
		
		if(object.has(THIRD_PARTY_AUTH) && !object.isNull(THIRD_PARTY_AUTH)) {
			JSONObject authJson = object.getJSONObject(THIRD_PARTY_AUTH);
			
			if(authJson != null && userAuthDataFactory != null) {
				UserAuthData authData = userAuthDataFactory.fromJSON(authJson);
				user.setAuthData(authData);
			}
		}
	}

	@Override
	protected void postToJSON(User user, JSONObject object) throws JSONException {
		object.put(FIRST_NAME, user.getFirstName());
		object.put(LAST_NAME, user.getLastName());
		object.put(DESCRIPTION, user.getDescription());
		object.put(LOCATION, user.getLocation());
		object.put(IMAGE_DATA, user.getProfilePicData());
	}

	public StatsFactory getStatsFactory() {
		return statsFactory;
	}

	public UserAuthDataFactory getUserAuthDataFactory() {
		return userAuthDataFactory;
	}

	public void setStatsFactory(StatsFactory statsFactory) {
		this.statsFactory = statsFactory;
	}

	public void setUserAuthDataFactory(UserAuthDataFactory userAuthDataFactory) {
		this.userAuthDataFactory = userAuthDataFactory;
	}
}
