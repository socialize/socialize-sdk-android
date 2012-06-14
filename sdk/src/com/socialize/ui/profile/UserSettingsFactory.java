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
package com.socialize.ui.profile;

import org.json.JSONException;
import org.json.JSONObject;
import com.socialize.entity.JSONFactory;
import com.socialize.util.BitmapUtils;


/**
 * @author Jason Polites
 */
public class UserSettingsFactory extends JSONFactory<UserSettings> {
	
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String IMAGE_DATA = "picture";
	public static final String AUTO_POST_FACEBOOK = "auto_post_fb";
	public static final String AUTO_POST_TWITTER = "auto_post_tw";
	public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
	public static final String SHARE_LOCATION = "share_location";
	
	private BitmapUtils bitmapUtils;
	
	@Override
	public Object instantiateObject(JSONObject object) {
		return new UserSettings();
	}
	
	@Override
	protected void fromJSON(JSONObject object, UserSettings user) throws JSONException {
		user.setFirstName(getString(object,FIRST_NAME));
		user.setLastName(getString(object,LAST_NAME));
		user.setAutoPostFacebook(getBoolean(object,AUTO_POST_FACEBOOK, false));
		user.setAutoPostTwitter(getBoolean(object,AUTO_POST_TWITTER, false));
		user.setLocationEnabled(getBoolean(object,SHARE_LOCATION, true));
		user.setNotificationsEnabled(getBoolean(object,NOTIFICATIONS_ENABLED, true));
	}

	@Override
	protected void toJSON(UserSettings user, JSONObject object) throws JSONException {
		
		String encoded = null;
		
		if(user.getImage() != null) {
			encoded = bitmapUtils.encode(user.getImage());
		}
		
		object.put(FIRST_NAME, user.getFirstName());
		object.put(LAST_NAME, user.getLastName());
		
		if(encoded != null) {
			object.put(IMAGE_DATA, encoded);
		}
		
		object.put(AUTO_POST_FACEBOOK, user.isAutoPostFacebook());
		object.put(AUTO_POST_TWITTER, user.isAutoPostTwitter());
		object.put(SHARE_LOCATION, user.isLocationEnabled());
		object.put(NOTIFICATIONS_ENABLED, user.isNotificationsEnabled());
	}

	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.bitmapUtils = bitmapUtils;
	}
}
