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
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

/**
 * @author Jason Polites
 *
 */
public class SocializeConfig {
	
	private Properties properties;
	
	private String propertiesFileName = "socialize.properties";
	private final String defaultPropertiesFileName = propertiesFileName;
	
	
	public static final String FACTORY_PREFIX = "factory.";
	public static final String LOG_LEVEL = "log.level";
	public static final String LOG_TAG = "log.tag";
	
	public SocializeConfig() {
		super();
	}

	public SocializeConfig(String propertiesFileName) {
		super();
		this.propertiesFileName = propertiesFileName;
	}
	
	public void init(Context context) {
		InputStream in = null;
		try {
			try {
				
				// Check assets first for override
				try {
					in = context.getAssets().open(propertiesFileName);
				}
				catch (FileNotFoundException ignore) {
					// Ignore this, just means no override.
				}
				
				if(in == null) {
					in = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultPropertiesFileName); // Don't use prop file name
				}
				
				if(in != null) {
					properties = new Properties();
					properties.load(in);
				}
				else {
					throw new FileNotFoundException("Could not locate [" +
							defaultPropertiesFileName +
							"] in the classpath");
				}
			}
			catch (FileNotFoundException ignore) {
				// TODO: log
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

	public Properties getProperties() {
		return properties;
	}

	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	public String getDefaultPropertiesFileName() {
		return defaultPropertiesFileName;
	}
	
}
