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
package com.socialize.test.unit;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Stats;
import com.socialize.entity.StatsFactory;
import com.socialize.entity.User;
import com.socialize.entity.UserAuthData;
import com.socialize.entity.UserAuthDataFactory;
import com.socialize.entity.UserFactory;

/**
 * @author Jason Polites
 * 
 */
public class UserFactoryTest extends AbstractSocializeObjectFactoryTest<User, UserFactory> {

	final String meta = "mock_meta";
	final String first_name = "mock_first_name";
	final String last_name = "mock_last_name";
	final String description = "mock_description";
	final String location = "mock_location";
	final String username = "mock_username";
	final String small_image_uri = "mock_small_image_uri";
	final String medium_image_uri = "mock_medium_image_uri";
	final String large_image_uri = "mock_large_image_uri";
	final String picture = "mock_image_data";
//	final String auto_post_facebook= "auto_post_fb";
//	final String auto_post_twitter = "auto_post_tw";
//	final String notifications_enabled = "notifications_enabled";
//	final String auto_post_fb = "auto_post_fb";
//	final String share_location = "share_location";
	final String third_party_auth = "third_party_auth";

//	final boolean is_auto_post_facebook = true;
//	final boolean is_auto_post_twitter = false;
//	final boolean is_share_location = true;
//	final boolean is_notifications_enabled = false;

	List<UserAuthData> authData;
	UserAuthDataFactory userAuthDataFactory;
	JSONArray array;

	@SuppressWarnings("unchecked")
	@UsesMocks({ JSONArray.class, List.class })
	@Override
	protected void setupToJSONExpectations() throws JSONException {
		array = AndroidMock.createMock(JSONArray.class);
		authData = AndroidMock.createNiceMock(List.class);

		AndroidMock.expect(authData.size()).andReturn(1);
		AndroidMock.expect(userAuthDataFactory.toJSON(authData)).andReturn(array);

		AndroidMock.expect(object.getFirstName()).andReturn(first_name);
		AndroidMock.expect(object.getLastName()).andReturn(last_name);
		AndroidMock.expect(object.getDescription()).andReturn(description);
		AndroidMock.expect(object.getLocation()).andReturn(location);
		AndroidMock.expect(object.getProfilePicData()).andReturn(picture);
		AndroidMock.expect(object.getAuthData()).andReturn(authData);
		AndroidMock.expect(object.getSmallImageUri()).andReturn(small_image_uri);
		AndroidMock.expect(object.getMediumImageUri()).andReturn(medium_image_uri);
		AndroidMock.expect(object.getLargeImageUri()).andReturn(large_image_uri);
//		AndroidMock.expect(object.isAutoPostToFacebook()).andReturn(is_auto_post_facebook);
//		AndroidMock.expect(object.isAutoPostToTwitter()).andReturn(is_auto_post_twitter);
		AndroidMock.expect(object.getMetaData()).andReturn(meta);
//		AndroidMock.expect(object.isShareLocation()).andReturn(is_share_location);
//		AndroidMock.expect(object.isNotificationsEnabled()).andReturn(is_notifications_enabled);

		AndroidMock.expect(json.put("first_name", first_name)).andReturn(json);
		AndroidMock.expect(json.put("last_name", last_name)).andReturn(json);
		AndroidMock.expect(json.put("description", description)).andReturn(json);
		AndroidMock.expect(json.put("location", location)).andReturn(json);
		AndroidMock.expect(json.put("picture", picture)).andReturn(json);
		AndroidMock.expect(json.put("meta", meta)).andReturn(json);
//		AndroidMock.expect(json.put(auto_post_facebook, is_auto_post_facebook)).andReturn(json);
//		AndroidMock.expect(json.put(auto_post_twitter, is_auto_post_twitter)).andReturn(json);
//		AndroidMock.expect(json.put(share_location, is_share_location)).andReturn(json);
//		AndroidMock.expect(json.put(notifications_enabled, is_notifications_enabled)).andReturn(json);
		AndroidMock.expect(json.put("small_image_uri", small_image_uri)).andReturn(json);
		AndroidMock.expect(json.put("medium_image_uri", medium_image_uri)).andReturn(json);
		AndroidMock.expect(json.put("large_image_uri", large_image_uri)).andReturn(json);
		AndroidMock.expect(json.put(third_party_auth, array)).andReturn(json);

		AndroidMock.replay(authData);
		AndroidMock.replay(userAuthDataFactory);
	}

	@Override
	protected void doToJSONVerify() {
		AndroidMock.verify(authData);
		AndroidMock.verify(userAuthDataFactory);
	}

	@UsesMocks({ Stats.class, UserAuthData.class })
	@Override
	protected void setupFromJSONExpectations() throws Exception {

		Stats stats = AndroidMock.createMock(Stats.class);
		UserAuthData authData = AndroidMock.createMock(UserAuthData.class);

		array = AndroidMock.createMock(JSONArray.class);

		AndroidMock.expect(json.has("picture")).andReturn(true);
		AndroidMock.expect(json.has("first_name")).andReturn(true);
		AndroidMock.expect(json.has("last_name")).andReturn(true);
		AndroidMock.expect(json.has("username")).andReturn(true);
		AndroidMock.expect(json.has("description")).andReturn(true);
		AndroidMock.expect(json.has("location")).andReturn(true);
		AndroidMock.expect(json.has("small_image_uri")).andReturn(true);
		AndroidMock.expect(json.has("medium_image_uri")).andReturn(true);
		AndroidMock.expect(json.has("large_image_uri")).andReturn(true);
		AndroidMock.expect(json.has("stats")).andReturn(true);
		AndroidMock.expect(json.has(third_party_auth)).andReturn(true);

		AndroidMock.expect(json.has("meta")).andReturn(true);
		
		// Legacy
//		AndroidMock.expect(json.has("auto_post_likes_fb")).andReturn(false);
//		AndroidMock.expect(json.has("auto_post_comments_fb")).andReturn(false);
//		AndroidMock.expect(json.has(auto_post_facebook)).andReturn(true);
//		AndroidMock.expect(json.has(auto_post_twitter)).andReturn(true);
//		AndroidMock.expect(json.has(notifications_enabled)).andReturn(true);
//		AndroidMock.expect(json.has("share_location")).andReturn(true);

		AndroidMock.expect(json.isNull("picture")).andReturn(false);
		AndroidMock.expect(json.isNull("first_name")).andReturn(false);
		AndroidMock.expect(json.isNull("last_name")).andReturn(false);
		AndroidMock.expect(json.isNull("username")).andReturn(false);
		AndroidMock.expect(json.isNull("description")).andReturn(false);
		AndroidMock.expect(json.isNull("location")).andReturn(false);
		AndroidMock.expect(json.isNull("small_image_uri")).andReturn(false);
		AndroidMock.expect(json.isNull("medium_image_uri")).andReturn(false);
		AndroidMock.expect(json.isNull("large_image_uri")).andReturn(false);
		AndroidMock.expect(json.isNull("stats")).andReturn(false);
		AndroidMock.expect(json.isNull(third_party_auth)).andReturn(false);

//		AndroidMock.expect(json.isNull(auto_post_facebook)).andReturn(false);
//		AndroidMock.expect(json.isNull(auto_post_twitter)).andReturn(false);
//		AndroidMock.expect(json.isNull(notifications_enabled)).andReturn(false);
//		AndroidMock.expect(json.isNull("share_location")).andReturn(false);

		AndroidMock.expect(json.isNull("meta")).andReturn(false);

		AndroidMock.expect(json.getString("picture")).andReturn(picture);
		AndroidMock.expect(json.getString("first_name")).andReturn(first_name);
		AndroidMock.expect(json.getString("last_name")).andReturn(last_name);
		AndroidMock.expect(json.getString("username")).andReturn(username);
		AndroidMock.expect(json.getString("description")).andReturn(description);
		AndroidMock.expect(json.getString("location")).andReturn(location);
		AndroidMock.expect(json.getString("small_image_uri")).andReturn(small_image_uri);
		AndroidMock.expect(json.getString("medium_image_uri")).andReturn(medium_image_uri);
		AndroidMock.expect(json.getString("large_image_uri")).andReturn(large_image_uri);
		AndroidMock.expect(json.getString("meta")).andReturn(meta);

		AndroidMock.expect(json.getJSONObject("stats")).andReturn(json);
		
//		AndroidMock.expect(json.getBoolean(auto_post_facebook)).andReturn(is_auto_post_facebook);
//		AndroidMock.expect(json.getBoolean(auto_post_twitter)).andReturn(is_auto_post_twitter);
//		AndroidMock.expect(json.getBoolean(notifications_enabled)).andReturn(is_notifications_enabled);
//		AndroidMock.expect(json.getBoolean("share_location")).andReturn(is_share_location);

		AndroidMock.expect(json.getJSONArray(third_party_auth)).andReturn(array);
		AndroidMock.expect(array.length()).andReturn(1).times(2);
		AndroidMock.expect(array.getJSONObject(0)).andReturn(json);

		AndroidMock.expect(factory.getUserAuthDataFactory().fromJSON(json)).andReturn(authData);
		AndroidMock.expect(factory.getStatsFactory().fromJSON(json)).andReturn(stats);

		object.setFirstName(first_name);
		object.setLastName(last_name);
		object.setUsername(username);
		object.setDescription(description);
		object.setLocation(location);
		object.setSmallImageUri(small_image_uri);
		object.setMediumImageUri(medium_image_uri);
		object.setLargeImageUri(large_image_uri);
		object.setProfilePicData(picture);

		object.setMetaData(meta);

//		object.setAutoPostToFacebook(is_auto_post_facebook);
//		object.setAutoPostToTwitter(is_auto_post_twitter);
//		object.setNotificationsEnabled(is_notifications_enabled);
//		object.setShareLocation(is_share_location);
		
		object.setStats(stats);
		object.addUserAuthData(authData);

		AndroidMock.replay(array);
		AndroidMock.replay(factory.getUserAuthDataFactory());
		AndroidMock.replay(factory.getStatsFactory());
	}

	@Override
	protected void doFromJSONVerify() {
		AndroidMock.verify(array);
		AndroidMock.verify(factory.getUserAuthDataFactory());
		AndroidMock.verify(factory.getStatsFactory());
	}

	@Override
	protected Class<User> getObjectClass() {
		return User.class;
	}

	@UsesMocks({ StatsFactory.class, UserAuthDataFactory.class })
	@Override
	protected UserFactory createFactory() {

		final StatsFactory statsFactory = AndroidMock.createMock(StatsFactory.class);
		userAuthDataFactory = AndroidMock.createMock(UserAuthDataFactory.class);

		UserFactory factory = new UserFactory() {
			@Override
			public User instantiateObject(JSONObject json) {
				return object;
			}

			@Override
			public JSONObject instantiateJSON() {
				return json;
			}
		};

		factory.setStatsFactory(statsFactory);
		factory.setUserAuthDataFactory(userAuthDataFactory);

		return factory;
	}

}
