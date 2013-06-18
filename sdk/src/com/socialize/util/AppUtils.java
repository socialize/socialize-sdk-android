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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;

/**
 * @author Jason Polites
 *
 */
public interface AppUtils {

	public boolean isActivityAvailable(Context context, String activityClassName);
	
	public boolean isActivityAvailable(Context context, Class<?> activity);

	public boolean isIntentAvailable(Context context, String action);

	public boolean isIntentAvailable(Context context, Intent intent);

	public boolean isServiceAvailable(Context context, Class<?> cls);
	
	public ActivityInfo getActivityInfo(Context context, Class<?> cls);

	public boolean isReceiverAvailable(Context context, Class<?> cls);

	public boolean isLocationAvailable(Context context);

	public boolean isNotificationsAvailable(Context context);

	public boolean hasPermission(Context context, String permission);

	public String getAppName();
	
	public String getCountry();

	public String getCarrier();

	public String getNetworkType();
	
	public int getAppIconId(Context context);

	public String getPackageName();

	public String getAppStoreAbbreviation(String appStore);
	
	public String getUserAgentString();
	
	public void checkAndroidManifest(Context context);

	/**
	 * Called by Socialize during activity resume
	 * @param context
	 */
	public void onResume(Context context);
}