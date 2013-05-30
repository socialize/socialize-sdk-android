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
package com.socialize.demo.implementations.location;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.socialize.LocationUtils;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * @author Jason Polites
 *
 */
public class LocationActivity extends DemoActivity {

	@Override
	protected void onCreate() {

		setContentView(R.layout.location);

		Location lastKnownLocation = LocationUtils.getLastKnownLocation(this);
		TextView text = (TextView) findViewById(R.id.txtLocation);
		Button btnLocation = (Button) findViewById(R.id.btnLocation);
		
		
		Address geoCoded = null;
		if(lastKnownLocation != null) {
			try {
				geoCoded = geoCode(lastKnownLocation);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(geoCoded != null) {
			text.setText(geoCoded.getLocality());
		}
		else {
			text.setText("Unknown");
		}
		
		btnLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationUtils.updateLocation(LocationActivity.this);
				Toast.makeText(LocationActivity.this, "Update requested", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public Address geoCode(Location location) throws IOException  {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> fromLocation = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		if(fromLocation != null && fromLocation.size() > 0) {
			return fromLocation.get(0);
		}
		return null;
	}	
}
