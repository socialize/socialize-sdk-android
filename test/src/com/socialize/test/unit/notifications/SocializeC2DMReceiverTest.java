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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.NotificationContainer;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({NotificationContainer.class, C2DMCallback.class, IOCContainer.class})
public class SocializeC2DMReceiverTest extends SocializeUnitTest {

	public void test_init() throws Exception {
		final NotificationContainer container = AndroidMock.createMock(NotificationContainer.class);
		IOCContainer ioc = AndroidMock.createMock(IOCContainer.class);

		container.onCreate(getContext());
		AndroidMock.expect(container.getContainer()).andReturn(ioc);
		
		AndroidMock.expect( ioc.getBean("logger")).andReturn(null);
		AndroidMock.expect( ioc.getBean("notificationCallback")).andReturn(null);
		
		AndroidMock.replay(container, ioc);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected NotificationContainer newNotificationContainer() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}
		};
		
		receiver.init();
		
		AndroidMock.verify(container, ioc);
		
		assertTrue(receiver.isInitialized());
		
	}
	
	public void test_onDestroy() {
		final NotificationContainer container = AndroidMock.createMock(NotificationContainer.class);

		container.onDestroy(getContext());
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected NotificationContainer newNotificationContainer() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}

			@Override
			protected void superOnDestroy() {
				addResult("superOnDestroy");
			}
		};
		
		receiver.onDestroy();
		
		AndroidMock.verify(container);
		
		assertFalse(receiver.isInitialized());
		
		Object nextResult = getNextResult();
		
		assertNotNull(nextResult);
		assertEquals("superOnDestroy", nextResult);
	}
	
	public void test_onError() {
		final String errorId = "foobar";
		
		C2DMCallback notificationCallback = AndroidMock.createMock(C2DMCallback.class);
		
		notificationCallback.onError(getContext(), errorId);
		
		AndroidMock.replay(notificationCallback);
		
		PublicSocializeC2DMReceiver receiver = new PublicSocializeC2DMReceiver() {
			@Override
			public Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}

			@Override
			public boolean assertInitialized() {
				return true;
			}
		};
		
		receiver.setNotificationCallback(notificationCallback);
		receiver.onError(getContext(), errorId);
		
		AndroidMock.verify(notificationCallback);
	}
	
	@UsesMocks({Intent.class})
	public void test_onMessage() {
	
		Intent intent = AndroidMock.createMock(Intent.class);
		C2DMCallback notificationCallback = AndroidMock.createMock(C2DMCallback.class);
		
		Bundle bundle = new Bundle();
		
		AndroidMock.expect(intent.getExtras()).andReturn(bundle);
		
		notificationCallback.onMessage(getContext(), bundle);
		
		AndroidMock.replay(intent, notificationCallback);
		
		PublicSocializeC2DMReceiver receiver = new PublicSocializeC2DMReceiver() {
			@Override
			public Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}

			@Override
			public boolean assertInitialized() {
				return true;
			}
		};
		
		receiver.setNotificationCallback(notificationCallback);
		receiver.onMessage(getContext(), intent);
		
		AndroidMock.verify(notificationCallback);
	}
	
	public void test_onRegistrered() {
		final String registrationId = "foobar";
		
		C2DMCallback notificationCallback = AndroidMock.createMock(C2DMCallback.class);
		
		notificationCallback.onRegister(getContext(), registrationId);
		
		AndroidMock.replay(notificationCallback);
		
		PublicSocializeC2DMReceiver receiver = new PublicSocializeC2DMReceiver() {
			@Override
			public Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}

			@Override
			public boolean assertInitialized() {
				return true;
			}
		};
		
		receiver.setNotificationCallback(notificationCallback);
		receiver.onRegistrered(getContext(), registrationId);
		
		AndroidMock.verify(notificationCallback);
	}
	
	public void test_onUnregistered() {
		
		C2DMCallback notificationCallback = AndroidMock.createMock(C2DMCallback.class);
		
		notificationCallback.onUnregister(getContext());
		
		AndroidMock.replay(notificationCallback);
		
		PublicSocializeC2DMReceiver receiver = new PublicSocializeC2DMReceiver() {
			@Override
			public Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}

			@Override
			public boolean assertInitialized() {
				return true;
			}
		};
		
		receiver.setNotificationCallback(notificationCallback);
		receiver.onUnregistered(getContext());
		
		AndroidMock.verify(notificationCallback);
	}
}
