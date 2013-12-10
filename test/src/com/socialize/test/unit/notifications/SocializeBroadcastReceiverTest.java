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
import android.os.Bundle;
import android.test.mock.MockContext;
import com.socialize.SocializeService;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.SocializeBroadcastReceiver;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.notifications.WakeLock;
import com.socialize.test.PublicSocialize;
import com.socialize.test.SocializeUnitTest;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 */
public class SocializeBroadcastReceiverTest extends SocializeUnitTest {

	public void test_onReceive() {
		
		final WakeLock wakeLock = Mockito.mock(WakeLock.class);
		final Intent intent = Mockito.mock(Intent.class);
		final MockContext context = Mockito.mock(MockContext.class);
		final Bundle extras = new Bundle();
		

		
		Mockito.when(wakeLock.acquire(context)).thenReturn(true);
		Mockito.when(intent.getAction()).thenReturn(SocializeC2DMReceiver.C2DM_INTENT);
		Mockito.when(intent.getExtras()).thenReturn(extras);
		Mockito.when(intent.setClassName(context, SocializeC2DMReceiver.class.getName())).thenReturn(intent);
		Mockito.when(context.startService(intent)).thenReturn(null);
		

		PublicSocialize receiver = new PublicSocialize() {
			@Override
			public WakeLock getWakeLock() {
				return wakeLock;
			}
		};
		
		receiver.handleBroadcastIntent(context, intent);

        Mockito.verify(extras).putString(C2DMCallback.SOURCE_KEY, "socialize");
	}
	
	public void testOnRecieveOnBroadcastReceiver() {
		
		final SocializeService service = Mockito.mock(SocializeService.class);
		Intent intent = new Intent();
		
		SocializeBroadcastReceiver receiver = new SocializeBroadcastReceiver() {
			@Override
			protected SocializeService getSocialize() {
				return service;
			}
		};

		receiver.onReceive(getContext(), intent);

        Mockito.verify(service).handleBroadcastIntent(getContext(), intent);
	}
	
}
