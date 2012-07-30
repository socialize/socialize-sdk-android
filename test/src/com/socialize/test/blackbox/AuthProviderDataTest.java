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
package com.socialize.test.blackbox;

import com.socialize.auth.AuthProviderData;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class AuthProviderDataTest extends SocializeUnitTest {

	// No value in mocking a data bean.. so just assert in/out
	public void testAuthProviderData() {
	
		AuthProviderData data = new AuthProviderData();
		
		String token3rdParty = "foobar_token3rdParty";
		String userId3rdParty = "foobar_userId3rdParty";
		
		data.setToken3rdParty(token3rdParty);
		data.setUserId3rdParty(userId3rdParty);
		
		assertEquals(token3rdParty, data.getToken3rdParty());
		assertEquals(userId3rdParty, data.getUserId3rdParty());
	}
	
	public void testEquals() {
		
		AuthProviderData data0 = new AuthProviderData();
		AuthProviderData data1 = new AuthProviderData();
		AuthProviderData data2 = new AuthProviderData();
		
		String token0 = "token0";
		String token1 = "token1";
		String token2 = "token0";
		
		String secret0 = "secret0";
		String secret1 = "secret1";
		String secret2 = "secret0";
		
		String user0 = "user0";
		String user1 = "user1";
		String user2 = "user0";	
		
		
		data0.setToken3rdParty(token0);
		data1.setToken3rdParty(token1);
		data2.setToken3rdParty(token2);
		
		data0.setSecret3rdParty(secret0);
		data1.setSecret3rdParty(secret1);
		data2.setSecret3rdParty(secret2);
		
		data0.setUserId3rdParty(user0);
		data1.setUserId3rdParty(user1);
		data2.setUserId3rdParty(user2);
		
		assertFalse(data0.equals(data1));
		assertFalse(data1.equals(data2));
		
		assertTrue(data1.equals(data1));
		assertTrue(data0.equals(data2));
	}
}
