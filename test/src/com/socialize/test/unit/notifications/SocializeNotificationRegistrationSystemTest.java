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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.test.mock.MockContext;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.DeviceRegistrationListener;
import com.socialize.api.DeviceRegistrationSystem;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.user.SocializeUserSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.DeviceRegistration;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.notifications.NotificationRegistrationState;
import com.socialize.notifications.SocializeNotificationRegistrationSystem;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	DeviceRegistration.class, 
	NotificationRegistrationState.class, 
	DeviceRegistrationSystem.class,
	IBeanFactory.class,
	SocializeSession.class})
public class SocializeNotificationRegistrationSystemTest extends SocializeUnitTest {

	@SuppressWarnings("unchecked")
	public void test_doRegistrationSocialize() throws SocializeException {
		
		final String registrationId = "foobar";
		final User user = new User();
		
		DeviceRegistration registration = AndroidMock.createMock(DeviceRegistration.class);
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		
		DeviceRegistrationSystem deviceRegistrationSystem = new DeviceRegistrationSystem() {
			@Override
			public void registerDevice(SocializeSession session, DeviceRegistration registration, DeviceRegistrationListener listener) {
				listener.onSuccess();
			}
		};
		
		IBeanFactory<DeviceRegistration> deviceRegistrationFactory = AndroidMock.createMock(IBeanFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.expect(deviceRegistrationFactory.getBean()).andReturn(registration);
		AndroidMock.expect(session.getUser()).andReturn(user);
		
		registration.setRegistrationId(registrationId);
		notificationRegistrationState.setC2DMRegistrationId(registrationId);
		notificationRegistrationState.setRegisteredSocialize(user);
		notificationRegistrationState.save(getContext());
		
		AndroidMock.replay(deviceRegistrationFactory, registration, notificationRegistrationState, session);
		
		PublicSocializeNotificationRegistrationSystem system = new PublicSocializeNotificationRegistrationSystem();
		
		system.setDeviceRegistrationFactory(deviceRegistrationFactory);
		system.setDeviceRegistrationSystem(deviceRegistrationSystem);
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		system.doRegistrationSocialize(getContext(), session, registrationId);
		
		AndroidMock.verify(deviceRegistrationFactory, registration, notificationRegistrationState, session);
	}
	
	@SuppressWarnings("deprecation")
	@UsesMocks ({SocializeConfig.class, MockContext.class, Intent.class})
	public void test_registerC2DM() {
		final String senderId = "foobar";
		final String legacy_senderId = "foobar_legacy";
		
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		MockContext context = AndroidMock.createNiceMock(MockContext.class);
		final Intent registrationIntent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(notificationRegistrationState.isC2dmPending()).andReturn(false);
		
		notificationRegistrationState.setC2dmPendingRequestTime(AndroidMock.anyLong());
		notificationRegistrationState.save(context);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID)).andReturn(legacy_senderId);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_GCM_SENDER_ID, legacy_senderId)).andReturn(senderId);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CUSTOM_GCM_SENDER_ID)).andReturn(null);
		
		AndroidMock.expect(registrationIntent.putExtra(SocializeNotificationRegistrationSystem.EXTRA_APPLICATION_PENDING_INTENT, (PendingIntent) null)).andReturn(registrationIntent);
		AndroidMock.expect(registrationIntent.putExtra(SocializeNotificationRegistrationSystem.EXTRA_SENDER, senderId)).andReturn(registrationIntent);
		AndroidMock.expect(context.startService(registrationIntent)).andReturn(null);		
		
		AndroidMock.replay(notificationRegistrationState, config, context, registrationIntent);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem() {
			@Override
			public boolean isRegisteredC2DM() {
				return false;
			}

			@Override
			protected Intent newIntent(String action) {
				return registrationIntent;
			}

			@Override
			protected PendingIntent newPendingIntent(Context context) {
				return null;
			}
		};
		
		system.setConfig(config);
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		system.registerC2DM(context);
		
		AndroidMock.verify(notificationRegistrationState, config, context, registrationIntent);
	}
	
	public void test_registerC2DMFailed() {
		
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		
		notificationRegistrationState.setC2dmPendingRequestTime(0);
		notificationRegistrationState.save(getContext());
		
		AndroidMock.replay(notificationRegistrationState);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		system.registerC2DMFailed(getContext(), null);
		
		AndroidMock.verify(notificationRegistrationState);
	}
	
	public void test_isRegisteredSocialize() {
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		
		final User user = new User();
		AndroidMock.expect(notificationRegistrationState.isRegisteredSocialize(user)).andReturn(true);
		AndroidMock.replay(notificationRegistrationState);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		assertTrue(system.isRegisteredSocialize(user));
		
		AndroidMock.verify(notificationRegistrationState);
	}
	
	public void test_isRegisteredC2DM() {
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		
		AndroidMock.expect(notificationRegistrationState.isRegisteredC2DM()).andReturn(true);
		AndroidMock.replay(notificationRegistrationState);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		assertTrue(system.isRegisteredC2DM());
		
		AndroidMock.verify(notificationRegistrationState);
	}
	
	public void test_isRegistrationPending() {
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		
		AndroidMock.expect(notificationRegistrationState.isC2dmPending()).andReturn(true);
		AndroidMock.replay(notificationRegistrationState);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		assertTrue(system.isRegistrationPending());
		
		AndroidMock.verify(notificationRegistrationState);
	}
	
	public void test_registerC2DMAsync() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem() {
			@Override
			public synchronized void registerC2DM(Context context) {
				latch.countDown();
			}
		};
		
		system.registerC2DMAsync(getContext());
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}
	
	
	public void test_registerSocialize() {
		NotificationRegistrationState notificationRegistrationState = AndroidMock.createMock(NotificationRegistrationState.class);
		notificationRegistrationState.setPendingSocializeRequestTime(AndroidMock.anyLong());
		notificationRegistrationState.save(getContext());
		
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final String registrationId = "foobar";
		
		SocializeUserSystem userSystem = new SocializeUserSystem(null) {
			@Override
			public void authenticate(Context context, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
				listener.onAuthSuccess(session);
			}
		};
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem() {
			@Override
			protected void doRegistrationSocialize(Context context, SocializeSession session, String registrationId) {
				addResult(0, session);
			}
		};
		
		system.setUserSystem(userSystem);
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		AndroidMock.replay(notificationRegistrationState, session);
		
		system.registerSocialize(getContext(), registrationId);
		
		AndroidMock.verify(notificationRegistrationState, session);
		
		SocializeSession result = getResult(0);
		
		assertSame(session, result);
		
	}
	
	
	class PublicSocializeNotificationRegistrationSystem extends SocializeNotificationRegistrationSystem {

		@Override
		public void doRegistrationSocialize(Context context, SocializeSession session, String registrationId) {
			super.doRegistrationSocialize(context, session, registrationId);
		}
		
	}
}
