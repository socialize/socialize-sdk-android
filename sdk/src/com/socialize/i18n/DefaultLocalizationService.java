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
package com.socialize.i18n;

import android.content.Context;
import com.socialize.log.SocializeLogger;
import com.socialize.util.ResourceLocator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author Jason Polites
 *
 */
public class DefaultLocalizationService implements LocalizationService {

	private ResourceLocator resourceLocator;
	private Properties properties;
	private SocializeLogger logger;
	
	public void init(Context context) {
		InputStream in = null;
		try {
			properties = createProperties();
			in = resourceLocator.locate(context, "i18n.properties");
			properties.load(in);
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Failure loading i18n resources", e);
			}
			else {
				e.printStackTrace();
			}
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.i18n.LocalizationService#getString(android.content.Context, java.lang.String)
	 */
	@Override
	public String getString(String key) {
		if(properties != null) {
			String property = properties.getProperty(key);
			if(property == null) {
				return key;
			}
			else {
				return property;
			}
		}
		return null;
	}
	
	public void setResourceLocator(ResourceLocator resourceLocator) {
		this.resourceLocator = resourceLocator;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	protected Properties createProperties() {
		return new Properties();
	}
}
