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

	/* (non-Javadoc)
	 * @see com.socialize.android.ioc.IBeanMaker#getBeanName(java.lang.Object, com.socialize.android.ioc.Container)
	 */
	@Override
	public String getBeanName(Object parent, Container container) {
		try {
			Class.forName("com.facebook.Session");
			
			// TODO: Change this!
			// Just return v2 always for now
			return v2BeanName;
//			return v3BeanName;
		}
		catch (ClassNotFoundException e) {
			return v2BeanName;
		}
	}
	
	public void setV2BeanName(String v2BeanName) {
		this.v2BeanName = v2BeanName;
	}
	
	public void setV3BeanName(String v3BeanName) {
		this.v3BeanName = v3BeanName;
	}
}
