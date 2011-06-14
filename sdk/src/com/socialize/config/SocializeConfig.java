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
package com.socialize.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeConfig {
	
	public static final String DEFAULT_PROPERTIES = "socialize.properties";
	
	private Properties properties;
	
	private String propertiesFileName = DEFAULT_PROPERTIES;
	
	public static final String LOG_LEVEL = "log.level";
	public static final String LOG_TAG = "log.tag";
	public static final String LOG_MSG = "log.msg.";
	public static final String API_HOST = "api.host";
	
	public static final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";
	public static final String HTTP_SOCKET_TIMEOUT = "http.socket.timeout";
	
	public SocializeConfig() {
		super();
	}

	public SocializeConfig(String propertiesFileName) {
		super();
		this.propertiesFileName = propertiesFileName;
	}
	
	/**
	 * 
	 * @param context
	 */
	public void init(Context context) {
		init(context, null);
	}
	
	/**
	 * 
	 * @param context
	 * @param classLoaderProvider
	 */
	public void init(Context context, ClassLoaderProvider classLoaderProvider) {
		InputStream in = null;
		try {
			try {
				
				// Check assets first for override
				try {
					in = context.getAssets().open(propertiesFileName);
				}
				catch (IOException ignore) {
					// Ignore this, just means no override.
					Log.w(getClass().getSimpleName(), "Could not load config from [" +
							propertiesFileName +
							"].  Using defaults");
				}
				
				if(in == null) {
					if(classLoaderProvider == null) {
						in = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_PROPERTIES); // Don't use prop file name
					}
					else {
						in = classLoaderProvider.getClassloader().getResourceAsStream(DEFAULT_PROPERTIES); // Don't use prop file name
					}
				}
				
				if(in != null) {
					properties = new Properties();
					properties.load(in);
				}
				else {
					throw new FileNotFoundException("Could not locate [" +
							DEFAULT_PROPERTIES +
							"] in the classpath");
				}
			}
			catch (IOException ignore) {
				// No logger yet.. just Android log
				Log.w(getClass().getSimpleName(), "Config load error", ignore);
			}
			finally {
				if(in != null) {
					in.close();
				}
			}
			
			if(properties == null) {
				properties = new Properties();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getProperty(String key) {
		return getProperty(key, null);
	}
	
	public String getProperty(String key, String defaultValue) {
		String val = properties.getProperty(key);
		if(!StringUtils.isEmpty(val)) {
			return val;
		}
		return defaultValue;
	}
	
	public long getLongProperty(String key, long defaultValue) {
		String val = properties.getProperty(key);
		if(!StringUtils.isEmpty(val)) {
			return Long.parseLong(val);
		}
		return defaultValue;
	}
	
	public int getIntProperty(String key, int defaultValue) {
		String val = properties.getProperty(key);
		if(!StringUtils.isEmpty(val)) {
			return Integer.parseInt(val);
		}
		return defaultValue;
	}

	public String getDefaultPropertiesFileName() {
		return DEFAULT_PROPERTIES;
	}

	public Properties getProperties() {
		return properties;
	}
}
