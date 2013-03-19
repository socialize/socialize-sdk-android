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
package com.socialize.auth;

import com.socialize.config.SocializeConfig;

/**
 * @author Jason Polites
 *
 */
public abstract class BaseAuthProviderInfoFactory<I extends AuthProviderInfo> implements AuthProviderInfoFactory<I> {

	protected SocializeConfig config;
	protected I instance;

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	@Deprecated
	@Override
	public I getInstance(String...permissions) {
		if(instance == null) {
			instance = initInstanceForWrite(permissions);
		}
		updateForWrite(instance, permissions);
		return instance;
	}
	
	@Override
	public I getInstanceForRead(String... permissions) {
		return getInstance(true, permissions);
	}

	@Override
	public I getInstanceForWrite(String... permissions) {
		return getInstance(false, permissions);
	}

	private I getInstance(boolean readOnly, String...permissions) {
		if(instance == null) {
			if(readOnly) {
				instance = initInstanceForRead(permissions);
			}
			else {
				instance = initInstanceForWrite(permissions);
			}
		}
		
		if(readOnly) {
			updateForRead(instance, permissions);	
		}
		else {
			updateForWrite(instance, permissions);	
		}		
		
		return instance;
	}

    public abstract I initInstanceForRead(String...permissions);

    public abstract I initInstanceForWrite(String...permissions);
	
	protected abstract void updateForRead(I instance, String... permissions);
	
	protected abstract void updateForWrite(I instance, String... permissions);
}
