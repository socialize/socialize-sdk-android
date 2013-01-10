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
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.User;
import com.socialize.notifications.NotificationChecker;
import com.socialize.notifications.NotificationRegistrationState;
import com.socialize.notifications.NotificationRegistrationSystem;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.AppUtils;


/**
 * @author Jason Polites
 *
 */
public class NotificationCheckerTest extends SocializeActivityTest {

	@UsesMocks ({
		AppUtils.class, 
		NotificationRegistrationSystem.class, 
		NotificationRegistrationState.class, 
		SocializeConfig.class,
		SocializeSession.class,
		User.class})
	public void testCheckRegistrationsNotRegisteredForC2DM() {
		
		final Activity context = TestUtils.getActivity(this);
		
		final AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		final NotificationRegistrationSystem notificationRegistrationSystem = AndroidMock.createMock(NotificationRegistrationSystem.class);
		final NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final User user = AndroidMock.createMock(User.class);
		
		// Base mocks
		AndroidMock.expect(config.getBooleanProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, true)).andReturn(true).anyTimes();
		AndroidMock.expect(config.getBooleanProperty(SocializeConfig.GCM_REGISTRATION_ENABLED, true)).andReturn(true).anyTimes();
		AndroidMock.expect(session.getUser()).andReturn(user).anyTimes();
		AndroidMock.expect(appUtils.isNotificationsAvailable(context)).andReturn(true).anyTimes();
		
		NotificationChecker checker = new NotificationChecker();
		
		checker.setAppUtils(appUtils);
		checker.setNotificationRegistrationState(notificationRegistrationState);
		checker.setNotificationRegistrationSystem(notificationRegistrationSystem);
		checker.setConfig(config);
		
		// Orchestrate a state where we are not yet registered for C2DM

		AndroidMock.expect(notificationRegistrationSystem.isRegisteredC2DM(context)).andReturn(false).anyTimes();
		AndroidMock.expect(notificationRegistrationSystem.isRegistrationPending()).andReturn(false).anyTimes();
		AndroidMock.expect(notificationRegistrationSystem.isRegisteredSocialize(context, user)).andReturn(false).anyTimes();
		
		notificationRegistrationState.reload(context);
		
		// Expect this once
		notificationRegistrationSystem.registerC2DMAsync(context);
		
		AndroidMock.replay(appUtils, notificationRegistrationSystem, notificationRegistrationState, config, session);
		
		assertTrue(checker.checkRegistrations(context, session));
		
		AndroidMock.verify(appUtils, notificationRegistrationSystem, notificationRegistrationState, config, session);
	}
	
	@UsesMocks ({
		AppUtils.class, 
		NotificationRegistrationSystem.class, 
		NotificationRegistrationState.class, 
		SocializeConfig.class,
		SocializeSession.class,
		User.class})
	public void testCheckRegistrationsNotRegisteredForSocialize() {
		
		final Activity context = TestUtils.getActivity(this);
		
		final AppUtils appUtils = AndroidMock.createMock(AppUtils.class);
		final NotificationRegistrationSystem notificationRegistrationSystem = AndroidMock.createMock(NotificationRegistrationSystem.class);
		final NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final User user = AndroidMock.createMock(User.class);
		final String registrationId = "foobar";
		
		// Base mocks
		AndroidMock.expect(config.getBooleanProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, true)).andReturn(true).anyTimes();
		AndroidMock.expect(session.getUser()).andReturn(user).anyTimes();
		AndroidMock.expect(appUtils.isNotificationsAvailable(context)).andReturn(true).anyTimes();
		
		NotificationChecker checker = new NotificationChecker();
		
		checker.setAppUtils(appUtils);
		checker.setNotificationRegistrationState(notificationRegistrationState);
		checker.setNotificationRegistrationSystem(notificationRegistrationSystem);
		checker.setConfig(config);
		
		// Orchestrate a state where we are not yet registered for Socialize

		AndroidMock.expect(notificationRegistrationSystem.isRegisteredC2DM(context)).andReturn(true).anyTimes();
		AndroidMock.expect(notificationRegistrationSystem.isSocializeRegistrationPending()).andReturn(false).anyTimes();
		AndroidMock.expect(notificationRegistrationSystem.isRegisteredSocialize(context, user)).andReturn(false).anyTimes();
		AndroidMock.expect(notificationRegistrationState.getC2DMRegistrationId()).andReturn(registrationId).anyTimes();
		
		notificationRegistrationState.reload(context);
		
		// Expect this once
		notificationRegistrationSystem.registerSocialize(context, registrationId);
		
		AndroidMock.replay(appUtils, notificationRegistrationSystem, notificationRegistrationState, config, session);
		
		assertTrue(checker.checkRegistrations(context, session));
		
		AndroidMock.verify(appUtils, notificationRegistrationSystem, notificationRegistrationState, config, session);
		
	}	
	
}
