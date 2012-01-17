package com.socialize.sample.mocks;

import android.content.Context;

import com.socialize.api.SocializeSession;
import com.socialize.entity.User;
import com.socialize.notifications.NotificationRegistrationSystem;

public class MockNotificationRegistrationSystem implements NotificationRegistrationSystem {

	@Override
	public boolean isRegisteredC2DM() {
		return true;
	}

	@Override
	public boolean isRegisteredSocialize(User user) {
		return true;
	}

	@Override
	public void registerC2DM(Context context) {}
	
	
	@Override
	public boolean isRegistrationPending() {
		return false;
		
	}

	@Override
	public void registerC2DMFailed(Context context, String cause) {}

	@Override
	public void registerSocialize(Context context, SocializeSession session, String registrationId) {}

}
