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
package com.socialize.test.integration.services.b;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import com.socialize.LocationUtils;
import com.socialize.ioc.SocializeIOC;
import com.socialize.location.DefaultLocationProvider;
import com.socialize.location.SocializeLocationManager;
import com.socialize.location.SocializeLocationProvider;
import com.socialize.test.SocializeActivityTest;


/**
 * @author Jason Polites
 *
 */
public class LocationUtilsTest extends SocializeActivityTest {

	public void testGetLocation() {
		
		final Location mockLocation = new Location((String)null);
		mockLocation.setLatitude(69);
		mockLocation.setLongitude(96);
		
		// Stub in location provider and manager
		SocializeLocationManager mockLocationManager = new SocializeLocationManager(null) {

			@Override
			public void init(Context context) {}

			@Override
			public String getBestProvider(Criteria criteria, boolean enabledOnly) {
				return "foobar";
			}

			@Override
			public Location getLastKnownLocation(String provider) {
				return mockLocation;
			}

			@Override
			public boolean isProviderEnabled(String provider) {
				return true;
			}

			@Override
			public void requestLocationUpdates(Activity activity, String provider, long minTime, float minDistance, LocationListener listener) {}

			@Override
			public void removeUpdates(LocationListener listener) {}
		};
		
		SocializeLocationProvider mockLocationProvider = new DefaultLocationProvider() {

			@Override
			public Location getLastKnownLocation() {
				return mockLocation;
			}

			@Override
			public Location getLocation(Context context) {
				return mockLocation;
			}
		};
		
		
		SocializeIOC.registerStub("locationManager", mockLocationManager);
		SocializeIOC.registerStub("locationProvider", mockLocationProvider);
		
		Location location = LocationUtils.getLastKnownLocation(getContext());
		
		SocializeIOC.unregisterStub("locationManager");
		SocializeIOC.unregisterStub("locationProvider");
		
		assertNotNull(location);
		assertEquals(mockLocation.getLongitude(),  location.getLongitude());
		assertEquals(mockLocation.getLatitude(),  location.getLatitude());
	}
	
}
