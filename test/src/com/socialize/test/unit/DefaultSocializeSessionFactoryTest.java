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

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.DefaultSocializeSessionFactory;
import com.socialize.api.WritableSession;
import com.socialize.config.SocializeConfig;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class DefaultSocializeSessionFactoryTest extends SocializeUnitTest {

	@UsesMocks (SocializeConfig.class)
	public void testSessionFactory() {
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.API_HOST)).andReturn("foobar");
		
		AndroidMock.replay(config);
		
		DefaultSocializeSessionFactory factory = new DefaultSocializeSessionFactory(config);
		String key= "foo", secret="bar";
		WritableSession session = factory.create(key, secret);
		assertNotNull(session);
		assertEquals(key, session.getConsumerKey());
		assertEquals(secret, session.getConsumerSecret());
		assertEquals("foobar", session.getHost());
		
		AndroidMock.verify(config);
	}
	
}
