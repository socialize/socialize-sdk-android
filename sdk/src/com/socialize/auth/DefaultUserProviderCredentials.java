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
package com.socialize.auth;

/**
 * @author Jason Polites
 *
 */
public class DefaultUserProviderCredentials implements UserProviderCredentials {

	private String userId;
	private String accessToken;
	private String tokenSecret;
	
	private AuthProviderInfo authProviderInfo;
	
	/* (non-Javadoc)
	 * @see com.socialize.auth.UserProviderCredentials#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.UserProviderCredentials#getAccessToken()
	 */
	@Override
	public String getAccessToken() {
		return accessToken;
	}

	/* (non-Javadoc)
	 * @see com.socialize.auth.UserProviderCredentials#getAuthProviderInfo()
	 */
	@Override
	@Deprecated
	public AuthProviderInfo getAuthProviderInfo() {
		return authProviderInfo;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.auth.UserProviderCredentials#getTokenSecret()
	 */
	@Override
	public String getTokenSecret() {
		return tokenSecret;
	}
	
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setAuthProviderInfo(AuthProviderInfo authProviderInfo) {
		this.authProviderInfo = authProviderInfo;
	}
	
	public void merge(UserProviderCredentials that) {
		setAccessToken(that.getAccessToken());
		setTokenSecret(that.getTokenSecret());
		setUserId(that.getUserId());
		if(this.authProviderInfo != null && that.getAuthProviderInfo() != null) {
			this.authProviderInfo.merge(that.getAuthProviderInfo());
		}
	}
}
