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

import java.util.Locale;
import android.content.Context;
import android.content.pm.PackageManager;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.AppUtils;
import com.socialize.util.DefaultAppUtils;

/**
 * @author jasonpolites
 * 
 */
public class DeviceUtilsTest extends SocializeActivityTest {

	@UsesMocks({ MockContext.class, MockPackageManager.class })
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

		AppUtils utils = new DefaultAppUtils();

		utils.hasPermission(mockContext, permission);

		AndroidMock.verify(mockContext);
		AndroidMock.verify(mockManager);
	}

	public void testDeviceUtilsUserAgentString() {
		DefaultAppUtils appUtils = new DefaultAppUtils();
		appUtils.init(TestUtils.getActivity(this));
		String userAgentString = appUtils.getUserAgentString();
		String expected = "Android-" + android.os.Build.VERSION.SDK_INT + "/" + android.os.Build.MODEL + " SocializeSDK/v" + Socialize.VERSION + "; " + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry() + "; BundleID/com.socialize.sample;";
		assertEquals(expected, userAgentString);
	}

	// Can't extend TelephonyManager.. so don't bother trying to test. urgh!

}
