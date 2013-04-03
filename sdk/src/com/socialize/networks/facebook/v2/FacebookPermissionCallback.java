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
package com.socialize.networks.facebook.v2;

import com.socialize.error.SocializeException;
import com.socialize.facebook.AsyncFacebookRunner.RequestListener;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.SocializeListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author Jason Polites
 */
@Deprecated
public abstract class FacebookPermissionCallback implements SocializeListener, RequestListener {
	
	public abstract void onSuccess(String[] permissions);

	@Override
	public void onComplete(String response, Object state) {
		try {
			JSONObject json = new JSONObject(response);
			
			if(json.has("data") && !json.isNull("data")) {
				JSONObject data = json.getJSONArray("data").getJSONObject(0);
				
				// Permissions are keys
				JSONArray names = data.names();
				
				ArrayList<String> current = new ArrayList<String>();
				
				for (int i = 0; i < names.length(); i++) {
					
					// If we're ON we're on
					String permission = names.getString(i);
					int status = data.getInt(permission);
					
					if(status == 1) {
						// ON
						current.add(permission);
					}
				}
				
				String[] values = current.toArray(new String[current.size()]);
				
				Arrays.sort(values); // Sort for binary searching
				
				onSuccess(values);
			}
		}
		catch (JSONException e) {
			onError(new SocializeException(e));
		}
	}

	@Override
	public void onIOException(IOException e, Object state) {
		onError(new SocializeException(e));
	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e, Object state) {
		onError(new SocializeException(e));
	}

	@Override
	public void onMalformedURLException(MalformedURLException e, Object state) {
		onError(new SocializeException(e));
	}

	@Override
	public void onFacebookError(FacebookError e, Object state) {
		onError(new SocializeException(e));
	}
}
