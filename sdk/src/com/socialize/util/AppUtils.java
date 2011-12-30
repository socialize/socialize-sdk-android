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

import com.socialize.log.SocializeLogger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

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
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(context, activity);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
	public boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
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
