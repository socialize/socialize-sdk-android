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

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.log.SocializeLogger;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.HttpUtils;
import com.socialize.util.ResourceLocator;


/**
 * @author jasonpolites
 *
 */
public class HttpUtilsTest extends SocializeActivityTest {

	public void testHttpStatusCodes() throws Exception {
		
		HttpUtils utils = new HttpUtils();
		ResourceLocator resourceLocator = new ResourceLocator();
		ClassLoaderProvider provider = new ClassLoaderProvider();
		resourceLocator.setClassLoaderProvider(provider);
		utils.setResourceLocator(resourceLocator);
		utils.setLogger(new SocializeLogger());
		utils.init(getActivity());
		
		// Just test that the codes are loaded.  We assume the messages are correct
		int[] codes = {
				100,
				101,
				200,
				201,
				202,
				203,
				204,
				205,
				206,
				300,
				301,
				302,
				303,
				304,
				305,
				307,
				400,
				401,
				402,
				403,
				404,
				405,
				406,
				407,
				408,
				409,
				410,
				411,
				412,
				413,
				414,
				415,
				416,
				417,
				500,
				501,
				502,
				503,
				504,
				505};
		
		for (int i : codes) {
			assertNotNull(utils.getMessageFor(i));
		}
	}
	
	public void testHttpIsError() {
		HttpUtils utils = new HttpUtils();
		assertTrue(utils.isHttpError(500));
		assertTrue(utils.isHttpError(404));
		assertFalse(utils.isHttpError(301));
		assertFalse(utils.isHttpError(200));
	}
	
	@UsesMocks ({HttpResponse.class, StatusLine.class})
	public void testHttpIsAuthErrorFromResponse() {
		
		final int code = 69;
		
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		StatusLine statusLine = AndroidMock.createMock(StatusLine.class);
		
		AndroidMock.expect(response.getStatusLine()).andReturn(statusLine);
		AndroidMock.expect(statusLine.getStatusCode()).andReturn(69);
		
		AndroidMock.replay(response);
		AndroidMock.replay(statusLine);
		
		HttpUtils utils = new HttpUtils() {

			@Override
			public boolean isAuthError(int code) {
				addResult(code);
				return super.isAuthError(code);
			}
		};
		
		utils.isAuthError(response);
		
		Integer result = getResult();
		
		assertNotNull(result);
		assertEquals(code, result.intValue());
		
		AndroidMock.verify(response);
		AndroidMock.verify(statusLine);
		
	}
	
	public void testHttpIsAuthError() {
		HttpUtils utils = new HttpUtils();
		assertTrue(utils.isAuthError(401));
		assertTrue(utils.isAuthError(403));
		assertFalse(utils.isAuthError(301));
		assertFalse(utils.isAuthError(200));
		assertFalse(utils.isAuthError(500));
	}
}
