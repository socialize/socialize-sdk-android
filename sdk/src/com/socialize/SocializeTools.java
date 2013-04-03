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
import android.content.Intent;
import android.net.Uri;
import com.socialize.tools.SocializeToolsProxy;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.util.GeoUtils;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.util.AppUtils;
import com.socialize.util.DisplayUtils;
import com.socialize.util.IOUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Jason Polites
 */
public class SocializeTools {
	static SocializeToolsProxy proxy;
	
	static {
		proxy = (SocializeToolsProxy) Proxy.newProxyInstance(
				SocializeToolsProxy.class.getClassLoader(),
				new Class[]{SocializeToolsProxy.class},
				new SocializeActionProxy("socializeTools"));	// Bean name
	}
	
	public static ImageLoader getImageLoader(Context context) {
		return proxy.getImageLoader(context);
	}
	
	public static GeoUtils getGeoUtils(Context context) {
		return proxy.getGeoUtils(context);
	}
	
	public static DisplayUtils getDisplayUtils(Context context) {
		return proxy.getDisplayUtils(context);
	}
	
	public static KeyboardUtils getKeyboardUtils(Context context) {
		return proxy.getKeyboardUtils(context);
	}
	
	public static IOUtils getIOUtils(Context context) {
		return proxy.getIOUtils(context);
	}
	
	public static AppUtils getAppUtils(Context context) {
		return proxy.getAppUtils(context);
	}		

	public static boolean sendExternalLogs(Activity context) {
		Set<Uri> urls = proxy.getExternalLogFilePaths(context);
		if(urls != null && urls.size() > 0) {
			ArrayList<Uri> extras = new ArrayList<Uri>(urls);
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
			emailIntent.setType("text/plain");
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, extras);
			context.startActivity(emailIntent);
			return true;
		}
		return false;
	}

	public static void deleteExternalLogs(Context context) {
		proxy.deleteExternalLogFiles(context);
	}
}
