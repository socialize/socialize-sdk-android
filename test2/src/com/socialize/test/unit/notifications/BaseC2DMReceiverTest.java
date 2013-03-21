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
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.notifications.BaseC2DMReceiver;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
public class BaseC2DMReceiverTest extends SocializeUnitTest {

	@UsesMocks ({Intent.class})
	public void test_onHandleIntentRegistration() {
		
		final String action = "foobar";
		
		Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(intent.getAction()).andReturn(action);
		
		AndroidMock.replay(intent);
		
		PublicBaseC2DMReceiver receiver = new PublicBaseC2DMReceiver() {
			@Override
			public Context getApplicationContext() {
				return getContext();
			}

			@Override
			protected boolean isRegistrationAction(String action) {
				return true;
			}

			@Override
			protected boolean isMessageAction(String action) {
				return false;
			}

			@Override
			public void onRegistrationResponse(Context context, Intent intent) {
				addResult(0, intent);
			}
		};
		
		receiver.onHandleIntent(intent);

		AndroidMock.verify(intent);
		
		Intent intentAfter = getResult(0);
		
		assertNotNull(intentAfter);
		assertSame(intent, intentAfter);
	}
	
	@UsesMocks ({Intent.class})
	public void test_onHandleIntentMessage() {
		
		final String action = "foobar";
		
		Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(intent.getAction()).andReturn(action);
		
		AndroidMock.replay(intent);
		
		PublicBaseC2DMReceiver receiver = new PublicBaseC2DMReceiver() {
			@Override
			public Context getApplicationContext() {
				return getContext();
			}

			@Override
			protected boolean isRegistrationAction(String action) {
				return false;
			}

			@Override
			protected boolean isMessageAction(String action) {
				return true;
			}
			
			@Override
			protected void onMessage(Context context, Intent intent) {
				addResult(0, intent);
			}
		};
		
		receiver.onHandleIntent(intent);

		AndroidMock.verify(intent);
		
		Intent intentAfter = getResult(0);
		
		assertNotNull(intentAfter);
		assertSame(intent, intentAfter);
	}	
	
	@UsesMocks ({Intent.class})
	public void test_onRegistrationResponse_onError() {
		String value = "foobar";
		
		Intent intent = AndroidMock.createMock(Intent.class);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_REGISTRATION_ID)).andReturn(null);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_ERROR)).andReturn(value);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_UNREGISTERED)).andReturn(null);
		
		AndroidMock.replay(intent);
		
		PublicBaseC2DMReceiver receiver = new PublicBaseC2DMReceiver() {
			@Override
			protected void onMessage(Context context, Intent intent) {
				fail();
			}

			@Override
			protected void onError(Context context, String errorId) {
				addResult(errorId);
			}

			@Override
			protected void onRegistrered(Context context, String registrationId) {
				fail();
			}

			@Override
			protected void onUnregistered(Context context) {
				fail();
			}
		};
		
		receiver.onRegistrationResponse(getContext(), intent);
		
		AndroidMock.verify(intent);
				
		String success = getNextResult();
		assertNotNull(success);
		assertEquals(value, success);
	}
	
	
	@UsesMocks ({Intent.class})
	public void test_onRegistrationResponse_onRegistrered() {
		
		String value = "foobar";
		
		Intent intent = AndroidMock.createMock(Intent.class);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_REGISTRATION_ID)).andReturn(value);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_ERROR)).andReturn(null);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_UNREGISTERED)).andReturn(null);
		
		AndroidMock.replay(intent);
		
		PublicBaseC2DMReceiver receiver = new PublicBaseC2DMReceiver() {
			@Override
			protected void onMessage(Context context, Intent intent) {
				fail();
			}

			@Override
			protected void onError(Context context, String errorId) {
				fail();
			}

			@Override
			protected void onRegistrered(Context context, String registrationId) {
				addResult(registrationId);
			}

			@Override
			protected void onUnregistered(Context context) {
				fail();
			}
		};
		
		receiver.onRegistrationResponse(getContext(), intent);
		
		AndroidMock.verify(intent);
				
		String success = getNextResult();
		assertNotNull(success);
		assertEquals(value, success);
	}
	
	@UsesMocks ({Intent.class})
	public void test_onRegistrationResponse_noExtra() {
		
		Intent intent = AndroidMock.createMock(Intent.class);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_REGISTRATION_ID)).andReturn(null);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_ERROR)).andReturn(null);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_UNREGISTERED)).andReturn(null);
		
		AndroidMock.replay(intent);
		
		PublicBaseC2DMReceiver receiver = new PublicBaseC2DMReceiver() {
			@Override
			protected void onMessage(Context context, Intent intent) {
				fail();
			}

			@Override
			protected void onError(Context context, String errorId) {
				addResult("success");
			}

			@Override
			protected void onRegistrered(Context context, String registrationId) {
				fail();
			}

			@Override
			protected void onUnregistered(Context context) {
				fail();
			}
		};
		
		receiver.onRegistrationResponse(getContext(), intent);
		
		AndroidMock.verify(intent);
				
		String success = getNextResult();
		assertNotNull(success);
		assertEquals("success", success);
	}	
	
	@UsesMocks ({Intent.class})
	public void test_onRegistrationResponse_onUnregistered() {
		
		String value = "foobar";
		
		Intent intent = AndroidMock.createMock(Intent.class);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_REGISTRATION_ID)).andReturn(null);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_ERROR)).andReturn(null);
		AndroidMock.expect(intent.getStringExtra(BaseC2DMReceiver.EXTRA_UNREGISTERED)).andReturn(value);
		
		AndroidMock.replay(intent);
		
		PublicBaseC2DMReceiver receiver = new PublicBaseC2DMReceiver() {
			@Override
			protected void onMessage(Context context, Intent intent) {
				fail();
			}

			@Override
			protected void onError(Context context, String errorId) {
				fail();
			}

			@Override
			protected void onRegistrered(Context context, String registrationId) {
				fail();
			}

			@Override
			protected void onUnregistered(Context context) {
				addResult("success");
			}
		};
		
		receiver.onRegistrationResponse(getContext(), intent);
		
		AndroidMock.verify(intent);
				
		String success = getNextResult();
		assertNotNull(success);
		assertEquals("success", success);
	}
	
	
}
