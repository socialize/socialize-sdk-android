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

import android.test.mock.MockContext;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeApiError;
import com.socialize.log.SocializeLogger;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.TestUtils;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.HttpUtils;
import com.socialize.util.ResourceLocator;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * @author jasonpolites
 *
 */
public class HttpUtilsTest extends SocializeActivityTest {

	public void testHttpStatusCodes() throws Throwable {
		
		HttpUtils utils = new HttpUtils();
		ResourceLocator resourceLocator = new ResourceLocator();
		ClassLoaderProvider provider = new ClassLoaderProvider();
		resourceLocator.setClassLoaderProvider(provider);
		utils.setResourceLocator(resourceLocator);
		utils.setLogger(new SocializeLogger());
		utils.init(TestUtils.getActivity(this));
		
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
	
	public void testHttpIsAuthErrorFromResponse() {
		
		final int code = 69;
		
		HttpResponse response = Mockito.mock(HttpResponse.class);
		StatusLine statusLine = Mockito.mock(StatusLine.class);

        Mockito.when(response.getStatusLine()).thenReturn(statusLine);
        Mockito.when(statusLine.getStatusCode()).thenReturn(69);

		HttpUtils utils = new HttpUtils() {

			@Override
			public boolean isAuthError(int code) {
				addResult(code);
				return super.isAuthError(code);
			}
		};
		
		utils.isAuthError(response);
		
		Integer result = getNextResult();
		
		assertNotNull(result);
		assertEquals(code, result.intValue());
	}
	
	public void testHttpIsAuthError() {
		HttpUtils utils = new HttpUtils();
		assertTrue(utils.isAuthError(401));
		assertTrue(utils.isAuthError(403));
		assertFalse(utils.isAuthError(301));
		assertFalse(utils.isAuthError(200));
		assertFalse(utils.isAuthError(500));
	}
	
	public void test_isAuthErrorWithException() {
		
		int resultCode = 69;
		String message = "foobar";
		
		SocializeApiError error = Mockito.mock(SocializeApiError.class);
		
		Mockito.when(error.getResultCode()).thenReturn(resultCode);
		
		HttpUtils utils = new HttpUtils() {
			@Override
			public boolean isAuthError(int code) {
				addResult(code);
				return false;
			}
		};
		
		utils.isAuthError(error);
		
		Integer result = getNextResult();
		
		assertNotNull(result);
		assertEquals(resultCode, result.intValue());
	}
	
	public void testHttpUtilsInitFail() throws IOException {
		ResourceLocator locator = Mockito.mock(ResourceLocator.class);
		SocializeLogger logger = Mockito.mock(SocializeLogger.class);
		MockContext context = new MockContext();
		
		
		// Mock an inputStream from a fail string
		String failString = "NaN=OK";
		ByteArrayInputStream bin = new ByteArrayInputStream(failString.getBytes());
		
		Mockito.when(locator.locate(context, SocializeConfig.SOCIALIZE_ERRORS_PATH)).thenReturn(bin);
		Mockito.when(logger.isWarnEnabled()).thenReturn(true);

		logger.warn("NaN is not an integer");
		
		HttpUtils utils = new HttpUtils();
		utils.setLogger(logger);
		utils.setResourceLocator(locator);
		utils.init(context);

		Mockito.verify(locator).locate(context, SocializeConfig.SOCIALIZE_ERRORS_PATH);
		Mockito.verify(logger).isWarnEnabled();
	}
}
