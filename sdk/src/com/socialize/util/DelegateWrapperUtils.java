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

import java.lang.reflect.Proxy;

import com.socialize.android.ioc.IBeanFactory;


/**
 * @author Jason Polites
 */
public class DelegateWrapperUtils  {
	
	private IBeanFactory<DelegateWrapper> delgateWrapperFactory;
	
	@SuppressWarnings("unchecked")
	public <T> T wrap(T primary, T secondary) {
		
		DelegateWrapper wrapper = null;
		
		while(Proxy.isProxyClass(primary.getClass())) {
			wrapper = (DelegateWrapper) Proxy.getInvocationHandler(primary);
			primary = (T) wrapper.getPrimary();
		}
		
		while(Proxy.isProxyClass(secondary.getClass())) {
			wrapper = (DelegateWrapper) Proxy.getInvocationHandler(secondary);
			secondary = (T) wrapper.getSecondary();
		}
		
		wrapper = delgateWrapperFactory.getBean(primary, secondary);
		
		return (T) Proxy.newProxyInstance(
				primary.getClass().getClassLoader(),
				primary.getClass().getInterfaces(),
				wrapper);	
	}
	
	public void setDelgateWrapperFactory(IBeanFactory<DelegateWrapper> delgateWrapperFactory) {
		this.delgateWrapperFactory = delgateWrapperFactory;
	}
	
	
}