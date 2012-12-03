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
import android.os.Bundle;
import com.socialize.listener.SocializeInitListener;
import com.socialize.log.SocializeLogger.LogLevel;

/**
 * Singleton helper class to make accessing the socialize service easier.
 * @author Jason Polites
 */
public class Socialize {
	
	// This will be set during the build process
	public static final String VERSION = "2.7.4";
	
	public static final String ENTITY_OBJECT = "socialize.entity";
	public static final String ENTITY_ID = "socialize.entity.id";
	
//	public static final Map<String, SocializeListener> STATIC_LISTENERS = new HashMap<String, SocializeListener>();
	
	public static final String ACTION_ID = "socialize.action.id";
	public static final String ACTION_TYPE = "socialize.action.type";
	public static final String DIRECT_URL = "socialize.direct.url";
	
	public static final String LOG_KEY = "Socialize";
	public static final String USER_ID = "socialize.user.id";
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	
	public static LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARN;
	
	static final SocializeServiceImpl instance = new SocializeServiceImpl();

	private Socialize() {
		super();
	}

	/**
	 * Initialize Socialize synchronously.  Should not be called from the main UI thread.
	 * @param context
	 */
	public static final void init(Context context) {
		instance.init(context);
	}
	
	/**
	 * Initialize Socialize asynchronously.  Can be called from the main UI thread.
	 * @param context
	 */
	public static final void initAsync(Context context) {
		instance.initAsync(context, null);
	}
	
	/**
	 * Initialize Socialize asynchronously.  Can be called from the main UI thread.
	 * @param context
	 * @param listener A listener which will be called after init.
	 */
	public static final void initAsync(Context context, SocializeInitListener listener) {
		instance.initAsync(context, listener);
	}

	/**
	 * Expert only.  Does not normally need to be called.
	 * @param context
	 */
	public static final void destroy(Context context) {
		if(instance != null) {
			instance.destroy();
		}
	}

	/**
	 * Returns the Socialize singleton instance.
	 * @return The Socialize singleton instance.
	 */
	public static final SocializeService getSocialize() {
		return instance;
	}
	
	static Object getBean(String name) {
		return instance.getContainer().getBean(name);
	}

	/**
	 * Called by containing Activity's in their onPause() method.
	 * @param context
	 */
	public static void onPause(Activity context) {
		instance.onPause(context);
	}
	
	/**
	 * Called by containing Activity's in their onResume() method.
	 * @param context
	 */
	public static void onResume(Activity context) {
		instance.onResume(context);
	}
	
	/**
	 * Called by containing Activity's in their onCreate() method.
	 * @param context
	 */
	public static void onCreate(Activity context, Bundle savedInstanceState) {
		instance.onCreate(context, savedInstanceState);
	}

	/**
	 * Called by containing Activity's in their onDestroy() method.
	 * @param context
	 */
	public static void onDestroy(Activity context) {
		instance.onDestroy(context);
	}	
}
