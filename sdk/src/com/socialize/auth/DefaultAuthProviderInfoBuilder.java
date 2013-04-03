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

import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class DefaultAuthProviderInfoBuilder implements AuthProviderInfoBuilder {

	private Map<String, AuthProviderInfoFactory<?>> factories;
	private SocializeLogger logger;

	public void setFactories(Map<String, AuthProviderInfoFactory<?>> factories) {
		this.factories = factories;
	}
	
	@SuppressWarnings("unchecked")
	public <I extends AuthProviderInfo> AuthProviderInfoFactory<I> getFactory(AuthProviderType type) {
		return (AuthProviderInfoFactory<I>) factories.get(type.name());
	}
	
	public boolean validateAll() {
		boolean valid = true;
		Collection<AuthProviderInfoFactory<?>> values = factories.values();
		for (AuthProviderInfoFactory<?> factory : values) {
			AuthProviderInfo instance = factory.getInstanceForRead();
			try {
				instance.validate();
			} 
			catch (SocializeException e) {
				if(logger != null) {
					logger.debug("Failed to validate auth provider [" +
							instance.getType() +
							"] with error [" +
							e.getMessage() +
							"]");
				}
				valid = false;
			}
		}
		return valid;
	}
	
	public boolean isSupported(AuthProviderType type) {
		AuthProviderInfoFactory<AuthProviderInfo> factory = getFactory(type);
		if(factory != null) {
			return factory.getInstanceForRead().isValid();
		}
		return false;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	
}
