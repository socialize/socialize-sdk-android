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
package com.socialize;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.socialize.location.LocationUtilsProxy;

import java.lang.reflect.Proxy;


/**
 * Provides a simple way to access the device's location.
 * @author Jason Polites
 */
public class LocationUtils {
	
	static LocationUtilsProxy proxy;
	
	static {
		proxy = (LocationUtilsProxy) Proxy.newProxyInstance(
				LocationUtilsProxy.class.getClassLoader(),
				new Class[]{LocationUtilsProxy.class},
				new SocializeActionProxy("locationUtils")); // Bean name
	}
	
	/**
	 * Returns the last known location of the device.  May return null if the location has not yet been obtained from the device.
	 * @param context The current context.
	 * @return The current Location, or null if the location has not been, or cannot be determined.
	 */
	public static Location getLastKnownLocation(Context context) {
		return proxy.getLastKnownLocation(context);
	}
	
	/**
	 * Requests a location update from the device.
	 * @param context The current context.
	 */
	public static void updateLocation(Activity context) {
		proxy.updateLocation(context);
	}
}
