package com.socialize.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import com.socialize.log.SocializeLogger;

public class SocializeLocationListener implements LocationListener {
	
	private DefaultLocationProvider locationProvider;
	private SocializeLogger logger;
	
	public SocializeLocationListener() {
		super();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	@Override
	public void onProviderEnabled(String provider) {}
	
	@Override
	public void onProviderDisabled(String provider) {}
	
	@Override
	public void onLocationChanged(Location location) {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("Found location from device, ceasing updates");
		}		
		if(locationProvider != null) {
			locationProvider.setLocation(location);
			locationProvider.getLocationManager().removeUpdates(this);
		}
	}

	public void setLocationProvider(DefaultLocationProvider locationProvider) {
		this.locationProvider = locationProvider;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
