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

import android.content.Context;
import com.socialize.log.SocializeLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jason Polites
 *
 */
public class ResourceLocator {

	private ClassLoaderProvider classLoaderProvider = new ClassLoaderProvider();
	private SocializeLogger logger;

	public InputStream locateInAssets(Context context, String name) throws IOException {
		InputStream in = null;
		
		try {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Looking for " +
						name +
						" in asset path...");
			}
			
			in = context.getAssets().open(name);
			
			if(in != null) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Found " +
							name +
							" in asset path");
				}
			}
		}
		catch (IOException ignore) {
			// Ignore this, just means no override.
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("No file found in assets with name [" +
						name +
						"].");
			}
		}
		
		return in;
	}
	
	public InputStream locateInLocalStorage(Context context, String name) throws IOException {
		try {
			return context.openFileInput(name);
		}
		catch (FileNotFoundException e) {
			return null;
		}
	}

	public InputStream locateInClassPath(Context context, String name) throws IOException {
		
		InputStream in = null;
		
		if(classLoaderProvider != null) {
			
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("Looking for " +
						name +
						" in classpath...");
			}
			
			in = classLoaderProvider.getClassLoader().getResourceAsStream(name);
			
			if(in != null) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Found " +
							name +
							" in classpath");
				}
			}
		}

		return in;
	}
	
	public InputStream locate(Context context, String name) throws IOException {
		
		InputStream in = locateInAssets(context, name);
		
		if(in == null) {
			in = locateInClassPath(context, name);
		}
		
		if(in == null) {
			in = locateInLocalStorage(context, name);
		}
		
		if(in == null) {
			if(logger != null) {
				logger.error("Could not locate [" +
						name +
						"] in any location");
			}
			else {
				FileNotFoundException e = new FileNotFoundException("Could not locate [" +
						name +
						"] in any location");
				SocializeLogger.e(e.getMessage(), e);
			}
		}
		
		return in;
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
}
