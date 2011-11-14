package com.socialize.test.mock;

import com.socialize.api.SocializeAuthRequest;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.AuthProviderListener;

public class MockFacebookAuthProvider implements AuthProvider {

	private SocializeConfig config;
	
	private String userId;
	private String token;
	
	public void init() {
		userId = config.getProperty("facebook.user.id");
		token = config.getProperty("facebook.token");
	}
	
	@Override
	public void authenticate(SocializeAuthRequest authRequest, String appId, AuthProviderListener listener) {
		AuthProviderResponse mockResponse = new AuthProviderResponse();
		mockResponse.setUserId(userId);
		mockResponse.setToken(token);
		listener.onAuthSuccess(mockResponse);
	}

	@Override
	public void clearCache(String appId) {}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
}
