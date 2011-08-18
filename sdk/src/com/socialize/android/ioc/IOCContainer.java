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

/**
 * 
 * @author Jason Polites
 *
 */
public interface IOCContainer {

	/**
	 * Gets the bean denoted by the given name (id).  
	 * If this bean is defined as non-singleton, it will be instantiated in this call.
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T extends Object> T getBean(String name);
	
	/**
	 * Gets the bean denoted by the given name (id).  
	 * If this bean is defined as non-singleton, it will be instantiated in this call using
	 * the arguments passed as constructor arguments.
	 * @param <T>
	 * @param name
	 * @param args Optional constructor arguments.  If the bean is defined with fixed constructor arguments, 
	 * the arguments provided here will be appended to the set of arguments used when instantiating the bean.
	 * Only applies to non-singleton beans!
	 * @return
	 */
	public <T extends Object> T getBean(String name, Object...args);

	/**
	 * Destroys the container.
	 */
	public void destroy();

	/**
	 * Initializes the container.
	 * @param context The current context.
	 * @param builder
	 * @param in
	 * @throws Exception
	 */
	public void init(Context context, ContainerBuilder builder, InputStream...in) throws Exception;

	public void init(Context context, InputStream...in) throws Exception;
	
	public int size();
	
	/**
	 * Allows for re-setting of context.
	 * @param context
	 */
	public void setContext(Context context);

}
