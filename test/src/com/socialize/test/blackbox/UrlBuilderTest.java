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

import com.socialize.test.SocializeUnitTest;
import com.socialize.util.UrlBuilder;

/**
 * @author Jason Polites
 */
public class UrlBuilderTest extends SocializeUnitTest {

	public void testUrlBuilder() {

		String endpoint = "foo";
		String key = "bar";
		String[] ids = {"A","B","C"};
		
		UrlBuilder builder = new UrlBuilder();
		
		builder.start(endpoint);
		builder.addParams(key, ids);
		
		String expected = "foo?bar=A&bar=B&bar=C";
		String actual = builder.toString();
		
		assertEquals(expected, actual);
	}
	
	public void testUrlBuilderEncoding() {

		String endpoint = "foo";
		String key = "bar";
		String[] ids = {"with a space and %percent","some && ampersands","a plus++"};
		
		UrlBuilder builder = new UrlBuilder();
		
		builder.start(endpoint);
		builder.addParams(key, ids);
		
		String expected = "foo?bar=with+a+space+and+%25percent&bar=some+%26%26+ampersands&bar=a+plus%2B%2B";
		String actual = builder.toString();
		
		assertEquals(expected, actual);
	}
	
}
