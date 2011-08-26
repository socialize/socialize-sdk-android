package com.socialize.sample.mock;

import android.content.Context;
import android.location.Location;

import com.socialize.location.DefaultLocationProvider;
import com.socialize.location.SocializeLocationManager;

public class MockDefaultLocationProvider extends DefaultLocationProvider {
	public MockDefaultLocationProvider(Context context) {
		super(context);
	}
	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
	}
	@Override
	public SocializeLocationManager getLocationManager() {
		return super.getLocationManager();
	}
}