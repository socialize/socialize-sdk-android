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

import android.content.Intent;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.notifications.SocializeBroadcastReceiver;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.notifications.WakeLock;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
public class SocializeBroadcastReceiverTest extends SocializeUnitTest {

	@UsesMocks ({WakeLock.class, Intent.class, MockContext.class})
	public void test_onReceive() {
		
		final WakeLock wakeLock = AndroidMock.createMock(WakeLock.class);
		final Intent intent = AndroidMock.createMock(Intent.class);
		final MockContext context = AndroidMock.createMock(MockContext.class);
		
		AndroidMock.expect(wakeLock.acquire(context)).andReturn(true);
		AndroidMock.expect(intent.getAction()).andReturn(SocializeC2DMReceiver.C2DM_INTENT);
		AndroidMock.expect(intent.setClassName(context, SocializeC2DMReceiver.class.getName())).andReturn(intent);
		AndroidMock.expect(context.startService(intent)).andReturn(null);
		
		AndroidMock.replay(wakeLock, intent, context);
		
		SocializeBroadcastReceiver receiver = new SocializeBroadcastReceiver() {
			@Override
			protected WakeLock getWakeLock() {
				return wakeLock;
			}
		};
		
		receiver.onReceive(context, intent);
		
		AndroidMock.verify(wakeLock, intent, context);
	}
	
}
