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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.config.SocializeConfig;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.ui.action.ActionDetailActivity;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.profile.ProfileActivity;

import java.util.List;
import java.util.Locale;

/**
 * @author Jason Polites
 */
public class DefaultAppUtils implements AppUtils {
	
	private String packageName;
	private String appName;
	private String userAgent;
	private String country;
	private String carrier = "unknown";
	private String networkType = "unknown";
	private SocializeLogger logger;
	private SocializeConfig config;
	
	private boolean locationAvailable = false;
	private boolean locationAssessed = false;
	
	private boolean notificationsAvailable = false;
	private boolean notificationsAssessed = false;
	
	private String lastNotificationWarning = null;
	
	public void init(Context context) {

		packageName = context.getPackageName();
		
		// Try to get the app name 
		try {
			PackageManager pkgManager = context.getPackageManager();
			appName = pkgManager.getApplicationLabel(pkgManager.getApplicationInfo(packageName, 0)).toString();
		}
		catch (Exception e) {
			String msg = "Failed to lookup application label.  Make sure this is specified in your AndroidManifest.xml";
			
			if(logger != null) {
				logger.error(msg, e);
			}
			else {
				SocializeLogger.e(msg, e);
			}
		}

		if(StringUtils.isEmpty(appName)) {
			appName = packageName;
		}
		
		if(StringUtils.isEmpty(appName)) {
			appName = "A Socialize enabled app";
		}		
		
		try {

			if(hasPermission(context, "android.permission.READ_PHONE_STATE")) {
				TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				country = manager.getNetworkCountryIso();
				carrier = manager.getNetworkOperatorName();

				if(StringUtils.isEmpty(carrier)) {
					carrier = "unknown";
				}
			}
		}
		catch (Exception ignore) {}

		if(StringUtils.isEmpty(country)) {
			country = Locale.getDefault().getCountry();
		}
	}

	@Override
	public void onResume(Context context) {
		determineNetworkType(context);
	}

	private void determineNetworkType(Context context) {
		try {
			if(hasPermission(context, "android.permission.ACCESS_NETWORK_STATE")) {
				ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if(mWifi != null && mWifi.isConnected()) {
					networkType = "wifi";
				}
				else {
					networkType = "cell";
				}
			}
		}
		catch (Exception ignore) {}
	}
	
	@Override
	public String getAppStoreAbbreviation(String appStore) {
		if(appStore != null && appStore.equalsIgnoreCase("amazon")) {
			return "amz";
		}
		return null;
	}
	
	@Override
	public boolean isActivityAvailable(Context context, String activityClassName) {
		try {
			Class<?> activityClass = Class.forName(activityClassName);
			return isActivityAvailable(context, activityClass);
		}
		catch (ClassNotFoundException ignored) {}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isActivityAvailable(android.content.Context, java.lang.Class)
	 */
	@Override
	public boolean isActivityAvailable(Context context, Class<?> activity) {
		Intent intent = new Intent(context, activity);
		return isIntentAvailable(context, intent);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isIntentAvailable(android.content.Context, java.lang.String)
	 */
	@Override
	public boolean isIntentAvailable(Context context, String action) {
		Intent intent = new Intent(action);
		return isIntentAvailable(context, intent);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isIntentAvailable(android.content.Context, android.content.Intent)
	 */
	@Override
	public boolean isIntentAvailable(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}	
	
	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isServiceAvailable(android.content.Context, java.lang.Class)
	 */
	@Override
	public boolean isServiceAvailable(Context context, Class<?> cls) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(context, cls);
	    return packageManager.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}
	
	@Override
	public ActivityInfo getActivityInfo(Context context, Class<?> cls) {
		try {
			final PackageManager packageManager = context.getPackageManager();
			return packageManager.getActivityInfo(new ComponentName(context, cls), 0);
		}
		catch (NameNotFoundException e) {
			if(logger != null) {
				logger.error("Failed to locate info for activity [" +
						cls.getName() +
						"]", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isReceiverAvailable(android.content.Context, java.lang.Class)
	 */
	@Override
	public boolean isReceiverAvailable(Context context, Class<?> cls) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(context, cls);
	    return packageManager.queryBroadcastReceivers(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
	}	
	
	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isLocationAvaiable(android.content.Context)
	 */
	@Override
	public boolean isLocationAvailable(Context context) {
		if(!locationAssessed && config != null) {
			locationAvailable = config.getBooleanProperty(SocializeConfig.SOCIALIZE_LOCATION_ENABLED, true) && (hasPermission(context, "android.permission.ACCESS_FINE_LOCATION") || hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION"));
			locationAssessed = true;
		}
		return locationAvailable;
	}

	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#isNotificationsAvaiable(android.content.Context)
	 */
	@Override
	public boolean isNotificationsAvailable(Context context) {

		if(!notificationsAssessed) {
			
		    String permissionString = context.getPackageName() + ".permission.C2D_MESSAGE";
		    
			boolean ok = true;
			
			if(config.getBooleanProperty(SocializeConfig.SOCIALIZE_NOTIFICATIONS_ENABLED, true)) {
				if(!hasPermission(context, permissionString)) {
					lastNotificationWarning = "Notifications not available, permission [" + permissionString + "] not specified in AndroidManifest.xml";
					if(logger.isInfoEnabled()) logger.info(lastNotificationWarning);
					ok = false;
				}
				
				if(!hasPermission(context, "com.google.android.c2dm.permission.RECEIVE")) {
					lastNotificationWarning = "Notifications not available, permission com.google.android.c2dm.permission.RECEIVE not specified in AndroidManifest.xml, or device does not include Google APIs";
					if(logger.isInfoEnabled()) logger.info(lastNotificationWarning);
					ok = false;
				}
				
				if(config.isEntityLoaderCheckEnabled() && getSocialize().getEntityLoader() == null) {
					lastNotificationWarning = "Notifications not available. Entity loader not found.";
					if(logger.isInfoEnabled()) logger.info(lastNotificationWarning);
					ok = false;
				}
			}
			else {
				ok = false;
				if(logger.isDebugEnabled()) {
					logger.debug("SmartAlerts disabled in config");
				}
			}
			
			notificationsAvailable = ok;
			notificationsAssessed = true;
		}
		else if(!notificationsAvailable) {
			if(lastNotificationWarning != null && logger.isInfoEnabled()) {
				logger.info(lastNotificationWarning);
			}
		}

		return notificationsAvailable;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	@Override
	public void checkAndroidManifest(Context context) {
		// Check the launch activity config
		checkActivitiesExist(context, CommentActivity.class, ActionDetailActivity.class, ProfileActivity.class, FacebookActivity.class, SocializeLaunchActivity.class);
		
		ActivityInfo info = getActivityInfo(context, SocializeLaunchActivity.class);
		if(info != null) {
			if((info.flags & ActivityInfo.FLAG_NO_HISTORY) != ActivityInfo.FLAG_NO_HISTORY) {
				logger.warn("Activity flag android:noHistory=\"true\" not found for " + SocializeLaunchActivity.class.getSimpleName() + ".  Please ensure this is added to the declaration of this activity in your AndroidManifest.xml");
			}
			
			
			if((info.launchMode & ActivityInfo.LAUNCH_SINGLE_TOP) == ActivityInfo.LAUNCH_SINGLE_TOP) {
				logger.warn("Activity flag android:launchMode=\"singleTop\" found for " + SocializeLaunchActivity.class.getSimpleName() + ".  This should be removed from the declaration of this activity in your AndroidManifest.xml");
			}
		}	
		
		info = getActivityInfo(context, CommentActivity.class);
		
		if(info != null) {
			if((info.configChanges & ActivityInfo.CONFIG_ORIENTATION) != ActivityInfo.CONFIG_ORIENTATION) {
				logger.warn("Activity flag android:configChanges=\"orientation|keyboardHidden|screenSize\" not found for " + CommentActivity.class.getSimpleName() + ".  Please ensure this is added to the declaration of this activity in your AndroidManifest.xml");
			}
		}
	}
	
	protected void checkActivitiesExist(Context context, Class<?>...classes) {
		for (Class<?> cls : classes) {
			ActivityInfo info = getActivityInfo(context, cls);
			
			if(info == null) {
				logger.warn("No activity element declared for [" +
						cls.getName() +
						"].  Please ensure you have included this in your AndroidManifest.xml");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#hasPermission(android.content.Context, java.lang.String)
	 */
	@Override
	public boolean hasPermission(Context context, String permission) {
		return context.getPackageManager().checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
	}	
	
	public static boolean launchMainApp(Activity origin) {
		Intent mainIntent = getMainAppIntent(origin);
		if(mainIntent != null) {
			origin.startActivity(mainIntent);	
			return true;
		}
		return false;
	}
	
	public static Intent getMainAppIntent(Context context) {
		PackageManager pm = context.getPackageManager();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.setPackage(context.getPackageName());

		List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
		
		if(appList != null && appList.size() > 0) {
			ResolveInfo resolveInfo = appList.get(0);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mainIntent.setComponent(new ComponentName(resolveInfo.activityInfo.applicationInfo.packageName, resolveInfo.activityInfo.name));
			return mainIntent;
		}
		
		return null;
	}
	
	/**
	 * Attempts to get the resource if for the app icon.
	 * @param context
	 * @return
	 */
	public int getAppIconId(Context context) {
		ApplicationInfo applicationInfo = context.getApplicationInfo();
		return applicationInfo.icon;
	}
	
	public String getUserAgentString() {
		if (userAgent == null) {
			userAgent = "Android-" + android.os.Build.VERSION.SDK_INT + "/" + android.os.Build.MODEL + " SocializeSDK/v" + Socialize.VERSION + "; " + Locale.getDefault().getLanguage() + "_"
					+ getCountry() + "; BundleID/" + getPackageName() + "; Carrier/" + carrier + ";";
		}

		// Always add the network state in case it changed
		return userAgent + " Network/" + networkType + ";";
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.util.IAppUtils#getAppName()
	 */
	@Override
	public String getAppName() {
		return appName;
	}
	
	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getCarrier() {
		return carrier;
	}

	@Override
	public String getNetworkType() {
		return networkType;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	void setNotificationsAssessed(boolean notificationsAssessed) {
		this.notificationsAssessed = notificationsAssessed;
	}
}
