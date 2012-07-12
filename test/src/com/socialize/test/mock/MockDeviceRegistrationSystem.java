package com.socialize.test.mock;

import com.socialize.api.DeviceRegistrationListener;
import com.socialize.api.DeviceRegistrationSystem;
import com.socialize.api.SocializeSession;
import com.socialize.entity.DeviceRegistration;

public class MockDeviceRegistrationSystem implements DeviceRegistrationSystem {

	@Override
	public void registerDevice(SocializeSession session, DeviceRegistration registration, DeviceRegistrationListener listener){}

}
