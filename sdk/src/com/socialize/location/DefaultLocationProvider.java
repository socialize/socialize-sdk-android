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

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class DefaultLocationProvider implements SocializeLocationProvider {

	private DeviceUtils deviceUtils;
	private Location location;
	private Activity context;
	private SocializeLocationManager locationManager;
	private IBeanFactory<SocializeLocationListener> locationListenerFactory;
	private SocializeLocationListener listener = null;
	
	public DefaultLocationProvider() {
		super();
	}
	
	public void init(Activity context) {
		this.context = context;
		listener = locationListenerFactory.getBean();
		getLocation();
	}
	
	public void destroy() {
		if(locationManager != null && listener != null) {
			locationManager.removeUpdates(listener);
		}
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

	private void requestLocation(Activity context, int accuracy) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(accuracy);
		
		String provider = locationManager.getBestProvider(criteria, true);
		
		if(!StringUtils.isEmpty(provider)) {
			Location mostRecentLocation = locationManager.getLastKnownLocation(provider);
			
			if(mostRecentLocation != null) {
				location = mostRecentLocation;
			}
			else if(locationManager.isProviderEnabled(provider)) {
				locationManager.requestLocationUpdates(context, provider, 1, 0, listener);
			}
		}
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setLocationManager(SocializeLocationManager locationManager) {
		this.locationManager = locationManager;
	}

	protected SocializeLocationManager getLocationManager() {
		return locationManager;
	}

	protected void setLocation(Location location) {
		this.location = location;
	}

	public void setLocationListenerFactory(IBeanFactory<SocializeLocationListener> listenerFactory) {
		this.locationListenerFactory = listenerFactory;
	}
}
