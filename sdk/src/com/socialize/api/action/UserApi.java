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

import android.content.Context;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionPersister;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class UserApi extends SocializeApi<User, SocializeProvider<User>> {

	public static final String ENDPOINT = "/user/";
	
	private SocializeSessionPersister sessionPersister;
	
	public UserApi(SocializeProvider<User> provider) {
		super(provider);
	}
	
	public void getUser(SocializeSession session, long id, UserListener listener) {
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
	public void saveUserProfile(final Context context, final SocializeSession session, String firstName, String lastName, String encodedImage, final UserListener listener) {
		User user = session.getUser();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setProfilePicData(encodedImage);
		
		String endpoint = ENDPOINT + user.getId() + "/";
		
		putAsPostAsync(session, endpoint, user, new UserSaveListener() {

			@Override
			public void onError(SocializeException error) {
				listener.onError(error);
			}

			@Override
			public void onUpdate(User user) {
				// Update local in-memory user
				session.getUser().merge(user);
				
				// Save this user to the local session for next load
				if(sessionPersister != null) {
					sessionPersister.saveUser(context, user);
				}
				
				listener.onUpdate(user);
			}
		});
	}

	public void setSessionPersister(SocializeSessionPersister sessionPersister) {
		this.sessionPersister = sessionPersister;
	}
}
