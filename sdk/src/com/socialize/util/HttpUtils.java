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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpResponse;

import android.content.Context;

import com.socialize.config.SocializeConfig;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class HttpUtils {
	
	private ResourceLocator resourceLocator;
	private SocializeLogger logger;
	
	final Map<Integer, String> httpStatusCodes = new HashMap<Integer, String>();
	
	public void init(Context context) {
		InputStream in = null;
		try {
			in = resourceLocator.locate(context, SocializeConfig.SOCIALIZE_ERRORS_PATH);
			
			Properties props = new Properties();
			props.load(in);
			
			Set<Object> keys = props.keySet();
			
			for (Object object : keys) {
				try {
					int code = Integer.parseInt(object.toString());
					String msg = props.getProperty(object.toString());
					httpStatusCodes.put(code, msg);
				}
				catch (NumberFormatException e) {
					if(logger != null && logger.isWarnEnabled()) {
						logger.warn(object.toString() + " is not an integer");
					}
				}
			}
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error(SocializeLogger.ERROR_CODE_LOAD_FAIL, e);
			}
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException ignore) {}
			}
		}
	}
	
	public boolean isHttpError(HttpResponse response) {
		return isHttpError(response.getStatusLine().getStatusCode());
	}
	
	public boolean isAuthError(HttpResponse response) {
		return isAuthError(response.getStatusLine().getStatusCode());
	}
	
	public boolean isAuthError(int code) {
		return (code == 401 || code == 403);
	}
	
	public boolean isHttpError(int code) {
		return (code >= 400);
	}
	
	public String getMessageFor(int code) {
		return httpStatusCodes.get(code);
	}

	public void setResourceLocator(ResourceLocator resourceLocator) {
		this.resourceLocator = resourceLocator;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
}
