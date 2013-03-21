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
package com.socialize.test.unit.api;

import android.content.Context;
import android.test.mock.MockContext;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionPersister;
import com.socialize.api.action.user.SocializeUserSystem;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.user.UserListener;
import com.socialize.listener.user.UserSaveListener;
import com.socialize.notifications.NotificationRegistrationSystem;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.profile.UserSettings;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, UserListener.class, SocializeProvider.class})
public class UserApiTest extends SocializeUnitTest {

	SocializeProvider<User> provider;
	SocializeSession session;
	UserListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(UserListener.class);
	}

	public void testGetUser() {
		
		final int userId = 69;
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
				addResult(id);
			}
		};
		
		api.getUser(session, userId, listener);
		
		String gotten = getNextResult();
		
		assertNotNull(gotten);
		assertEquals(userId, Integer.parseInt(gotten));
	}
	
	/**
	 * Tests save user profile but does NOT test that listener methods are correct.
	 */
	@UsesMocks ({User.class})
	public void testSaveUserProfile() {
		
		final long id = 69;
		
		Context context = new MockContext();
		String firstName = "foo";
		String lastName = "bar";
//		String encodedImage = "foobar_encoded";
		
		User user = AndroidMock.createMock(User.class);
		
		AndroidMock.expect(session.getUser()).andReturn(user);
		AndroidMock.expect(user.getId()).andReturn(id);
//		AndroidMock.expect(user.isNotificationsEnabled()).andReturn(true);
//		AndroidMock.expect(user.isShareLocation()).andReturn(false);
//		
		user.setFirstName(firstName);
		user.setLastName(lastName);
//		user.setProfilePicData(encodedImage);
//		user.setAutoPostToFacebook(true);
//		user.setAutoPostToTwitter(true);
//		user.setNotificationsEnabled(false);
//		user.setShareLocation(true);
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void putAsPostAsync(SocializeSession session, String endpoint, User object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		AndroidMock.replay(session);
		AndroidMock.replay(user);
		
		UserSettings profile = new UserSettings();
		profile.setFirstName(firstName);
		profile.setLastName(lastName);
//		profile.setEncodedImage(encodedImage);
		profile.setAutoPostFacebook(true);
		profile.setAutoPostTwitter(true);
		profile.setNotificationsEnabled(false);
		profile.setLocationEnabled(true);
		
		api.saveUserSettings(context, session, profile, listener);
		
		AndroidMock.verify(session);
		AndroidMock.verify(user);
		
		User gotten = getNextResult();
		assertSame(user, gotten);
	}
	
	/**
	 * Tests that the listener created in saveUserSettings behaves correctly.
	 */
	@UsesMocks ({SocializeException.class, SocializeSessionPersister.class, NotificationRegistrationSystem.class, User.class, UserSettings.class})
	public void testSaveUserProfileListener() {
		
		final long id = 69;
		
		Context context = new MockContext();
		String firstName = "foo";
		String lastName = "bar";
		
		UserSettings profile = new UserSettings();
		profile.setFirstName(firstName);
		profile.setLastName(lastName);
		profile.setAutoPostFacebook(true);
		profile.setAutoPostTwitter(true);
		profile.setNotificationsEnabled(true);
		profile.setLocationEnabled(true);
		
		User user = AndroidMock.createMock(User.class);
		UserSettings userSettings = AndroidMock.createMock(UserSettings.class);
		SocializeSessionPersister sessionPersister = AndroidMock.createMock(SocializeSessionPersister.class);
		SocializeException exception = AndroidMock.createMock(SocializeException.class);
		NotificationRegistrationSystem notificationRegistrationSystem = AndroidMock.createMock(NotificationRegistrationSystem.class);
		
		AndroidMock.expect(session.getUser()).andReturn(user).times(2);
		AndroidMock.expect(session.getUserSettings()).andReturn(userSettings).times(2);
		AndroidMock.expect(user.getId()).andReturn(id);
		AndroidMock.expect(userSettings.isNotificationsEnabled()).andReturn(false);
		
		notificationRegistrationSystem.registerC2DMAsync(context);
		
		user.update(user);
		userSettings.update(profile);
		
		listener.onUpdate(user);
		listener.onError(exception);
		
		sessionPersister.saveUser(context, user, profile);
		
		user.setFirstName(firstName);
		user.setLastName(lastName);
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void putAsPostAsync(SocializeSession session, String endpoint, User object, SocializeActionListener listener) {
				addResult(listener);
			}
		};
		
		api.setSessionPersister(sessionPersister);
		
		AndroidMock.replay(session, listener, sessionPersister, user, userSettings, notificationRegistrationSystem);

		api.setNotificationRegistrationSystem(notificationRegistrationSystem);
		api.saveUserSettings(context, session, profile, listener);
		
		// This will fail if it's the wrong type
		UserSaveListener listenerFound = getNextResult();
		
		assertNotNull(listenerFound);
		
		// Call each listener methods to check the behavior
		listenerFound.onUpdate(user);
		listenerFound.onError(exception);
		
		AndroidMock.verify(session, listener, sessionPersister, user, userSettings, notificationRegistrationSystem);
		
	}
}
