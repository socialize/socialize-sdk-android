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

import com.socialize.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * @author Jason Polites
 */
public class UserFactory extends SocializeObjectFactory<User> {
	
	private StatsFactory statsFactory;
	
	private UserAuthDataFactory userAuthDataFactory;
	
	public static final String META = "meta";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String USERNAME = "username";
	public static final String DESCRIPTION = "description";
	public static final String LOCATION = "location";
	public static final String IMAGE_DATA = "picture";
	public static final String SMALL_IMAGE_URI = "small_image_uri";
	public static final String MEDIUM_IMAGE_URI = "medium_image_uri";
	public static final String LARGE_IMAGE_URI = "large_image_uri";
	public static final String STATS = "stats";
	public static final String THIRD_PARTY_AUTH = "third_party_auth";
	
	public static final String AUTO_POST_FACEBOOK = "auto_post_fb";
	public static final String AUTO_POST_TWITTER = "auto_post_tw";
	
	public static final String AUTO_POST_LIKES_FACEBOOK = "auto_post_likes_fb";
	
	public static final String AUTO_POST_COMMENTS_FACEBOOK = "auto_post_comments_fb";
	
	public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
	
	public static final String SHARE_LOCATION = "share_location";
	
	@Override
	public Object instantiateObject(JSONObject object) {
		return new User();
	}
	
	@Override
	protected void postFromJSON(JSONObject object, User user) throws JSONException {
		
		user.setMetaData(getString(object,META));
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
			JSONArray authJson = object.getJSONArray(THIRD_PARTY_AUTH);
			if(authJson != null && authJson.length() > 0 && userAuthDataFactory != null) {
				int length = authJson.length();
				for (int i = 0; i < length; i++) {
					UserAuthData authData = userAuthDataFactory.fromJSON(authJson.getJSONObject(i));
					user.addUserAuthData(authData);
				}
			}
		}
	}

	@Override
	protected void postToJSON(User user, JSONObject object) throws JSONException {
		
		String metaData = user.getMetaData();
		
		if(!StringUtils.isEmpty(metaData)) {
			object.put(META, metaData);
		}
		else {
			object.put(META, "");
		}
		
		object.put(FIRST_NAME, user.getFirstName());
		object.put(LAST_NAME, user.getLastName());
		object.put(DESCRIPTION, user.getDescription());
		object.put(LOCATION, user.getLocation());
		object.put(IMAGE_DATA, user.getProfilePicData());
		object.put(SMALL_IMAGE_URI, user.getSmallImageUri());
		object.put(MEDIUM_IMAGE_URI, user.getMediumImageUri());
		object.put(LARGE_IMAGE_URI, user.getLargeImageUri());

		List<UserAuthData> authData = user.getAuthData();
		
		if(authData != null && authData.size() > 0) {
			object.put(THIRD_PARTY_AUTH, userAuthDataFactory.toJSON(authData));
		}
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
