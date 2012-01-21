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
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.error.SocializeException;
import com.socialize.init.DefaultInitializationAsserter;
import com.socialize.listener.SocializeListener;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({SocializeService.class, SocializeListener.class})
public class InitializationAsserterTest extends SocializeUnitTest {

	public void test_assertInitialized() {
		SocializeListener listener = AndroidMock.createMock(SocializeListener.class);
		SocializeService service = AndroidMock.createMock(SocializeService.class);
		
		AndroidMock.expect(service.isInitialized()).andReturn(false);
		listener.onError((SocializeException) AndroidMock.anyObject());
		
		AndroidMock.replay(service, listener);
		
		DefaultInitializationAsserter asserter = new DefaultInitializationAsserter();
		asserter.assertInitialized(service, listener);
		
		AndroidMock.verify(service, listener);
	}
	
	
	public void test_assertAuthenticated() {
		SocializeListener listener = AndroidMock.createMock(SocializeListener.class);
		
		listener.onError((SocializeException) AndroidMock.anyObject());
		
		AndroidMock.replay(listener);
		
		DefaultInitializationAsserter asserter = new DefaultInitializationAsserter() {
			@Override
			public boolean assertInitialized(SocializeService service, SocializeListener listener) {
				return true;
			}
		};
		
		asserter.assertAuthenticated(null, null, listener);
		
		AndroidMock.verify(listener);
	}
}
