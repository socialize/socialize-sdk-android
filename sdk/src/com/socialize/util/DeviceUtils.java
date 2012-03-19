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
package com.socialize.util;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public class DeviceUtils {

	private SocializeLogger logger;
	
	private AppUtils appUtils;
	
	private float density = 1f;
	private boolean hasCamera;
	private int orientation;
	private int displayHeight;
	private int displayWidth;
	
	private boolean deviceIdObtained = false;
	private String deviceId;

	public void init(Context context) {
	
		if (context instanceof Activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			display.getMetrics(metrics);
			density = metrics.density;
			
			displayHeight = display.getHeight();
			displayWidth = display.getWidth();
			
			if (displayWidth == displayHeight) {
				orientation = Configuration.ORIENTATION_SQUARE;
			} 
			else { 
				if (displayWidth < displayHeight) {
					orientation = Configuration.ORIENTATION_PORTRAIT;
				} 
				else { 
					orientation = Configuration.ORIENTATION_LANDSCAPE;
				}
			}
		}
		
		if(appUtils != null) {
			hasCamera = appUtils.isIntentAvailable(context, MediaStore.ACTION_IMAGE_CAPTURE);
		}
	}

	/**
	 * 
	 * @param context
	 * @param activity
	 * @return
	 * @deprecated use AppUtils
	 */
	@Deprecated
	public boolean isActivityAvailable(Context context, Class<?> activity) {
		return appUtils.isActivityAvailable(context, activity);
	}
	
	/**
	 * 
	 * @param context
	 * @param action
	 * @return
	 * @deprecated use AppUtils
	 */
	@Deprecated
	public boolean isIntentAvailable(Context context, String action) {
		return appUtils.isIntentAvailable(context, action);
	}

	@Deprecated
	public int getDIP(float pixels) {
		return getDIP(Math.round(pixels));
	}
	
	@Deprecated
	public int getDIP(int pixels) {
		if (pixels != 0) {
			return (int) ((float) pixels * density);
		}
		return pixels;
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
				deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
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
	
	/**
	 * Returns the Android market url for this app.
	 * @param http If true an HTTP link is returned, otherwise a market:// link is returned.
	 * @return The HTTP url for the app on the Android Market
	 * @deprecated use AppUtils
	 */
	@Deprecated
	public String getMarketUrl(boolean http) {
		return appUtils.getMarketUrl();
	}
	
	/**
	 * @deprecated use AppUtils
	 * @return The package name of the app.
	 */
	@Deprecated
	public String getPackageName() {
		return appUtils.getPackageName();
	}

	/**
	 * @deprecated use AppUtils
	 * @return The name of the app, as defined in the AndroidManifest.xml
	 */
	@Deprecated
	public String getAppName() {
		return appUtils.getAppName();
	}

	/**
	 * @deprecated use AppUtils
	 * @return
	 */
	@Deprecated
	public String getUserAgentString() {
		return appUtils.getUserAgentString();
	}

	/**
	 * @deprecated use AppUtils
	 * @param context
	 * @param permission
	 * @return true if the application has the given permission
	 */
	@Deprecated
	public boolean hasPermission(Context context, String permission) {
		return appUtils.hasPermission(context, permission);
	}

	/**
	 * @deprecated use AppUtils
	 * @param context
	 * @return true if location is available.
	 */
	@Deprecated
	public boolean isLocationAvaiable(Context context) {
		return appUtils.isLocationAvaiable(context);
	}
	
	public boolean hasCamera() {
		return hasCamera;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	@Deprecated
	public int getDisplayHeight() {
		return displayHeight;
	}

	@Deprecated
	public int getDisplayWidth() {
		return displayWidth;
	}

	@Deprecated
	public int getOrientation() {
		return orientation;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}
}
