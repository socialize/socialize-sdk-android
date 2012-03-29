package com.socialize.test.mock;

import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.api.action.UserSystem;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.user.UserListener;
import com.socialize.ui.profile.UserProfile;

public class MockUserSystem implements UserSystem {
	
	private User user;
	
	public MockUserSystem() {
		super();
		this.user = new User();
	}
	
	@Override
	public SocializeSession authenticateSynchronous(Context context, String consumerKey, String consumerSecret, SocializeSessionConsumer sessionConsumer) throws SocializeException {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		
		if(sessionConsumer != null)	sessionConsumer.setSession(mockSocializeSession);
	
		return mockSocializeSession;
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		if(sessionConsumer != null)	sessionConsumer.setSession(mockSocializeSession);
		listener.onAuthSuccess(mockSocializeSession);
	}

	@Override
	public void authenticate(Context context, String consumerKey, String consumerSecret, AuthProviderData authProviderData, SocializeAuthListener listener, SocializeSessionConsumer sessionConsumer, boolean do3rdPartyAuth) {
		MockSocializeSession mockSocializeSession = new MockSocializeSession();
		if(sessionConsumer != null)	sessionConsumer.setSession(mockSocializeSession);
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

	@Override
	public void saveSession(Context context, SocializeSession session) {}

	public void setUser(User user) {
		this.user = user;
	}
}
