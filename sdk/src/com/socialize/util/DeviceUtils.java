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
package com.socialize.util;

import android.Manifest.permission;
import android.content.Context;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public class DeviceUtils {

	private SocializeLogger logger;
	
	private AppUtils appUtils;
	
	private boolean hasCamera;
	
	private boolean deviceIdObtained = false;
	private String deviceId;

	public void init(Context context) {
		if(appUtils != null) {
			hasCamera = appUtils.isIntentAvailable(context, MediaStore.ACTION_IMAGE_CAPTURE);
		}
	}

	public String getAndroidID(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}
	
	public String getUDID(Context context) {
		
		if(!deviceIdObtained) {
			if (appUtils.hasPermission(context, permission.READ_PHONE_STATE)) {
				TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				deviceId = tManager.getDeviceId();
			}
			
			if (StringUtils.isEmpty(deviceId)) {
				if (logger != null) {
					logger.warn("Unable to determine device UDID, reverting to " + Secure.ANDROID_ID);
				}
				deviceId = getAndroidID(context);
			}
			
			if (StringUtils.isEmpty(deviceId)) {
				// this is fatal
				if (logger != null) {
					logger.error(SocializeLogger.NO_UDID);
				}
			}
			
			deviceIdObtained = true;
		}
		
		return deviceId;
	}
	
	
	public boolean hasCamera() {
		return hasCamera;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
}
