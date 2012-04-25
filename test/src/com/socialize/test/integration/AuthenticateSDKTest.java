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
package com.socialize.test.integration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.entity.User;


/**
 * @author Jason Polites
 */
public class AuthenticateSDKTest extends SDKIntegrationTest {

	public void testAuthenticate() throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);
		final Activity context = getContext();
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Socialize.getSocialize().init(context);
				
				Socialize.getSocialize().clearSessionCache(context);
				
				SocializeSession session = Socialize.getSocialize().authenticateSynchronous(context);
				
				addResult(0, session.getUser());
				
				SocializeSession session2 = Socialize.getSocialize().authenticateSynchronous(context);
				
				addResult(1, session2.getUser());
				
				latch.countDown();
			}
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		User user1 = getResult(0);
		User user2 = getResult(0);
		
		assertNotNull(user1);
		assertNotNull(user2);
		
		assertEquals(user1, user2);
	}
	
}
