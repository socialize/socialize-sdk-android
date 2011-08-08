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
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.config.SocializeConfig;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class DefaultSocializeSessionFactoryTest extends SocializeUnitTest {

	@UsesMocks ({SocializeConfig.class, AuthProviders.class, AuthProvider.class})
	public void testSessionFactory() {
		
		final AuthProviderType authProviderType = AuthProviderType.SOCIALIZE;
		
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		AuthProviders providers = AndroidMock.createMock(AuthProviders.class);
		AuthProvider provider = AndroidMock.createMock(AuthProvider.class);
		
		AndroidMock.expect(config.getProperty(SocializeConfig.API_HOST)).andReturn("foobar");
		AndroidMock.expect(providers.getProvider(authProviderType)).andReturn(provider);
		
		AndroidMock.replay(config);
		AndroidMock.replay(providers);
		
		DefaultSocializeSessionFactory factory = new DefaultSocializeSessionFactory(config, providers);
		String key= "foo", secret="bar";
		String userId3rdParty = "foobar_userId3rdParty";
		String token3rdParty = "foobar_token3rdParty";
		String appId3rdParty = "foobar_appId3rdParty";
		
		
		WritableSession session = factory.create(key, secret, userId3rdParty, token3rdParty, appId3rdParty, authProviderType);
		
		assertNotNull(session);
		
		assertEquals(key, session.getConsumerKey());
		assertEquals(secret, session.getConsumerSecret());
		assertEquals(userId3rdParty, session.get3rdPartyUserId());
		assertEquals(token3rdParty, session.get3rdPartyToken());
		assertEquals(appId3rdParty, session.get3rdPartyAppId());
		assertEquals(authProviderType, session.getAuthProviderType());
		
		assertEquals("foobar", session.getHost());
		
		AndroidMock.verify(config);
		AndroidMock.verify(providers);
	}
	
}
