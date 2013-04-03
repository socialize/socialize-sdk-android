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
package com.socialize.api;

import com.socialize.auth.*;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.profile.UserSettings;

/**
 * @author Jason Polites
 */
public class DefaultSocializeSessionFactory implements SocializeSessionFactory {

	private SocializeConfig config;
	
	public DefaultSocializeSessionFactory(SocializeConfig config) {
		super();
		this.config = config;
	}
	
	@Override
	public WritableSession create(String key, String secret, AuthProviderData data) {
		
		DefaultUserProviderCredentials userProviderCredentials = new DefaultUserProviderCredentials();
		userProviderCredentials.setAccessToken(data.getToken3rdParty());
		userProviderCredentials.setTokenSecret(data.getSecret3rdParty());
		userProviderCredentials.setUserId(data.getUserId3rdParty());
		userProviderCredentials.setAuthProviderInfo(data.getAuthProviderInfo());
		
		return create(key, secret, userProviderCredentials);
	}

	@Override
	public WritableSession create(String key, String secret, UserProviderCredentials userProviderCredentials) {
		SocializeSessionImpl session = SocializeSessionImpl.getInstance();
		session.setConsumerKey(key);
		session.setConsumerSecret(secret);
		session.setUserSettings(new UserSettings());
		
		AuthProviderInfo authProviderInfo = userProviderCredentials.getAuthProviderInfo();
		
		if(authProviderInfo != null) {
			session.setUserProviderCredentials(authProviderInfo.getType(), userProviderCredentials);
		}
		
		if(config != null) {
			String host = config.getProperty(SocializeConfig.API_HOST);
			if(host != null)  {
				session.setHost(host.trim());
			}
		}
		
		return session;
	}
	
	@Override
	public WritableSession create(String key, String secret, UserProviderCredentialsMap userProviderCredentialsMap) {
		SocializeSessionImpl session = SocializeSessionImpl.getInstance();
		session.setConsumerKey(key);
		session.setConsumerSecret(secret);
		session.setUserProviderCredentials(userProviderCredentialsMap);
		if(config != null) {
			String host = config.getProperty(SocializeConfig.API_HOST);
			if(host != null)  {
				session.setHost(host.trim());
			}
		}
		return session;
	}
}
