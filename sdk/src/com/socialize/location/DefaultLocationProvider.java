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
package com.socialize.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class DefaultLocationProvider implements SocializeLocationProvider {

	private DeviceUtils deviceUtils;
	private Location location;
	private Context context;
	
	public DefaultLocationProvider(Context context, DeviceUtils deviceUtils) {
		super();
		this.deviceUtils = deviceUtils;
		this.context = context;
	}
	
	public void init() {
		getLocation();
	}

	@Override
	public Location getLocation() {

		if(location == null) {
			if(deviceUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")) {
				requestLocation(context, Criteria.ACCURACY_FINE);
			}
			else if(deviceUtils.hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
				requestLocation(context, Criteria.ACCURACY_COARSE);
			}
		}

		return location;
	}

	private void requestLocation(Context context, int accuracy) {
		final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(accuracy);
		String provider = lm.getBestProvider(criteria, true);
		
		if(!StringUtils.isEmpty(provider)) {
			Location mostRecentLocation = lm.getLastKnownLocation(provider);
			
			if(mostRecentLocation != null) {
				location = mostRecentLocation;
			}
			else if(lm.isProviderEnabled(provider)) {
				
				lm.requestLocationUpdates(provider, 1, 0, new LocationListener() {
					
					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {}
					
					@Override
					public void onProviderEnabled(String provider) {}
					
					@Override
					public void onProviderDisabled(String provider) {}
					
					@Override
					public void onLocationChanged(Location location) {
						DefaultLocationProvider.this.location = location;
						
						// Auto remove
						lm.removeUpdates(this);
					}
				});
			}
		}
	}

	public DeviceUtils getDeviceUtils() {
		return deviceUtils;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
}
