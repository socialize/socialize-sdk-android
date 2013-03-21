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
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.notifications.SocializeC2DMReceiverHandler;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({SocializeC2DMReceiverHandler.class, C2DMCallback.class})
public class SocializeC2DMReceiverTest extends SocializeUnitTest {

	public void test_onCreate() throws Exception {
		final SocializeC2DMReceiverHandler container = AndroidMock.createMock(SocializeC2DMReceiverHandler.class);

		container.onCreate(getContext());
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected SocializeC2DMReceiverHandler newC2DMReceiverHandler() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}
			
		};
		
		receiver.onCreate();
		
		AndroidMock.verify(container);
	}
	
	public void test_onDestroy() {
		final SocializeC2DMReceiverHandler container = AndroidMock.createMock(SocializeC2DMReceiverHandler.class);

		container.onDestroy(getContext());
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			
			@Override
			protected SocializeC2DMReceiverHandler newC2DMReceiverHandler() {
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
		
		Object nextResult = getNextResult();
		
		assertNotNull(nextResult);
		assertEquals("superOnDestroy", nextResult);
	}
	
	
	public void test_onError() throws Exception {
		final SocializeC2DMReceiverHandler container = AndroidMock.createMock(SocializeC2DMReceiverHandler.class);
		final String errorId = "foobar";
		container.onError(getContext(), errorId);
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected SocializeC2DMReceiverHandler newC2DMReceiverHandler() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}
		};
		
		receiver.onError(getContext(), errorId);
		
		AndroidMock.verify(container);
	}	
	
	@UsesMocks({Intent.class})
	public void test_onMessage() {
	
		Intent intent = AndroidMock.createMock(Intent.class);
		
		final SocializeC2DMReceiverHandler container = AndroidMock.createMock(SocializeC2DMReceiverHandler.class);
		container.onMessage(getContext(), intent);
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected SocializeC2DMReceiverHandler newC2DMReceiverHandler() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}
		};
		
		receiver.onMessage(getContext(), intent);
		
		AndroidMock.verify(container);		
	}
	
	public void test_onRegistrered() {
		
		final SocializeC2DMReceiverHandler container = AndroidMock.createMock(SocializeC2DMReceiverHandler.class);
		final String errorId = "foobar";
		container.onRegistered(getContext(), errorId);
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected SocializeC2DMReceiverHandler newC2DMReceiverHandler() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}
		};
		
		receiver.onRegistrered(getContext(), errorId);
		
		AndroidMock.verify(container);		
	}
	
	public void test_onUnregistered() {
		final SocializeC2DMReceiverHandler container = AndroidMock.createMock(SocializeC2DMReceiverHandler.class);
		container.onUnregistered(getContext());
		
		AndroidMock.replay(container);
		
		SocializeC2DMReceiver receiver = new SocializeC2DMReceiver() {
			@Override
			protected SocializeC2DMReceiverHandler newC2DMReceiverHandler() {
				return container;
			}

			@Override
			protected Context getContext() {
				return SocializeC2DMReceiverTest.this.getContext();
			}
		};
		
		receiver.onUnregistered(getContext());
		
		AndroidMock.verify(container);	
	}
}
