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
package com.socialize.test.unit.api;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.activity.SocializeActivitySystem;
import com.socialize.entity.SocializeAction;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.activity.ActionListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 */
@UsesMocks ({SocializeSession.class, ActionListener.class, SocializeProvider.class})
public class UserActivityApiTest extends SocializeUnitTest {

	SocializeProvider<SocializeAction> provider;
	SocializeSession session;
	ActionListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(ActionListener.class);
	}

	public void getActivitysByUser() {
		
		final int key = 69;
		
		SocializeActivitySystem api = new SocializeActivitySystem(provider) {
			
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(key);
			}
		};
		
		api.getActivityByUser(session, key, listener);
		
		Integer after = (Integer) getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after.intValue());
	}
	
	public void getActivitysByUserPaginated() {
		
		final int key = 69;
		int startIndex = 0, endIndex = 10;
		
		SocializeActivitySystem api = new SocializeActivitySystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.getActivityByUser(session, key, startIndex, endIndex, listener);
		
		Integer after = (Integer) getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after.intValue());
		
		int afterStartIndex = (Integer) getNextResult();
		int afterEndIndex = (Integer) getNextResult();
		
		assertEquals(startIndex, afterStartIndex);
		assertEquals(endIndex, afterEndIndex);
	}	
	
}
