package com.socialize.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class SocializeLocationListener implements LocationListener {
	
	private DefaultLocationProvider provider;
	
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
		provider.setLocation(location);
		provider.getLocationManager().removeUpdates(this);
	}

	public DefaultLocationProvider getProvider() {
		return provider;
	}

	public void setProvider(DefaultLocationProvider provider) {
		this.provider = provider;
	}
}
