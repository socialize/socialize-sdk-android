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
package com.socialize.test.unit.facebook;

import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.auth.facebook.FacebookAuthProviderInfo.PermissionType;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
public class FacebookAuthProviderInfoTest extends SocializeUnitTest {

	public void testEquals() {
		FacebookAuthProviderInfo info0 = new FacebookAuthProviderInfo();
		FacebookAuthProviderInfo info1 = new FacebookAuthProviderInfo();
		FacebookAuthProviderInfo info2 = new FacebookAuthProviderInfo();
		
		info0.setAppId("abc");
		info1.setAppId("abc");
		info2.setAppId("def");
		
		assertTrue(info0.equals(info0));
		assertTrue(info0.equals(info1));
		assertFalse(info0.equals(info2));
	}
	
	public void testMatchesRead() {
		FacebookAuthProviderInfo info0 = new FacebookAuthProviderInfo();
		FacebookAuthProviderInfo info1 = new FacebookAuthProviderInfo();
		FacebookAuthProviderInfo info2 = new FacebookAuthProviderInfo();
		
		info0.setAppId("abc");
		info1.setAppId("abc");
		info2.setAppId("abc");
		
		info0.setReadPermissions(new String[]{"a","b","c"});
		info1.setReadPermissions(new String[]{"b","c"});
		info2.setReadPermissions(new String[]{"c","a","b"});
		
		
		assertTrue(info0.matches(info1));
		assertTrue(info0.matches(info2));
		
		assertTrue(info2.matches(info1));
		assertTrue(info2.matches(info0));
		
		assertFalse(info1.matches(info2));
		assertFalse(info1.matches(info0));
	}
	
	public void testMatchesWrite() {
		FacebookAuthProviderInfo info0 = new FacebookAuthProviderInfo();
		FacebookAuthProviderInfo info1 = new FacebookAuthProviderInfo();
		FacebookAuthProviderInfo info2 = new FacebookAuthProviderInfo();
		
		info0.setAppId("abc");
		info1.setAppId("abc");
		info2.setAppId("abc");
		
		info0.setWritePermissions(new String[]{"a","b","c"});
		info1.setWritePermissions(new String[]{"b","c"});
		info2.setReadPermissions(new String[]{"c","a","b"});
		
		info0.setPermissionType(PermissionType.WRITE);
		info1.setPermissionType(PermissionType.WRITE);
		info2.setPermissionType(PermissionType.READ);
		
		assertTrue(info0.matches(info1));
		assertFalse(info0.matches(info2));
		
		assertFalse(info2.matches(info1));
		assertFalse(info2.matches(info0));
		
		assertFalse(info1.matches(info2));
		assertFalse(info1.matches(info0));
	}	
	
}
