/*
 * Copyright (c) 2011 Socialize Inc.
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
import com.socialize.auth.AuthProviderType;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class AuthProviderDataTest extends SocializeUnitTest {

	// No value in mocking a data bean.. so just assert in/out
	public void testAuthProviderData() {
	
		AuthProviderData data = new AuthProviderData();
		
		String appId3rdParty = "foobar_appId3rdParty";
		String token3rdParty = "foobar_token3rdParty";
		String userId3rdParty = "foobar_userId3rdParty";
		AuthProviderType authProviderType = AuthProviderType.FACEBOOK;
		
		data.setAppId3rdParty(appId3rdParty);
		data.setAuthProviderType(authProviderType);
		data.setToken3rdParty(token3rdParty);
		data.setUserId3rdParty(userId3rdParty);
		
		assertEquals(appId3rdParty, data.getAppId3rdParty());
		assertEquals(authProviderType, data.getAuthProviderType());
		assertEquals(token3rdParty, data.getToken3rdParty());
		assertEquals(userId3rdParty, data.getUserId3rdParty());
	}
}
