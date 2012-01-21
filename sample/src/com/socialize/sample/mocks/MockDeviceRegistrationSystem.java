package com.socialize.sample.mocks;

import com.socialize.api.DeviceRegistrationSystem;
import com.socialize.api.SocializeSession;
import com.socialize.entity.DeviceRegistration;
import com.socialize.error.SocializeException;

public class MockDeviceRegistrationSystem implements DeviceRegistrationSystem {

	@Override
	public void registerDevice(SocializeSession session, DeviceRegistration registration) throws SocializeException {}

}
