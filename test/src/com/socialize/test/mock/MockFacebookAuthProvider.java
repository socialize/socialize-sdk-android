package com.socialize.test.mock;

import android.content.Context;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.facebook.FacebookAuthProvider;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.listener.AuthProviderListener;

public class MockFacebookAuthProvider extends FacebookAuthProvider {

	private SocializeConfig config;
	
	private String userId;
	private String token;
	
	public void init() {
		userId = config.getProperty("facebook.user.id");
		token = config.getProperty("facebook.token");
	}

	@Override
	public void authenticate(FacebookAuthProviderInfo info, AuthProviderListener listener) {
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

	@Override
	public boolean validate(FacebookAuthProviderInfo info) {
		return true;
	}
}
