/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.test;

import android.content.Context;
import android.content.pm.PackageManager;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.util.DeviceUtils;
import com.socialize.util.HttpUtils;


/**
 * @author jasonpolites
 *
 */
public class SocializeUtilsTest extends SocializeActivityTest {

	@UsesMocks({MockContext.class, MockPackageManager.class})
	public void testDeviceUtilsHasPermission() {

		Context mockContext = AndroidMock.createMock(MockContext.class);		
		PackageManager mockManager = AndroidMock.createMock(MockPackageManager.class);	
		
		final String permission = "foo";
		final String packageName = "bar";
		
		AndroidMock.expect(mockContext.getPackageName()).andReturn(packageName);
		AndroidMock.expect(mockContext.getPackageManager()).andReturn(mockManager);
		AndroidMock.expect(mockManager.checkPermission(permission, packageName)).andReturn(PackageManager.PERMISSION_GRANTED);
		
		AndroidMock.replay(mockContext);
		AndroidMock.replay(mockManager);
		
		DeviceUtils.hasPermission(mockContext, permission);
		
		AndroidMock.verify(mockContext);
		AndroidMock.verify(mockManager);
	}
	
	// UNABLE TO TEST!  Can't mock the TelephonyManager.. damn Android!
	
//	@UsesMocks({MockContext.class, MockPackageManager.class, TelephonyManager.class})
//	public void testDeviceUtilsGetUDID() {
//		Context mockContext = AndroidMock.createMock(MockContext.class);		
//		PackageManager mockManager = AndroidMock.createMock(MockPackageManager.class);	
//
//		final String packageName = "bar";
//		final String deviceId = "foobar";
//		
//		Class<?> cls = TelephonyManager.class;
//		Constructor<?> constructor = cls.getDeclaredConstructors()[0];
//	    constructor.setAccessible(true);
//	    
//	    TelephonyManager telephonyManager = AndroidMock.createMock(TelephonyManager.class);	
//		
////		MockTelephonyManager telephonyManager = new MockTelephonyManager() {
////			@Override
////			public String getDeviceId() {
////				return deviceId;
////			}
////		};
//		
//		AndroidMock.expect(mockContext.getPackageName()).andReturn(packageName);
//		AndroidMock.expect(mockContext.getPackageManager()).andReturn(mockManager);
//		AndroidMock.expect(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).andReturn(telephonyManager);
//		AndroidMock.expect(mockManager.checkPermission(permission.READ_PHONE_STATE, packageName)).andReturn(PackageManager.PERMISSION_GRANTED);
//		AndroidMock.expect(telephonyManager.getDeviceId()).andReturn(deviceId);
//		
//		
//		AndroidMock.replay(mockContext);
//		AndroidMock.replay(mockManager);
//		
//		assertEquals(deviceId, DeviceUtils.getUDID(mockContext));
//		
//		AndroidMock.verify(mockContext);
//		AndroidMock.verify(mockManager);
//		AndroidMock.verify(telephonyManager);
//	}
	
	
	public void testHttpStatusCodes() {
		
		// Just test that the codes are loaded.  We assume the messages are correct
		
		int[] codes = {
				100,
				101,
				200,
				201,
				202,
				203,
				204,
				205,
				206,
				300,
				301,
				302,
				303,
				304,
				305,
				307,
				400,
				401,
				402,
				403,
				404,
				405,
				406,
				407,
				408,
				409,
				410,
				411,
				412,
				413,
				414,
				415,
				416,
				417,
				500,
				501,
				502,
				503,
				504,
				505};
		
		for (int i : codes) {
			assertNotNull(HttpUtils.getMessageFor(i));
		}
	}
}
