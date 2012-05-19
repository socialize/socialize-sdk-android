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

import android.location.Location;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.location.SocializeLocationListener;
import com.socialize.location.SocializeLocationManager;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.mock.MockDefaultLocationProvider;
import com.socialize.util.AppUtils;

/**
 * @author Jason Polites
 */
public class SocializeLocationListenerTest extends SocializeActivityTest {

	@UsesMocks({ MockDefaultLocationProvider.class, Location.class, SocializeLocationManager.class, AppUtils.class })
	public void testLocationChangeSetsLocation() {

		String providerStr = "foobar";

		MockDefaultLocationProvider provider = AndroidMock.createMock(MockDefaultLocationProvider.class);
		Location location = AndroidMock.createMock(Location.class, providerStr);
		AppUtils utils = AndroidMock.createMock(AppUtils.class);
		SocializeLocationManager manager = AndroidMock.createMock(SocializeLocationManager.class, utils);

		SocializeLocationListener listener = new SocializeLocationListener();
		listener.setLocationProvider(provider);

		AndroidMock.expect(provider.getLocationManager()).andReturn(manager);
		manager.removeUpdates(listener);
		provider.setLocation(location);

		AndroidMock.replay(provider);
		AndroidMock.replay(manager);

		listener.onLocationChanged(location);

		AndroidMock.verify(provider);
		AndroidMock.verify(manager);
	}

	// Just test that other methods don't fail
	public void testLocationListenerMethods() {
		SocializeLocationListener listener = new SocializeLocationListener();
		listener.onProviderEnabled(null);
		listener.onProviderDisabled(null);
		listener.onStatusChanged(null, 0, null);
	}
}
