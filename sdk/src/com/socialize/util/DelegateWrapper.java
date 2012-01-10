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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.util.Log;

import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class DelegateWrapper implements InvocationHandler {

	private Object primary;
	private Object secondary;
	
	private SocializeLogger logger;
	
	public DelegateWrapper(Object primary, Object secondary) {
		super();
		this.primary = primary;
		this.secondary = secondary;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			if(!method.isAnnotationPresent(DelegateOnly.class)) {
				result = method.invoke(primary, args);
			}
		} 
		catch (Throwable e) {
			handleError(e);
		}
		finally {
			try {
				method.invoke(secondary, args);
			} 
			catch (Throwable e) {
				handleError(e);
			}
		}
		return result;
	}
	protected void handleError(Throwable e) {
		if(logger != null) {
			logger.error("Error during delegate call", e);
		}
		else {
			Log.e(SocializeLogger.LOG_TAG, "Error during delegate call", e);
		}
	}

	public Object getPrimary() {
		return primary;
	}

	public void setPrimary(Object primary) {
		this.primary = primary;
	}

	public Object getSecondary() {
		return secondary;
	}

	public void setSecondary(Object secondary) {
		this.secondary = secondary;
	}
}
