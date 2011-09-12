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
package com.socialize.test.unit;

import com.socialize.entity.User;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class UserTest extends SocializeUnitTest {

	public void testUserDisplayName() {

		String fname = "foo";
		String lname = "bar";
		String uname = "snafu";
		
		User user0 = new User();
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();
		
		user0.setFirstName(fname);
		
		user1.setFirstName(fname);
		user1.setLastName(lname);
		
		user2.setLastName(lname);
		
		user3.setUsername(uname);
		
		assertEquals(fname, user0.getDisplayName());
		assertEquals(fname + " " + lname, user1.getDisplayName());
		assertEquals(lname, user2.getDisplayName());
		assertEquals(uname, user3.getDisplayName());
		
	}
	
}
