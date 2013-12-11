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
import com.socialize.notifications.SocializeC2DMReceiver;
import com.socialize.notifications.SocializeC2DMReceiverHandler;
import com.socialize.test.SocializeUnitTest;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class SocializeC2DMReceiverTest extends SocializeUnitTest {

	public void test_onCreate() throws Exception {
		final SocializeC2DMReceiverHandler container = Mockito.mock(SocializeC2DMReceiverHandler.class);

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

		Mockito.verify(container).onCreate(getContext());
	}
	
	public void test_onDestroy() {
		final SocializeC2DMReceiverHandler container = Mockito.mock(SocializeC2DMReceiverHandler.class);

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
		
		Mockito.verify(container).onDestroy(getContext());
		
		Object nextResult = getNextResult();
		
		assertNotNull(nextResult);
		assertEquals("superOnDestroy", nextResult);
	}
	
	
	public void test_onError() throws Exception {
		final SocializeC2DMReceiverHandler container = Mockito.mock(SocializeC2DMReceiverHandler.class);
		final String errorId = "foobar";

		

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
		
		Mockito.verify(container).onError(getContext(), errorId);
	}	
	
	public void test_onMessage() {
	
		Intent intent = Mockito.mock(Intent.class);
		
		final SocializeC2DMReceiverHandler container = Mockito.mock(SocializeC2DMReceiverHandler.class);

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
		
		Mockito.verify(container).onMessage(getContext(), intent);
	}
	
	public void test_onRegistrered() {
		
		final SocializeC2DMReceiverHandler container = Mockito.mock(SocializeC2DMReceiverHandler.class);
		final String errorId = "foobar";


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
		
		Mockito.verify(container).onRegistered(getContext(), errorId);
	}
	
	public void test_onUnregistered() {
		final SocializeC2DMReceiverHandler container = Mockito.mock(SocializeC2DMReceiverHandler.class);

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
		
		Mockito.verify(container).onUnregistered(getContext());
	}
}
