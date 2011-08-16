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
package com.socialize.android.ioc;

import java.io.InputStream;

import android.content.Context;

public class AndroidIOC implements IOCContainer {
	
	private Container container;
	private boolean initialized = false;
	
	@Override
	public void init(Context context, InputStream...in) throws Exception {
		init(context, new ContainerBuilder(context), in);
	}
	
	@Override
	public void init(Context context, ContainerBuilder builder, InputStream...in) throws Exception {
		if(!initialized) {
			container = builder.build(in);
			initialized = true;
		}
	}
	
	@Override
	public int size() {
		return container.size();
	}

	/* (non-Javadoc)
	 * @see com.socialize.android.ioc.IOCContainer#getBean(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean(String name) {
		return (T) container.getBean(name);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.android.ioc.IOCContainer#getBean(java.lang.String, java.lang.Object[])
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(String name, Object... args) {
		return (T) container.getBean(name, args);
	}

	/* (non-Javadoc)
	 * @see com.socialize.android.ioc.IOCContainer#destroy()
	 */
	@Override
	public void destroy() {
		if(container != null) {
			container.destroy();	
		}
		initialized = false;
	}
}
