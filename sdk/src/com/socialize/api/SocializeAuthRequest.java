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

import com.socialize.api.SocializeApi.RequestType;
import com.socialize.auth.AuthProviderData;

/**
 * @author Jason Polites
 */
public class SocializeAuthRequest extends SocializeRequest {

	private String consumerKey;
	private String consumerSecret;
	private String udid;
	private AuthProviderData authProviderData;
	
	public SocializeAuthRequest() {
		super();
		setRequestType(RequestType.AUTH);
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String uuid) {
		this.udid = uuid;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public AuthProviderData getAuthProviderData() {
		return authProviderData;
	}

	public void setAuthProviderData(AuthProviderData authProviderData) {
		this.authProviderData = authProviderData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((authProviderData == null) ? 0 : authProviderData.hashCode());
		result = prime * result + ((consumerKey == null) ? 0 : consumerKey.hashCode());
		result = prime * result + ((consumerSecret == null) ? 0 : consumerSecret.hashCode());
		result = prime * result + ((udid == null) ? 0 : udid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocializeAuthRequest other = (SocializeAuthRequest) obj;
		if (authProviderData == null) {
			if (other.authProviderData != null)
				return false;
		}
		else if (!authProviderData.equals(other.authProviderData))
			return false;
		if (consumerKey == null) {
			if (other.consumerKey != null)
				return false;
		}
		else if (!consumerKey.equals(other.consumerKey))
			return false;
		if (consumerSecret == null) {
			if (other.consumerSecret != null)
				return false;
		}
		else if (!consumerSecret.equals(other.consumerSecret))
			return false;
		if (udid == null) {
			if (other.udid != null)
				return false;
		}
		else if (!udid.equals(other.udid))
			return false;
		return true;
	}
	
}
