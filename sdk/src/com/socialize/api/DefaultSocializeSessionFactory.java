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
package com.socialize.api;

import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.auth.DefaultUserAuthData;
import com.socialize.auth.UserAuthData;
import com.socialize.config.SocializeConfig;

/**
 * @author Jason Polites
 */
public class DefaultSocializeSessionFactory implements SocializeSessionFactory {

	private SocializeConfig config;
	private AuthProviders authProviders;
	
	public DefaultSocializeSessionFactory(SocializeConfig config, AuthProviders authProviders) {
		super();
		this.config = config;
		this.authProviders = authProviders;
	}
	
	@Override
	public WritableSession create(String key, String secret, AuthProviderData data) {
		
		DefaultUserAuthData userAuthData = new DefaultUserAuthData();
		userAuthData.setAccessToken(data.getToken3rdParty());
		userAuthData.setUserId(data.getUserId3rdParty());
		userAuthData.setAuthProviderInfo(data.getAuthProviderInfo());
		
		return create(key, secret, userAuthData);
	}

	@Override
	public WritableSession create(String key, String secret, UserAuthData userAuthData) {
		SocializeSessionImpl session = new SocializeSessionImpl();
		session.setConsumerKey(key);
		session.setConsumerSecret(secret);
		session.setUserAuthData(userAuthData.getAuthProviderInfo().getType(), userAuthData);
		session.setHost(config.getProperty(SocializeConfig.API_HOST).trim());
		return session;
	}

	@Deprecated
	@Override
	public WritableSession create(String key, String secret, String userId3rdParty, String token3rdParty, String appId3rdParty, AuthProviderType authProviderType) {
		SocializeSessionImpl session = new SocializeSessionImpl();
		session.setConsumerKey(key);
		session.setConsumerSecret(secret);
		session.set3rdPartyUserId(userId3rdParty);
		session.set3rdPartyToken(token3rdParty);
		session.set3rdAppId(appId3rdParty);
		session.setHost(config.getProperty(SocializeConfig.API_HOST).trim());
		session.setAuthProviderType(authProviderType);
		
		if(authProviderType != null) {
			session.setAuthProvider(authProviders.getProvider(authProviderType));
		}

		return session;
	}
}
