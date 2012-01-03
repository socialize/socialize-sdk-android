package com.socialize.sample.mocks;

import android.content.Context;

import com.socialize.notifications.NotificationRegistrationSystem;

public class MockNotificationRegistrationSystem implements NotificationRegistrationSystem {

	@Override
	public boolean isRegisteredC2DM() {
		return true;
	}

	@Override
	public boolean isRegisteredSocialize() {
		return true;
	}

	@Override
	public void registerC2DM(Context context) {}

	@Override
	public void registerSocialize(Context context, String registrationId) {}

}
