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
import junit.framework.Assert;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 */
public class UserApiTest extends SocializeUnitTest {

	SocializeProvider<User> provider;
	SocializeSession session;
	UserListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
        provider = Mockito.mock(SocializeProvider.class);
		session = Mockito.mock(SocializeSession.class);
		listener = Mockito.mock(UserListener.class);
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
	 * Tests that the listener created in saveUserSettings behaves correctly.
	 */
	public void testSaveUserProfileListener() {
		
		final long id = 69;
		
		Context context = new MockContext();
		String firstName = "foo";
		String lastName = "bar";
		
		UserSettings settingsToBeSaved = new UserSettings();
		settingsToBeSaved.setFirstName(firstName);
		settingsToBeSaved.setLastName(lastName);
		settingsToBeSaved.setAutoPostFacebook(true);
		settingsToBeSaved.setAutoPostTwitter(true);
		settingsToBeSaved.setNotificationsEnabled(true);
		settingsToBeSaved.setLocationEnabled(true);

		UserSettings sessionSettings = new UserSettings();
		sessionSettings.setNotificationsEnabled(false);


		User sessionUser = new User();
		sessionUser.setId(id);

		////////////////////////////////////////// Setup Mocks

		SocializeSessionPersister sessionPersister = Mockito.mock(SocializeSessionPersister.class);
		SocializeException exception = Mockito.mock(SocializeException.class);
		NotificationRegistrationSystem notificationRegistrationSystem = Mockito.mock(NotificationRegistrationSystem.class);
		
		Mockito.when(session.getUser()).thenReturn(sessionUser);
		Mockito.when(session.getUserSettings()).thenReturn(sessionSettings);

		notificationRegistrationSystem.registerC2DMAsync(context);

		listener.onUpdate(sessionUser);
		listener.onError(exception);
		
		sessionPersister.saveUser(context, sessionUser, sessionSettings);

		///////////////////////////////// End Setup Mocks
		
		SocializeUserSystem api = new SocializeUserSystem(provider) {
			@Override
			public void putAsPostAsync(SocializeSession session, String endpoint, User object, SocializeActionListener listener) {
				addResult(listener);
			}
		};
		
		api.setSessionPersister(sessionPersister);
		
		api.setNotificationRegistrationSystem(notificationRegistrationSystem);
		api.saveUserSettings(context, session, settingsToBeSaved, listener);
		
		// This will fail if it's the wrong type
		UserSaveListener listenerFound = getNextResult();
		
		assertNotNull(listenerFound);
		
		// Call each listener methods to check the behavior
		listenerFound.onUpdate(sessionUser);
		listenerFound.onError(exception);
		
        Assert.assertEquals(firstName, sessionSettings.getFirstName());
		Assert.assertEquals(lastName, sessionSettings.getLastName());
		Assert.assertEquals(true, sessionSettings.isAutoPostFacebook());
		Assert.assertEquals(true, sessionSettings.isAutoPostTwitter());
		Assert.assertEquals(true, sessionSettings.isNotificationsEnabled());
		Assert.assertEquals(true, sessionSettings.isLocationEnabled());
		Assert.assertEquals(firstName, sessionUser.getFirstName());
		Assert.assertEquals(lastName, sessionUser.getLastName());
	}
}
