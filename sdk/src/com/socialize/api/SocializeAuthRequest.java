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
	private String advertiserId;
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

	public String getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SocializeAuthRequest)) return false;
		if (!super.equals(o)) return false;

		SocializeAuthRequest that = (SocializeAuthRequest) o;

		if (advertiserId != null ? !advertiserId.equals(that.advertiserId) : that.advertiserId != null) return false;
		if (authProviderData != null ? !authProviderData.equals(that.authProviderData) : that.authProviderData != null)
			return false;
		if (consumerKey != null ? !consumerKey.equals(that.consumerKey) : that.consumerKey != null) return false;
		if (consumerSecret != null ? !consumerSecret.equals(that.consumerSecret) : that.consumerSecret != null)
			return false;
		if (udid != null ? !udid.equals(that.udid) : that.udid != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (consumerKey != null ? consumerKey.hashCode() : 0);
		result = 31 * result + (consumerSecret != null ? consumerSecret.hashCode() : 0);
		result = 31 * result + (udid != null ? udid.hashCode() : 0);
		result = 31 * result + (advertiserId != null ? advertiserId.hashCode() : 0);
		result = 31 * result + (authProviderData != null ? authProviderData.hashCode() : 0);
		return result;
	}

}
