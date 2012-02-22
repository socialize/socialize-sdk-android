package com.socialize.sample.mocks;

import android.content.Context;

import com.socialize.api.SocializeAuthRequest;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.AuthProviderListener;

public class MockFacebookAuthProvider implements AuthProvider<FacebookAuthProviderInfo> {

	private SocializeConfig config;
	
	private String userId;
	private String token;
	
	public void init() {
		userId = config.getProperty("facebook.user.id");
		token = config.getProperty("facebook.token");
	}
	
	@Deprecated
	@Override
	public void authenticate(SocializeAuthRequest authRequest, String appId, AuthProviderListener listener) {
		AuthProviderResponse mockResponse = new AuthProviderResponse();
		mockResponse.setUserId(userId);
		mockResponse.setToken(token);
		listener.onAuthSuccess(mockResponse);
	}

	@Deprecated
	@Override
	public void clearCache(Context context, String appId) {}
	

	@Override
	public void authenticate(SocializeAuthRequest authRequest, FacebookAuthProviderInfo info, AuthProviderListener listener) {
		AuthProviderResponse mockResponse = new AuthProviderResponse();
		mockResponse.setUserId(userId);
		mockResponse.setToken(token);
		listener.onAuthSuccess(mockResponse);
	}

	@Override
	public void clearCache(Context context, FacebookAuthProviderInfo info) {}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
}
