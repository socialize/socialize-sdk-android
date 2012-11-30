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
package com.socialize.test.integration.sdk;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.net.DefaultHttpClientFactory;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
@UsesMocks (SocializeConfig.class)
public class DefaultHttpClientFactoryTest extends SocializeUnitTest {
	
	DefaultHttpClientFactory factory;
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = new DefaultHttpClientFactory();
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(config.getIntProperty(SocializeConfig.HTTP_CONNECTION_TIMEOUT, 10000)).andReturn(60000);
		AndroidMock.expect(config.getIntProperty(SocializeConfig.HTTP_SOCKET_TIMEOUT, 10000)).andReturn(60000);
		
		AndroidMock.replay(config);
		
		factory.init(config);
		
		AndroidMock.verify(config);
	}

	@Override
	protected void tearDown() throws Exception {
		factory.destroy();
		super.tearDown();
	}

	public void testClientFactoryCorrectlyConfiguresAClientForHttps() throws Exception {
		HttpClient client = factory.getClient();
		HttpGet get = new HttpGet("https://google.com/");
		HttpResponse response = client.execute(get);
		assertNotNull(response);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	
	public void testClientFactoryCorrectlyConfiguresAClientForHttp() throws Exception {
		HttpClient client = factory.getClient();
		HttpGet get = new HttpGet("http://google.com/");
		HttpResponse response = client.execute(get);
		assertNotNull(response);
		assertEquals(200, response.getStatusLine().getStatusCode());
	}
	
}
