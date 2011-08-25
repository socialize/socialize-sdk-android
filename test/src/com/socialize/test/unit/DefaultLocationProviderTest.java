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
package com.socialize.test.unit;

import android.location.Criteria;
import android.location.Location;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.location.DefaultLocationProvider;
import com.socialize.location.SocializeLocationListener;
import com.socialize.location.SocializeLocationManager;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	DeviceUtils.class, 
	MockContext.class, 
	SocializeLocationManager.class, 
	SocializeLocationListener.class,
	Location.class,
	IBeanFactory.class})
public class DefaultLocationProviderTest extends SocializeUnitTest {

	DeviceUtils deviceUtils;
	MockContext context;
	SocializeLocationManager locationManager;
	IBeanFactory<SocializeLocationListener> locationListenerFactory;
	SocializeLocationListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		context = AndroidMock.createMock(MockContext.class);
		locationManager = AndroidMock.createMock(SocializeLocationManager.class, deviceUtils);
		locationListenerFactory = AndroidMock.createMock(IBeanFactory.class);
		listener = AndroidMock.createMock(SocializeLocationListener.class);
	}

	public void testDoesNotHavePermission() {
		
		AndroidMock.expect(deviceUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")).andReturn(false);
		AndroidMock.expect(deviceUtils.hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION")).andReturn(false);
		
		AndroidMock.replay(deviceUtils);
		
		DefaultLocationProvider provider = new DefaultLocationProvider(context);
		provider.setDeviceUtils(deviceUtils);
		
		assertNull(provider.getLocation());
		
		AndroidMock.verify(deviceUtils);
	}
	
	public void testLastKnownLocationAvaiable() {
		
		final String strProvider = "foobar";
		
		Location location = AndroidMock.createMock(Location.class, strProvider);
		
		AndroidMock.expect(deviceUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")).andReturn(true);
		AndroidMock.expect(locationManager.getBestProvider((Criteria)AndroidMock.anyObject(), AndroidMock.eq(true))).andReturn(strProvider);
		AndroidMock.expect(locationManager.getLastKnownLocation(strProvider)).andReturn(location);
		
		DefaultLocationProvider provider = new DefaultLocationProvider(context);
		provider.setLocationManager(locationManager);
		provider.setDeviceUtils(deviceUtils);
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(locationManager);
		
		Location loc = provider.getLocation();
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(locationManager);
		
		assertNotNull(loc);
		assertSame(location, loc);
	}
	
	
	public void testLastKnownLocationNotAvailable() {
		
		final String strProvider = "foobar";
		
		AndroidMock.expect(deviceUtils.hasPermission(context, "android.permission.ACCESS_FINE_LOCATION")).andReturn(true);
		AndroidMock.expect(locationManager.getBestProvider((Criteria)AndroidMock.anyObject(), AndroidMock.eq(true))).andReturn(strProvider);
		AndroidMock.expect(locationManager.getLastKnownLocation(strProvider)).andReturn(null);
		AndroidMock.expect(locationManager.isProviderEnabled(strProvider)).andReturn(true);
		AndroidMock.expect(locationListenerFactory.getBean()).andReturn(listener);

		locationManager.requestLocationUpdates(strProvider,1L, 0.0f, listener);
		
		DefaultLocationProvider provider = new DefaultLocationProvider(context);
		provider.setLocationManager(locationManager);
		provider.setDeviceUtils(deviceUtils);
		provider.setLocationListenerFactory(locationListenerFactory);
		
		AndroidMock.replay(locationListenerFactory);
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(locationManager);
		
		Location loc = provider.getLocation();
		
		AndroidMock.verify(locationListenerFactory);
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(locationManager);
		
		assertNull(loc);
	}
}
