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
import com.socialize.ui.SocializeActivityLifecycleListener;

/**
 * Singleton helper class to make accessing the socialize service easier.
 * @author Jason Polites
 */
public class Socialize {
	
	// This will be set during the build process
	public static final String VERSION = "3.1.3";
	
	public static final String ENTITY_OBJECT = "socialize.entity";
	public static final String ENTITY_ID = "socialize.entity.id";

	public static final String ACTION_ID = "socialize.action.id";
	public static final String ACTION_TYPE = "socialize.action.type";
	public static final String DIRECT_URL = "socialize.direct.url";
	
	public static final String LOG_KEY = "Socialize";
	public static final String USER_ID = "socialize.user.id";
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	
	public static LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARN;
	
	static final SocializeServiceImpl instance = new SocializeServiceImpl();
	
	static SocializeLifecycleListener socializeLifecycleListener;
	static SocializeActivityLifecycleListener socializeActivityLifecycleListener;

	private Socialize() {
		super();
	}

	/**
	 * Initialize Socialize synchronously.  Should not be called from the main UI thread.
	 */
	public static void init(Context context) {
		instance.init(context);
	}
	
	/**
	 * Initialize Socialize asynchronously.  Can be called from the main UI thread.
	 */
	public static void initAsync(Context context) {
		instance.initAsync(context, null);
	}
	
	/**
	 * Initialize Socialize asynchronously.  Can be called from the main UI thread.
	 * @param listener A listener which will be called after init.
	 */
	public static void initAsync(Context context, SocializeInitListener listener) {
		instance.initAsync(context, listener);
	}

	/**
	 * Expert only.  Does not normally need to be called.
	 */
	public static void destroy(Context context) {
        instance.destroy();
    }

	/**
	 * Returns the Socialize singleton instance.
	 * @return The Socialize singleton instance.
	 */
	public static SocializeService getSocialize() {
		return instance;
	}
	
	static Object getBean(String name) {
		return instance.getContainer().getBean(name);
	}

	/**
	 * Called by containing Activities in their onPause() method.
	 */
	public static void onPause(Activity context) {
		instance.onPause(context);
		
		if(socializeLifecycleListener != null) {
			socializeLifecycleListener.onPause(context);
		}
	}
	
	/**
	 * Called by containing Activities in their onResume() method.
	 */
	public static void onResume(Activity context) {
		instance.onResume(context);
		
		if(socializeLifecycleListener != null) {
			socializeLifecycleListener.onResume(context);
		}
	}
	
	/**
	 * Called by containing Activities in their onCreate() method.
	 */
	public static void onCreate(Activity context, Bundle savedInstanceState) {
		instance.onCreate(context, savedInstanceState);
		
		if(socializeLifecycleListener != null) {
			socializeLifecycleListener.onCreate(context, savedInstanceState);
		}
	}

	/**
	 * Called by containing Activities in their onDestroy() method.
	 */
	public static void onDestroy(Activity context) {
		instance.onDestroy(context);
		
		if(socializeLifecycleListener != null) {
			socializeLifecycleListener.onDestroy(context);
		}		
	}

    /**
     * Called by containing Activities in their onStart() method.
     */
    public static void onStart(Activity context) {
        instance.onStart(context);

        if(socializeLifecycleListener != null) {
            socializeLifecycleListener.onStart(context);
        }
    }

    /**
     * Called by containing Activities in their onStop() method.
     */
    public static void onStop(Activity context) {
        instance.onStop(context);

        if(socializeLifecycleListener != null) {
            socializeLifecycleListener.onStop(context);
        }
    }
	
	public static void setSocializeLifecycleListener(SocializeLifecycleListener socializeLifecycleListener) {
		Socialize.socializeLifecycleListener = socializeLifecycleListener;
	}

	public static SocializeActivityLifecycleListener getSocializeActivityLifecycleListener() {
		return socializeActivityLifecycleListener;
	}

	public static void setSocializeActivityLifecycleListener(SocializeActivityLifecycleListener socializeActivityLifecycleListener) {
		Socialize.socializeActivityLifecycleListener = socializeActivityLifecycleListener;
	}
}
