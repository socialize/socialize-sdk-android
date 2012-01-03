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

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public class AppUtils {
	
	private String packageName;
	private String appName;
	private SocializeLogger logger;
	
	public void init(Context context) {
		packageName = context.getPackageName();
		
		// Try to get the app name 
		try {
			Resources appR = context.getResources(); 
			CharSequence txt = appR.getText(appR.getIdentifier("app_name",  "string", context.getPackageName())); 
			appName = txt.toString();
		} 
		catch (Exception e) {
			String msg = "Failed to locate app_name String from resources.  Make sure this is specified in your AndroidManifest.xml";
			
			if(logger != null) {
				logger.error(msg, e);
			}
			else {
				System.err.println(msg);
				e.printStackTrace();
			}
		}

		if(StringUtils.isEmpty(appName)) {
			appName = packageName;
		}
		
		if(StringUtils.isEmpty(appName)) {
			appName = "A Socialize enabled app";
		}		
	}
	
	public String getMarketUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append("https://market.android.com/details?id=");
		builder.append(getAppName());
		return builder.toString();
	}
	
	public boolean isActivityAvailable(Context context, Class<?> activity) {
		Intent intent = new Intent(context, activity);
		return isIntentAvailable(context, intent);
	}
	
	public boolean isIntentAvailable(Context context, String action) {
		Intent intent = new Intent(action);
		return isIntentAvailable(context, intent);
	}
	
	public boolean isIntentAvailable(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}	
	
	public boolean isLocationAvaiable(Context context) {
		return hasPermission(context, "android.permission.ACCESS_FINE_LOCATION") || hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION");	
	}
	
	public boolean hasPermission(Context context, String permission) {
		return context.getPackageManager().checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}	
	
	public static boolean launchMainApp(Activity origin) {
		
		PackageManager pm = origin.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(origin.getPackageName());

		List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
		
		if(appList != null && appList.size() > 0) {
			ResolveInfo resolveInfo = appList.get(0);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mainIntent.setComponent(new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name));
			origin.startActivity(mainIntent);	
			return true;
		}
		
		return false;
	}
	
//	/**
//	 * Attempts to get the resource if for the app icon.
//	 * @param context
//	 * @return
//	 */
//	public int getAppIconId(Context context) {
//		int id = -1;
//		String className = context.getPackageName() + ".R.drawable";
//		try {
//			Class<?> cls = Class.forName(className);
//			Field field = cls.getField("icon");
//			if(field != null) {
//				Integer value = (Integer) field.get(null);
//				if(value != null) {
//					id = value;
//				}
//			}
//		} catch (Exception ignore) {}
//		return id;
//	}
	
	public String getAppName() {
		return appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
