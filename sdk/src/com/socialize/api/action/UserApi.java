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
package com.socialize.api.action;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.User;
import com.socialize.listener.user.UserListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class UserApi extends SocializeApi<User, SocializeProvider<User>> {

	public static final String ENDPOINT = "/user/";
	
	public UserApi(SocializeProvider<User> provider) {
		super(provider);
	}
	
	public void getUser(SocializeSession session, int id, UserListener listener) {
		getAsync(session, ENDPOINT, String.valueOf(id), listener);
	}
	
	/**
	 * Saves the CURRENT user details.
	 * @param session
	 * @param firstName
	 * @param lastName
	 * @param encodedImage Base64 encoded PNG image data.
	 * @param listener
	 */
	public void saveUserProfile(SocializeSession session, String firstName, String lastName, String encodedImage, UserListener listener) {
		User user = session.getUser();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setProfilePicData(encodedImage);
		
		String endpoint = ENDPOINT + user.getId() + "/";
		
		postAsync(session, endpoint, user, listener);
	}
}
