package com.socialize.test.mock;

import android.content.Context;
import com.socialize.entity.User;
import com.socialize.notifications.NotificationRegistrationSystem;

public class MockNotificationRegistrationSystem implements NotificationRegistrationSystem {

	@Override
	public boolean isRegisteredC2DM(Context context) {
		return true;
	}

	@Override
	public boolean isSocializeRegistrationPending() {
		return false;
	}

	@Override
	public boolean isRegisteredSocialize(Context context, User user) {
		return true;
	}

	@Override
	public void registerC2DMAsync(Context context) {
	}

	@Override
	public void registerC2DM(Context context) {}
	
	
	@Override
	public boolean isRegistrationPending() {
		return false;
	}
	
	@Override
	public void registerSocialize(Context context, String registrationId) {
	}

	@Override
	public void registerC2DMFailed(Context context, String cause) {}
}
