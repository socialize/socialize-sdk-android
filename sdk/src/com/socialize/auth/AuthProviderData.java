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
 */
public class AuthProviderData {
	
	private String userId3rdParty; 
	private String token3rdParty; 
	private String secret3rdParty;
	
	private AuthProviderInfo authProviderInfo;
	
	public AuthProviderData() {
		super();
	}
	
	public String getUserId3rdParty() {
		return userId3rdParty;
	}
	public void setUserId3rdParty(String userId3rdParty) {
		this.userId3rdParty = userId3rdParty;
	}
	public String getToken3rdParty() {
		return token3rdParty;
	}
	public void setToken3rdParty(String token3rdParty) {
		this.token3rdParty = token3rdParty;
	}
	public String getSecret3rdParty() {
		return secret3rdParty;
	}

	public void setSecret3rdParty(String secret3rdParty) {
		this.secret3rdParty = secret3rdParty;
	}

	public AuthProviderInfo getAuthProviderInfo() {
		return authProviderInfo;
	}

	public void setAuthProviderInfo(AuthProviderInfo authProviderInfo) {
		this.authProviderInfo = authProviderInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authProviderInfo == null) ? 0 : authProviderInfo.hashCode());
		result = prime * result + ((secret3rdParty == null) ? 0 : secret3rdParty.hashCode());
		result = prime * result + ((token3rdParty == null) ? 0 : token3rdParty.hashCode());
		result = prime * result + ((userId3rdParty == null) ? 0 : userId3rdParty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthProviderData other = (AuthProviderData) obj;
		if (authProviderInfo == null) {
			if (other.authProviderInfo != null)
				return false;
		}
		else if (!authProviderInfo.equals(other.authProviderInfo))
			return false;
		if (secret3rdParty == null) {
			if (other.secret3rdParty != null)
				return false;
		}
		else if (!secret3rdParty.equals(other.secret3rdParty))
			return false;
		if (token3rdParty == null) {
			if (other.token3rdParty != null)
				return false;
		}
		else if (!token3rdParty.equals(other.token3rdParty))
			return false;
		if (userId3rdParty == null) {
			if (other.userId3rdParty != null)
				return false;
		}
		else if (!userId3rdParty.equals(other.userId3rdParty))
			return false;
		return true;
	}
	
	
}
