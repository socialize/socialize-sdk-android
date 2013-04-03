/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.api;

import com.socialize.entity.DeviceRegistration;
import com.socialize.error.SocializeException;
import com.socialize.provider.SocializeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Polites
 *
 */
public class SocializeDeviceRegistrationSystem extends SocializeApi<DeviceRegistration, SocializeProvider<DeviceRegistration>> implements DeviceRegistrationSystem {

	public SocializeDeviceRegistrationSystem(SocializeProvider<DeviceRegistration> provider) {
		super(provider);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.DeviceRegistrationSystem#registerDevice(com.socialize.api.SocializeSession, com.socialize.entity.DeviceRegistration)
	 */
	@Override
	public void registerDevice(SocializeSession session, DeviceRegistration registration, DeviceRegistrationListener listener) {
		List<DeviceRegistration> registrations = new ArrayList<DeviceRegistration>(1);
		registrations.add(registration);
		postAsync(session, ENDPOINT, registrations, listener);
	}

	@Override
	public void registerDeviceSynchronous(SocializeSession session, DeviceRegistration registration) throws SocializeException {
		List<DeviceRegistration> registrations = new ArrayList<DeviceRegistration>(1);
		registrations.add(registration);
		post(session, ENDPOINT, registrations);
	}
}
