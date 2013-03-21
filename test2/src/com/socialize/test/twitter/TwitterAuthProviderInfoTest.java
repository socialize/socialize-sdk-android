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
package com.socialize.test.twitter;

import com.socialize.auth.twitter.TwitterAuthProviderInfo;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
public class TwitterAuthProviderInfoTest extends SocializeUnitTest {

	
	public void testEquals() {
		
		TwitterAuthProviderInfo a = new TwitterAuthProviderInfo();
		TwitterAuthProviderInfo b = new TwitterAuthProviderInfo();
		TwitterAuthProviderInfo c = new TwitterAuthProviderInfo();
		TwitterAuthProviderInfo d = new TwitterAuthProviderInfo();
		TwitterAuthProviderInfo e = new TwitterAuthProviderInfo();
		
		String c0 = "a";
		String c1 = "aa";
		
		String s0 = "b";
		String s1 = "bb";
		
		a.setConsumerKey(c0);
		a.setConsumerSecret(s0);
		
		b.setConsumerKey(c1);
		b.setConsumerSecret(s0);
		
		c.setConsumerKey(c0);
		c.setConsumerSecret(s1);
		
		d.setConsumerKey(c1);
		d.setConsumerSecret(s1);
		
		e.setConsumerKey(c0);
		e.setConsumerSecret(s0);
		
		assertFalse(a.equals(b));
		assertFalse(a.equals(c));
		assertFalse(a.equals(d));
		assertTrue(a.equals(e));
		assertTrue(b.equals(b));
	}
	
}
