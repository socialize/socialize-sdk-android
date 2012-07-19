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
public class SocializeSessionImpl implements WritableSession {

	private static final long serialVersionUID = -6937693636536504716L;
	
	private User user;
	private UserSettings userSettings;
	private String consumerKey;
	private String consumerSecret;
	private String consumerToken;
	private String consumerTokenSecret;
	private String host;
	private boolean restored;
	
	private UserProviderCredentialsMap userProviderCredentials;
	
	private static final SocializeSessionImpl instance = new SocializeSessionImpl();
	
	public static final SocializeSessionImpl getInstance() {
		return instance;
	}
	
	public SocializeSessionImpl() {
		super();
		userProviderCredentials = new DefaultUserProviderCredentialsMap();
	}
	
	@Override
	public void clear(AuthProviderType type) {
		userProviderCredentials.remove(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getUserSettings()
	 */
	public UserSettings getUserSettings() {
		return userSettings;
	}

	/*
	 * 	(non-Javadoc)
	 * @see com.socialize.api.WritableSession#setUserSettings(com.socialize.ui.profile.UserSettings)
	 */
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

	public void setUser(User user) {
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerKey()
	 */
	@Override
	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerSecret()
	 */
	@Override
	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerToken()
	 */
	@Override
	public String getConsumerToken() {
		return consumerToken;
	}

	public void setConsumerToken(String consumerToken) {
		this.consumerToken = consumerToken;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getConsumerTokenSecret()
	 */
	@Override
	public String getConsumerTokenSecret() {
		return consumerTokenSecret;
	}

	public void setConsumerTokenSecret(String consumerTokenSecret) {
		this.consumerTokenSecret = consumerTokenSecret;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.SocializeSession#getEndpointRoot()
	 */
	@Override
	public String getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.WritableSession#setHost(java.lang.String)
	 */
	@Override
	public void setHost(String host) {
		this.host = host;
	}

	public UserProviderCredentialsMap getUserProviderCredentials() {
		return userProviderCredentials;
	}

	public void setUserProviderCredentials(UserProviderCredentialsMap userProviderCredentials) {
		this.userProviderCredentials = userProviderCredentials;
	}

	@Override
	public void setUserProviderCredentials(AuthProviderType type, UserProviderCredentials data) {
		userProviderCredentials.put(type, data);
	}

	@Override
	public UserProviderCredentials getUserProviderCredentials(AuthProviderType type) {
		return userProviderCredentials.get(type);
	}

	@Override
	public boolean isRestored() {
		return restored;
	}
	
	@Override
	public void setRestored(boolean restored) {
		this.restored = restored;
	}
}
