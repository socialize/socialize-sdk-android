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
package com.socialize.android.ioc;

import android.util.Log;

public class Logger {

	public static String LOG_KEY = "AndroidIOC";
	
	public static int logLevel = Log.INFO;
	
	static {
		if(Log.isLoggable(LOG_KEY, Log.DEBUG)) {
			logLevel = Log.DEBUG;
		}
		else if(Log.isLoggable(LOG_KEY, Log.INFO)) {
			logLevel = Log.INFO;
		}
		else if(Log.isLoggable(LOG_KEY, Log.WARN)) {
			logLevel = Log.WARN;
		}
		else if(Log.isLoggable(LOG_KEY, Log.ERROR)) {
			logLevel = Log.ERROR;
		}
	}
	
	public static final void d(String tag, String msg) {
		if(logLevel <= Log.DEBUG) Log.d(LOG_KEY, tag + ": " + msg);
	}

	public static final void i(String tag, String msg) {
		if(logLevel <= Log.INFO) Log.i(LOG_KEY, tag + ": " + msg);
	}
	
	public static final void w(String tag, String msg) {
		if(logLevel <= Log.WARN) Log.w(LOG_KEY, tag + ": " + msg);
	}
	
	public static final void w(String tag, String msg, Throwable e) {
		if(logLevel <= Log.WARN) Log.w(LOG_KEY, tag + ": " + msg, e);
	}

	public static final void e(String tag, String msg) {
		if(logLevel <= Log.ERROR) Log.e(LOG_KEY, tag + ": " + msg);
	}
	
	public static final void e(String tag, String msg, Throwable e) {
		if(logLevel <= Log.ERROR) Log.e(LOG_KEY, tag + ": " + msg, e);
	}
	
	public static boolean isInfoEnabled() {
		return (logLevel <= Log.INFO);
	}
}
