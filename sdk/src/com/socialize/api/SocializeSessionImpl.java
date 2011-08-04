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

import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;

/**
 * @author Jason Polites
 *
 */
public class SocializeSessionImpl implements WritableSession {

	private User user;
	private String consumerKey;
	private String consumerSecret;
	private String consumerToken;
	private String consumerTokenSecret;
	private String host;
	private String appId3rdParty;
	private String userId3rdParty;
	private String token3rdParty;
	private AuthProviderType authProviderType;
	
	public SocializeSessionImpl() {
		super();
	}
	
	@Override
	public String get3rdPartyUserId() {
		return userId3rdParty;
	}
	
	@Override
	public String get3rdPartyToken() {
		return token3rdParty;
	}
	
	@Override
	public void set3rdPartyUserId(String userId) {
		this.userId3rdParty = userId;
		
	}
	
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

	@Override
	public AuthProviderType getAuthProviderType() {
		return authProviderType;
	}

	@Override
	public void setAuthProviderType(AuthProviderType authProviderType) {
		this.authProviderType = authProviderType;
	}

	@Override
	public String get3rdPartyAppId() {
		return appId3rdParty;
	}

	@Override
	public void set3rdAppId(String appId) {
		this.appId3rdParty = appId;
	}
}
