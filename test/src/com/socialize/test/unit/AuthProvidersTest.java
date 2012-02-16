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

import java.util.Map;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class AuthProvidersTest extends SocializeUnitTest {

	@SuppressWarnings("unchecked")
	@UsesMocks ({Map.class, AuthProvider.class})
	public void testGetProvider() {
		final AuthProviderType type = AuthProviderType.FACEBOOK;
		Map<Integer, AuthProvider<?>> providerMap = AndroidMock.createMock(Map.class);
		AuthProvider<FacebookAuthProviderInfo> provider = AndroidMock.createMock(AuthProvider.class);
		
		AndroidMock.expect((AuthProvider<FacebookAuthProviderInfo>) providerMap.get(type.getId())).andReturn(provider);
		
		AndroidMock.replay(providerMap);
		
		AuthProviders providers = new AuthProviders();
		providers.setProviders(providerMap);
		
		assertSame(provider, providers.getProvider(type));
		
		AndroidMock.verify(providerMap);
	}
	
}
