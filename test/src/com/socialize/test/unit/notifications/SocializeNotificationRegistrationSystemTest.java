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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.test.mock.MockContext;
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
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 *
 */
public class SocializeNotificationRegistrationSystemTest extends SocializeUnitTest {

	@SuppressWarnings("unchecked")
	public void test_doRegistrationSocialize() throws SocializeException {
		
		final String registrationId = "foobar";
		final User user = new User();
		
		DeviceRegistration registration = Mockito.mock(DeviceRegistration.class);
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		
		DeviceRegistrationSystem deviceRegistrationSystem = new DeviceRegistrationSystem() {
			@Override
			public void registerDevice(SocializeSession session, DeviceRegistration registration, DeviceRegistrationListener listener) {
				listener.onSuccess();
			}

			@Override
			public void registerDeviceSynchronous(SocializeSession session, DeviceRegistration registration) throws SocializeException {}
		};
		
		IBeanFactory<DeviceRegistration> deviceRegistrationFactory = Mockito.mock(IBeanFactory.class);
		SocializeSession session = Mockito.mock(SocializeSession.class);
		
		Mockito.when(deviceRegistrationFactory.getBean()).thenReturn(registration);
		Mockito.when(session.getUser()).thenReturn(user);
		


		PublicSocializeNotificationRegistrationSystem system = new PublicSocializeNotificationRegistrationSystem();
		
		system.setDeviceRegistrationFactory(deviceRegistrationFactory);
		system.setDeviceRegistrationSystem(deviceRegistrationSystem);
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		system.doRegistrationSocialize(getContext(), session, registrationId);


        Mockito.verify(registration).setRegistrationId(registrationId);
        Mockito.verify(notificationRegistrationState).setC2DMRegistrationId(registrationId);
        Mockito.verify(notificationRegistrationState).setRegisteredSocialize(user);
        Mockito.verify(notificationRegistrationState).save(getContext());
	}

	public void test_registerC2DM() {
		final String senderId = "foobar";
		final String legacy_senderId = "foobar_legacy";
		
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		SocializeConfig config = Mockito.mock(SocializeConfig.class);
		MockContext context = Mockito.mock(MockContext.class);
		final Intent registrationIntent = Mockito.mock(Intent.class);
		
		Mockito.when(notificationRegistrationState.isC2dmPending()).thenReturn(false);
		
		notificationRegistrationState.setC2dmPendingRequestTime(Mockito.anyLong());

		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID)).thenReturn(legacy_senderId);
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_GCM_SENDER_ID, legacy_senderId)).thenReturn(senderId);
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CUSTOM_GCM_SENDER_ID)).thenReturn(null);
		Mockito.when(config.getBooleanProperty(SocializeConfig.GCM_REGISTRATION_ENABLED, true)).thenReturn(true);
		
		Mockito.when(registrationIntent.putExtra(SocializeNotificationRegistrationSystem.EXTRA_APPLICATION_PENDING_INTENT, (PendingIntent) null)).thenReturn(registrationIntent);
		Mockito.when(registrationIntent.putExtra(SocializeNotificationRegistrationSystem.EXTRA_SENDER, senderId)).thenReturn(registrationIntent);
		Mockito.when(context.startService(registrationIntent)).thenReturn(null);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem() {
			@Override
			public boolean isRegisteredC2DM(Context context) {
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

        Mockito.verify(context).startService(registrationIntent);
	}
	
	public void test_registerC2DMFailed() {
		
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		system.registerC2DMFailed(getContext(), null);
		
		Mockito.verify(notificationRegistrationState).setC2dmPendingRequestTime(0);
	}
	
	public void test_isRegisteredSocialize() {
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		
		final User user = new User();

		Mockito.when(notificationRegistrationState.isRegisteredSocialize(getContext(), user)).thenReturn(true);

		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		assertTrue(system.isRegisteredSocialize(getContext(), user));
	}
	
	public void test_isRegisteredC2DM() {
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		
		Mockito.when(notificationRegistrationState.isRegisteredC2DM(getContext())).thenReturn(true);

		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		assertTrue(system.isRegisteredC2DM(getContext()));
	}
	
	public void test_isRegistrationPending() {
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		
		Mockito.when(notificationRegistrationState.isC2dmPending()).thenReturn(true);

		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem();
		
		system.setNotificationRegistrationState(notificationRegistrationState);
		
		assertTrue(system.isRegistrationPending());
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
		NotificationRegistrationState notificationRegistrationState = Mockito.mock(NotificationRegistrationState.class);
		notificationRegistrationState.setPendingSocializeRequestTime(Mockito.anyLong());
		notificationRegistrationState.save(getContext());
		
		final SocializeSession session = Mockito.mock(SocializeSession.class);
		final SocializeConfig config = Mockito.mock(SocializeConfig.class);
		final String registrationId = "foobar";
		
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).thenReturn("foo");
		Mockito.when(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET)).thenReturn("bar");
		
		SocializeUserSystem userSystem = new SocializeUserSystem(null) {
			@Override
			public void authenticate(Context context, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
				fail();
			}

			@Override
			public SocializeSession authenticateSynchronous(Context ctx, String consumerKey, String consumerSecret) throws SocializeException {
				return session;
			}
		};
		
		SocializeNotificationRegistrationSystem system = new SocializeNotificationRegistrationSystem() {
			@Override
			protected void doRegistrationSocialize(Context context, SocializeSession session, String registrationId) {
				addResult(0, session);
			}
		};
		
		system.setUserSystem(userSystem);
		system.setConfig(config);
		system.setNotificationRegistrationState(notificationRegistrationState);
		system.registerSocialize(getContext(), registrationId);
		
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
