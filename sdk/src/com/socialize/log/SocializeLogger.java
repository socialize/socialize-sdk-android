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
package com.socialize.log;

import android.content.Context;
import android.util.Log;
import com.socialize.config.SocializeConfig;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeLogger {

	public static enum LogLevel {VERBOSE,DEBUG,INFO,WARN,ERROR}
	
	public static String LOG_TAG = "Socialize";
	
	public static final int INITIALIZE_FAILED = 0;
	public static final int NOT_INITIALIZED = 1;
	public static final int NOT_AUTHENTICATED = 2;
	public static final int NO_CONFIG = 3;
	public static final int NO_UDID = 4;
	public static final int ERROR_CODE_LOAD_FAIL = 5;
	
	private LogLevel logLevel = LogLevel.WARN;
	private SocializeConfig config;
	private boolean logThread = true;
	private boolean initialized = false;
	
	private ExternalLogger externalLogger;
	
	public SocializeLogger() {
		super();
	}
	
	public SocializeLogger(LogLevel level) {
		super();
		this.logLevel = level;
	}

	public void init(Context context, SocializeConfig config) {
		
		if(config.isDiagnosticLoggingEnabled() && externalLogger == null) {
			externalLogger = newExternalLogger();
			externalLogger.init(context);
		}
		
		String ll = config.getProperty(SocializeConfig.LOG_LEVEL);
		logThread = config.getBooleanProperty(SocializeConfig.LOG_THREAD, true);
		
		if(!StringUtils.isEmpty(ll)) {
			logLevel = LogLevel.valueOf(ll);
		}
		
		String tag = config.getProperty(SocializeConfig.LOG_TAG);
		
		if(!StringUtils.isEmpty(tag)) {
			LOG_TAG = tag;
		}
		
		this.config = config;
		this.initialized = true;
	}
	
	// Mockable
	protected ExternalLogger newExternalLogger() {
		return new AsyncSDCardExternalLogger();
	}
	
	public void destroy() {
		if(externalLogger != null) {
			externalLogger.destroy();
			externalLogger = null;
		}
	}
	
	public ExternalLogger getExternalLogger() {
		return externalLogger;
	}

	public void setExternalLogger(ExternalLogger externalLogger) {
		this.externalLogger = externalLogger;
	}

	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
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
		msg = getMessage(msg);
		d(msg);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.DEBUG, System.currentTimeMillis(), LOG_TAG, msg);
		}
	}
	
	public void debug(String msg, Throwable error) {
		msg = getMessage(msg);
		d(msg, error);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.DEBUG, System.currentTimeMillis(), LOG_TAG, msg);
		}
	}
	
	public void info(String msg) {
		msg = getMessage(msg);
		i(msg);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.INFO, System.currentTimeMillis(), LOG_TAG, msg);
		}
	}
	
	public void warn(String msg) {
		msg = getMessage(msg);
		w(msg);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.WARN, System.currentTimeMillis(), LOG_TAG, msg);
		}
	}
	
	public void error(String msg) {
		msg = getMessage(msg);
		e(msg);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.ERROR, System.currentTimeMillis(), LOG_TAG, msg);
		}
	}
	
	public void warn(String msg, Throwable error) {
		msg = getMessage(msg);
		w(msg, error);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.WARN, System.currentTimeMillis(), LOG_TAG, msg, error);
		}
	}
	
	public void error(String msg, Throwable error) {
		msg = getMessage(msg);
		e(msg, error);
		
		if(externalLogger != null && externalLogger.canWrite()) {
			externalLogger.log(LogLevel.ERROR, System.currentTimeMillis(), LOG_TAG, msg, error);
		}
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
	
	public boolean isInitialized() {
		return initialized;
	}

	protected String getMessage(String message) {
		if(logThread) {
			return "Thread[" +
					Thread.currentThread().getName() +
					"]: " + message;
		}
		else {
			return message;
		}
	}
	
	public String getMessage(int id) {
		if(this.config != null) {
			String msg =  this.config.getProperty(SocializeConfig.LOG_MSG + id);
			if(msg == null) {
				msg = "";
			}
			return getMessage(msg);
		}
		else {
			return getMessage("Log System Error!  The log system has not been initialized correctly.  No config found.");
		}
	}
	
	
	public boolean isLogThread() {
		return logThread;
	}

	public void setLogThread(boolean logThread) {
		this.logThread = logThread;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public static void d(String msg) {
		Log.d(LOG_TAG, msg);
	}
	
	public static void d(String msg, Throwable error) {
		Log.d(LOG_TAG, msg, error);
	}
	
	public static void i(String msg) {
		Log.i(LOG_TAG, msg);
	}
	
	public static void e(String msg, Throwable error) {
		Log.e(LOG_TAG, msg, error);
	}
	
	public static void e(String msg) {
		Log.e(LOG_TAG, msg);
	}
	
	public static void w(String msg, Throwable error) {
		Log.w(LOG_TAG, msg, error);
	}
	
	public static void w(String msg) {
		Log.w(LOG_TAG, msg);
	}
	
	
	
}
