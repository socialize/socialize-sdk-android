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
package com.socialize.networks.facebook;

import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.IBeanMaker;

/**
 * Determines which version of the facebook SDK the user is using and returns the appropriate facade.
 * @author Jason Polites
 */
public class FacebookFacadeFactory implements IBeanMaker {
	
	private String v2BeanName;
	private String v3BeanName;
	
	private String beanName;
	
	/**
	 * Set to true to force FB SDK v2.x
	 */
	static boolean FORCE_V2 = false;

	/* (non-Javadoc)
	 * @see com.socialize.android.ioc.IBeanMaker#getBeanName(java.lang.Object, com.socialize.android.ioc.Container)
	 */
	@Override
	public String getBeanName(Object parent, Container container) {
		if(FORCE_V2) {
			return v2BeanName;
		}
		
		if(beanName == null) {
			try {
				Class.forName("com.facebook.Session");
				// We assume that if this class exists, we are v3+
				beanName = v3BeanName;
			}
			catch (ClassNotFoundException e) {
				beanName = v2BeanName;
			}
		}
		
		return beanName;
	}
	
	public void setV2BeanName(String v2BeanName) {
		this.v2BeanName = v2BeanName;
	}
	
	public void setV3BeanName(String v3BeanName) {
		this.v3BeanName = v3BeanName;
	}
}
