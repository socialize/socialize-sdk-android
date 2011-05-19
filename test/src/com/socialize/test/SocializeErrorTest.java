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
package com.socialize.test;

import com.socialize.error.SocializeApiError;
import com.socialize.util.HttpUtils;

/**
 * @author jasonpolites
 *
 */
public class SocializeErrorTest extends SocializeActivityTest {

	public void testSocializeApiError() {
		final int code = 404;
		SocializeApiError error = new SocializeApiError(code);
		
		// Technically this test is BAD, because we have a dependency on HttpUtils
		// but we can't easily mock this out because it's static, and it's static 
		// because of a lack of a goof dependency injection system for Android (as at today!)
		// HttpUtils is already tested elsewhere.
		
		// Get the result we expect:
		String expected = HttpUtils.getMessageFor(code);
		
		assertEquals(expected, error.getMessage());
		assertEquals(expected, error.getLocalizedMessage());
		assertEquals(code, error.getResultCode());
		
	}
	
}
