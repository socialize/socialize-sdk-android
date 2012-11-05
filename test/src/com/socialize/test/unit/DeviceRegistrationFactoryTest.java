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
package com.socialize.test.unit;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.DeviceRegistration;
import com.socialize.entity.DeviceRegistrationFactory;
import com.socialize.test.SocializeUnitTest;


/**
 * @author Jason Polites
 *
 */
public class DeviceRegistrationFactoryTest extends SocializeUnitTest {

	@UsesMocks ({JSONObject.class, DeviceRegistration.class})
	public void testToJSON() throws Exception {
		
		final String regId = "foobar";
		
		DeviceRegistration reg = AndroidMock.createMock(DeviceRegistration.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		
		AndroidMock.expect(reg.getRegistrationId()).andReturn(regId);
		AndroidMock.expect(json.put("gcm_registration_id", "foobar")).andReturn(json);
		AndroidMock.expect(json.put("device_type", "Android")).andReturn(json);
		AndroidMock.expect(json.put("service_type", "ANDROID_GCM")).andReturn(json);
		
		AndroidMock.replay(reg, json);
		
		PublicDeviceRegistrationFactory factory = new PublicDeviceRegistrationFactory();
		factory.postToJSON(reg, json);
	
		AndroidMock.verify(reg, json);
	}
	
	class PublicDeviceRegistrationFactory extends DeviceRegistrationFactory {

		@Override
		public void postToJSON(DeviceRegistration from, JSONObject to) throws JSONException {
			super.postToJSON(from, to);
		}
		
	}
	
}
