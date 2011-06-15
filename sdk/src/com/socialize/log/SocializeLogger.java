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
package com.socialize.log;

import android.util.Log;

import com.socialize.config.SocializeConfig;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeLogger {

	public static enum LogLevel {VERBOSE,DEBUG,INFO,WARN,ERROR}
	
	public static final int INITIALIZE_FAILED = 0;
	public static final int NOT_INITIALIZED = 1;
	public static final int NOT_AUTHENTICATED = 2;
	public static final int NO_CONFIG = 3;
	public static final int NO_UDID = 4;
	public static final int ERROR_CODE_LOAD_FAIL = 5;
	
	private LogLevel logLevel = LogLevel.INFO;
	private String logTag = "Socialize";
	private SocializeConfig config;
	
	public void init(SocializeConfig config) {
		String ll = config.getProperty(SocializeConfig.LOG_LEVEL);
		
		if(!StringUtils.isEmpty(ll)) {
			logLevel = LogLevel.valueOf(ll);
		}
		
		String tag = config.getProperty(SocializeConfig.LOG_TAG);
		
		if(!StringUtils.isEmpty(tag)) {
			logTag = tag;
		}
		
		this.config = config;
	}
	
	public void debug(int messageId) {
		debug(getMessage(messageId));
	}
	
	public void info(int messageId) {
		info(getMessage(messageId));
	}
	
	public void warn(int messageId) {
		warn(getMessage(messageId));
	}
	
	public void error(int messageId) {
		error(getMessage(messageId));
	}
	
	public void warn(int messageId, Throwable error) {
		warn(getMessage(messageId), error);
	}
	
	public void error(int messageId, Throwable error) {
		error(getMessage(messageId), error);
	}
	
	public void debug(String msg) {
		Log.d(logTag, msg);
	}
	
	public void info(String msg) {
		Log.i(logTag, msg);
	}
	
	public void warn(String msg) {
		Log.w(logTag, msg);
	}
	
	public void error(String msg) {
		Log.e(logTag, msg);
	}
	
	public void warn(String msg, Throwable error) {
		Log.w(logTag, msg, error);
	}
	
	public void error(String msg, Throwable error) {
		Log.e(logTag, msg, error);
	}
	
	public boolean isVerboseEnabled() {
		return logLevel.ordinal() <= LogLevel.VERBOSE.ordinal();
	}
	
	public boolean isDebugEnabled() {
		return logLevel.ordinal() <= LogLevel.DEBUG.ordinal();
	}
	
	public boolean isInfoEnabled() {
		return logLevel.ordinal() <= LogLevel.INFO.ordinal();
	}
	
	public boolean isWarnEnabled() {
		return logLevel.ordinal() <= LogLevel.WARN.ordinal();
	}
	
	public String getMessage(int id) {
		if(this.config != null) {
			String msg =  this.config.getProperty(SocializeConfig.LOG_MSG + id);
			if(msg == null) {
				msg = "";
			}
			return msg;
		}
		else {
			return "Log System Error!  The log system has not been initialized correctly.  No config found.";
		}
	}
	
}
