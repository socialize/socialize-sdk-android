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

import com.socialize.util.DeviceUtils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Just provides a wrapper around LocationManager because LocationManager cannot be mocked :/
 * @author Jason Polites
 */
public class SocializeLocationManager {
	LocationManager lm = null;
	
	private DeviceUtils deviceUtils;
	
	public SocializeLocationManager(DeviceUtils deviceUtils) {
		super();
		this.deviceUtils = deviceUtils;
	}
	
	public void init(Context context) {
		if(deviceUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION") || deviceUtils.hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION")) {
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}
	}

	public String getBestProvider(Criteria criteria, boolean enabledOnly) {
		return (lm == null) ? null : lm.getBestProvider(criteria, enabledOnly);
	}
	
	public Location getLastKnownLocation(String provider) {
		return (lm == null) ? null : lm.getLastKnownLocation(provider);
	}
	
	public boolean isProviderEnabled(String provider) {
		return (lm == null) ? false : lm.isProviderEnabled(provider);
	}
	
	public void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener) {
		if(lm != null) lm.requestLocationUpdates(provider, minTime, minDistance, listener);
	}

	public void removeUpdates(LocationListener listener) {
		if(lm != null) lm.removeUpdates(listener);
	}
}
