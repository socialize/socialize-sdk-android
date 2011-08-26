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
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;

/**
 * @author Jason Polites
 */
public class SocializeAuthRequest extends SocializeRequest {

	private String consumerKey;
	private String consumerSecret;
	private String udid;
	private String authUserId3rdParty;
	private String authToken3rdParty;
	private AuthProviderType authProviderType;
	private String appId3rdParty;

	private AuthProviderData authProviderData;
	private AuthProviderResponse providerResponse;
	
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

	@Deprecated
	public String getAuthUserId3rdParty() {
		return authUserId3rdParty;
	}

	@Deprecated
	public void setAuthUserId3rdParty(String authUserId3rdParty) {
		this.authUserId3rdParty = authUserId3rdParty;
	}

	@Deprecated
	public String getAuthToken3rdParty() {
		return authToken3rdParty;
	}

	@Deprecated
	public void setAuthToken3rdParty(String authToken3rdParty) {
		this.authToken3rdParty = authToken3rdParty;
	}

	@Deprecated
	public AuthProviderType getAuthProviderType() {
		return authProviderType;
	}

	@Deprecated
	public void setAuthProviderType(AuthProviderType authProviderType) {
		this.authProviderType = authProviderType;
	}

	@Deprecated
	public String getAppId3rdParty() {
		return appId3rdParty;
	}

	@Deprecated
	public void setAppId3rdParty(String appId3rdParty) {
		this.appId3rdParty = appId3rdParty;
	}
	
	@Deprecated
	public AuthProviderResponse getProviderResponse() {
		return providerResponse;
	}

	@Deprecated
	public void setProviderResponse(AuthProviderResponse providerResponse) {
		this.providerResponse = providerResponse;
	}

	public AuthProviderData getAuthProviderData() {
		return authProviderData;
	}

	public void setAuthProviderData(AuthProviderData authProviderData) {
		this.authProviderData = authProviderData;
	}
}
