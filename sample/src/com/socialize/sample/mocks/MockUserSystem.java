package com.socialize.sample.mocks;

import android.app.Activity;
import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.UserSystem;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.user.UserListener;
import com.socialize.ui.profile.UserProfile;

public class MockUserSystem implements UserSystem {
	
	private User user = new User();
	
	public MockUserSystem(User user) {
		super();
		this.user = user;
	}
	
	@Override
	public void init(Activity context) {}

	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
	}

	@Deprecated
	@Override
	public void authenticate(String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
	}

	@Override
	public void clearSession() {}

	@Override
	public void clearSession(AuthProviderType type) {}

	@Override
	public void getUser(SocializeSession session, long id, UserListener listener) {
		listener.onGet(user);
	}

	@Override
	public void saveUserProfile(Context context, SocializeSession session, UserProfile profile, UserListener listener) {
		listener.onUpdate(user);
	}

	@Deprecated
	@Override
	public void saveUserProfile(Context context, SocializeSession session, String firstName, String lastName, String encodedImage, UserListener listener) {
		listener.onUpdate(user);
	}
}
