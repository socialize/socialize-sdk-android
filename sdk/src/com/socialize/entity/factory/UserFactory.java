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

/**
 * @author Jason Polites
 *
 */
public class UserFactory extends SocializeObjectFactory<User> {
	
	private StatsFactory statsFactory;
//	private ImageUtils imageUtils;

	@Override
	public User instantiateObject() {
		return new User();
	}

	@Override
	protected void fromJSON(JSONObject object, User user) throws JSONException {
		
		if(object.has("first_name")) user.setFirstName(object.getString("first_name"));
		if(object.has("last_name"))user.setLastName(object.getString("last_name"));
		if(object.has("username"))user.setUsername(object.getString("username"));
		if(object.has("description"))user.setDescription(object.getString("description"));
		if(object.has("location"))user.setLocation(object.getString("location"));
		if(object.has("small_image_uri"))user.setSmallImageUri(object.getString("small_image_uri"));
		if(object.has("medium_image_uri"))user.setMediumImageUri(object.getString("medium_image_uri"));
		if(object.has("large_image_uri"))user.setLargeImageUri(object.getString("large_image_uri"));
		
		if(object.has("stats")) {
			JSONObject statsJson = object.getJSONObject("stats");
			
			if(statsJson != null) {
				Stats stats = statsFactory.fromJSON(statsJson);
				user.setStats(stats);
			}
		}
	}

	@Override
	protected void toJSON(User user, JSONObject object) throws JSONException {
		object.put("first_name", user.getFirstName());
		object.put("last_name", user.getLastName());
		object.put("description", user.getDescription());
		object.put("location", user.getLocation());
		
//		if(user.getImage() != null) {
//			object.put("picture", imageUtils.encode(user.getImage()));
//		}
	}

	public StatsFactory getStatsFactory() {
		return statsFactory;
	}

	public void setStatsFactory(StatsFactory statsFactory) {
		this.statsFactory = statsFactory;
	}

//	public ImageUtils getImageUtils() {
//		return imageUtils;
//	}
//
//	public void setImageUtils(ImageUtils imageUtils) {
//		this.imageUtils = imageUtils;
//	}
	
}
