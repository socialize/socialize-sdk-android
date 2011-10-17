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
package com.socialize.test.integration;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.location.SocializeLocationManager;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeLocationManagerTest extends SocializeActivityTest {

	// Can't mock location manager, so just do a full integration test
	@UsesMocks(DeviceUtils.class)
	public void testSocializeLocationManager() {
		
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		
		AndroidMock.expect(deviceUtils.hasPermission((Context)AndroidMock.anyObject(),AndroidMock.eq( "android.permission.ACCESS_FINE_LOCATION"))).andReturn(false);
		AndroidMock.expect(deviceUtils.hasPermission((Context)AndroidMock.anyObject(),AndroidMock.eq( "android.permission.ACCESS_COARSE_LOCATION"))).andReturn(true);
		
		AndroidMock.replay(deviceUtils);
		
		SocializeLocationManager manager = new SocializeLocationManager(deviceUtils);
		manager.init(getActivity());
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		
		String bestProvider = manager.getBestProvider(criteria, true);
		
		assertNotNull(bestProvider);
		
		// May be null, so don't assert result
		manager.getLastKnownLocation(bestProvider);
		
		manager.isProviderEnabled(bestProvider);
		
		LocationListener listener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
			
			@Override
			public void onLocationChanged(Location location) {}
		};
		
		manager.requestLocationUpdates(getActivity(), bestProvider, 0, 0, listener);
		manager.removeUpdates(listener);
		
		AndroidMock.verify(deviceUtils);
	}
	
}
