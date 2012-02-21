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

import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserAuthDataMap;
import com.socialize.auth.UserAuthData;
import com.socialize.auth.UserAuthDataMap;
import com.socialize.entity.User;

/**
 * @author Jason Polites
 *
 */
public class SocializeSessionImpl implements WritableSession {

	private static final long serialVersionUID = -6937693636536504716L;
	
	private User user;
	private String consumerKey;
	private String consumerSecret;
	private String consumerToken;
	private String consumerTokenSecret;
	private String host;
	
	private UserAuthDataMap userAuthData;
	
	@Deprecated
	private String appId3rdParty;
	
	@Deprecated
	private String userId3rdParty;
	
	@Deprecated
	private String token3rdParty;
	
	@Deprecated
	private AuthProviderType authProviderType;
	
	@SuppressWarnings("rawtypes")
	@Deprecated
	private AuthProvider authProvider;
	
	public SocializeSessionImpl() {
		super();
		
		userAuthData = new DefaultUserAuthDataMap();
	}
	
	@Override
	public void clear(AuthProviderType type) {
		token3rdParty = null;
		userId3rdParty = null;
		authProviderType = AuthProviderType.SOCIALIZE;
		authProvider = null;
	}

	@Deprecated
	@Override
	public String get3rdPartyUserId() {
		return userId3rdParty;
	}
	
	@Deprecated
	@Override
	public String get3rdPartyToken() {
		return token3rdParty;
	}
	
	@Deprecated
	@Override
	public void set3rdPartyUserId(String userId) {
		this.userId3rdParty = userId;
		
	}
	
	@Deprecated
	@Override
	public void set3rdPartyToken(String token) {
		this.token3rdParty = token;
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

	@Deprecated
	@Override
	public AuthProviderType getAuthProviderType() {
		return authProviderType;
	}

	@Deprecated
	@Override
	public void setAuthProviderType(AuthProviderType authProviderType) {
		this.authProviderType = authProviderType;
	}

	@Deprecated
	@Override
	public String get3rdPartyAppId() {
		return appId3rdParty;
	}

	@Deprecated
	@Override
	public void set3rdAppId(String appId) {
		this.appId3rdParty = appId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Deprecated
	@Override
	public AuthProvider getAuthProvider() {
		return authProvider;
	}
	
	@SuppressWarnings("rawtypes")
	@Deprecated
	@Override
	public void setAuthProvider(AuthProvider authProvider) {
		this.authProvider = authProvider;
	}

	public UserAuthDataMap getUserAuthData() {
		return userAuthData;
	}

	public void setUserAuthData(UserAuthDataMap userAuthData) {
		this.userAuthData = userAuthData;
	}

	@Override
	public void setUserAuthData(AuthProviderType type, UserAuthData data) {
		userAuthData.put(type, data);
	}

	@Override
	public UserAuthData getUserAuthData(AuthProviderType type) {
		return userAuthData.get(type);
	}
	
}
