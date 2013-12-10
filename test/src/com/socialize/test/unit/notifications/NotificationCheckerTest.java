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
package com.socialize.test.unit.notifications;

import android.app.Activity;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.User;
import com.socialize.notifications.NotificationChecker;
import com.socialize.notifications.NotificationRegistrationState;
import com.socialize.notifications.NotificationRegistrationSystem;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.util.AppUtils;
import org.mockito.Mockito;


/**
 * @author Jason Polites
 *
 */
public class NotificationCheckerTest extends SocializeActivityTest {

	public void testCheckRegistrationsNotRegisteredForC2DM() {
		
		final Activity context = TestUtils.getActivity(this);
		
        final AppUtils appUtils = Mockito.mock(AppUtils.class);
		final NotificationRegistrationSystem notificationRegistrationSystem = Mockito.mock(NotificationRegistrationSystem.class);
		final NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		final SocializeConfig config = Mockito.mock(SocializeConfig.class);
		final SocializeSession session = Mockito.mock(SocializeSession.class);
		final User user = Mockito.mock(User.class);
		
		// Base mocks
		Mockito.when(config.getBooleanProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, true)).thenReturn(true);
		Mockito.when(config.getBooleanProperty(SocializeConfig.GCM_REGISTRATION_ENABLED, true)).thenReturn(true);
		Mockito.when(session.getUser()).thenReturn(user);
		Mockito.when(appUtils.isNotificationsAvailable(context)).thenReturn(true);
		
		NotificationChecker checker = new NotificationChecker();
		
		checker.setAppUtils(appUtils);
		checker.setNotificationRegistrationState(notificationRegistrationState);
		checker.setNotificationRegistrationSystem(notificationRegistrationSystem);
		checker.setConfig(config);
		
		// Orchestrate a state where we are not yet registered for C2DM
		Mockito.when(notificationRegistrationSystem.isRegisteredC2DM(context)).thenReturn(false);
		Mockito.when(notificationRegistrationSystem.isRegistrationPending()).thenReturn(false);
		Mockito.when(notificationRegistrationSystem.isRegisteredSocialize(context, user)).thenReturn(false);
		
		notificationRegistrationState.reload(context);
		
		// Expect this once
        Mockito.verify(notificationRegistrationSystem).registerC2DMAsync(context);

		assertTrue(checker.checkRegistrations(context, session));
	}
	
	public void testCheckRegistrationsNotRegisteredForSocialize() {
		
		final Activity context = TestUtils.getActivity(this);
		
		final AppUtils appUtils = Mockito.mock(AppUtils.class);
		final NotificationRegistrationSystem notificationRegistrationSystem = Mockito.mock(NotificationRegistrationSystem.class);
		final NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		final SocializeConfig config = Mockito.mock(SocializeConfig.class);
		final SocializeSession session = Mockito.mock(SocializeSession.class);
		final User user = Mockito.mock(User.class);
		final String registrationId = "foobar";
		
		// Base mocks
		Mockito.when(config.getBooleanProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, true)).thenReturn(true);
		Mockito.when(session.getUser()).thenReturn(user);
		Mockito.when(appUtils.isNotificationsAvailable(context)).thenReturn(true);
		
		NotificationChecker checker = new NotificationChecker();
		
		checker.setAppUtils(appUtils);
		checker.setNotificationRegistrationState(notificationRegistrationState);
		checker.setNotificationRegistrationSystem(notificationRegistrationSystem);
		checker.setConfig(config);
		
		// Orchestrate a state where we are not yet registered for Socialize

		Mockito.when(notificationRegistrationSystem.isRegisteredC2DM(context)).thenReturn(true);
		Mockito.when(notificationRegistrationSystem.isSocializeRegistrationPending()).thenReturn(false);
		Mockito.when(notificationRegistrationSystem.isRegisteredSocialize(context, user)).thenReturn(false);
		Mockito.when(notificationRegistrationState.getC2DMRegistrationId()).thenReturn(registrationId);
		
		notificationRegistrationState.reload(context);
		
		// Expect this once
        Mockito.verify(notificationRegistrationSystem).registerSocialize(context, registrationId);

		assertTrue(checker.checkRegistrations(context, session));
    }
	
}
