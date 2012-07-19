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
package com.socialize.test.mock;

import com.socialize.api.WritableSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentialsMap;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.entity.User;
import com.socialize.ui.profile.UserSettings;

/**
 * @author Jason Polites
 *
 */
public class MockSocializeSession implements WritableSession {

	private static final long serialVersionUID = 2821519529554271142L;
	
	User user;
	UserSettings userSettings;
	String key = "all";
	String sec = "my";
	String tok = "base";
	String toksec = "belongs";
	String host = "to";
	
	private UserProviderCredentialsMap map = new DefaultUserProviderCredentialsMap();
	
	public MockSocializeSession() {
		super();
		user = new User();
		user.setId(-1L);
		
		userSettings = new UserSettings();
	}

	public UserSettings getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getUser()
	 */
	@Override
	public User getUser() {
		return user;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerKey()
	 */
	@Override
	public String getConsumerKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerSecret()
	 */
	@Override
	public String getConsumerSecret() {
		return sec;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerToken()
	 */
	@Override
	public String getConsumerToken() {
		return tok;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerTokenSecret()
	 */
	@Override
	public String getConsumerTokenSecret() {
		return toksec;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}

	@Override
	public UserProviderCredentialsMap getUserProviderCredentials() {
		return map;
	}

	@Override
	public UserProviderCredentials getUserProviderCredentials(AuthProviderType type) {
		return map.get(type);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#clear(com.socialize.auth.AuthProviderType)
	 */
	@Override
	public void clear(AuthProviderType type) {}

	@Override
	public void setConsumerToken(String token) {
	}

	@Override
	public void setConsumerTokenSecret(String secret) {
	}

	@Override
	public void setUser(User user) {
	}

	@Override
	public void setHost(String host) {
	}

	@Override
	public void setUserProviderCredentials(AuthProviderType type, UserProviderCredentials data) {
		map.put(type, data);
	}

	@Override
	public void setUserProviderCredentials(UserProviderCredentialsMap data) {
		this.map = data;
	}

	@Override
	public boolean isRestored() {
		return false;
	}

	@Override
	public void setRestored(boolean restored) {}
	
}
