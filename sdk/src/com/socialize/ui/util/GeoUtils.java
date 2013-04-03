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
package com.socialize.ui.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author Jason Polites
 */
public class GeoUtils {

	private Geocoder geocoder;
	private SocializeLogger logger;
	
	public void init(Context context) {
		geocoder = new Geocoder(context, Locale.getDefault());
	}
	
	public Address geoCode(double lat, double lng)  {
		try {
			List<Address> fromLocation = geocoder.getFromLocation(lat, lng, 1);
			
			if(fromLocation != null && fromLocation.size() > 0) {
				return fromLocation.get(0);
			}
		}
		catch (IOException e) {
			if(logger != null) {
				logger.error("Error geocoding location [" +
						lat +
						"," +
						lng +
						"]", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}
		}

		return null;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	/**
	 * Returns a simple one-line address based on city and country
	 * @param address
	 * @return
	 */
	public String getSimpleLocation(Address address) {
		StringBuilder builder = new StringBuilder();
		String locality = address.getLocality();
		String countryName = address.getCountryName();
		
		if(!StringUtils.isEmpty(locality)) {
			builder.append(locality);
		}
		else if(!StringUtils.isEmpty(countryName)) {
			builder.append(countryName);
		}
		
		return builder.toString();
	}
	
}
