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
package com.socialize;

import android.app.Activity;
import com.socialize.api.action.view.ViewUtilsProxy;
import com.socialize.entity.Entity;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.listener.view.ViewGetListener;

import java.lang.reflect.Proxy;


/**
 * @author Jason Polites
 *
 */
public class ViewUtils {
	
	static ViewUtilsProxy proxy;
	
	static {
		proxy = (ViewUtilsProxy) Proxy.newProxyInstance(
				ViewUtilsProxy.class.getClassLoader(),
				new Class[]{ViewUtilsProxy.class},
				new SocializeActionProxy("viewUtils"));	// Bean name
	}

	/**
	 * Records a view against the given entity for the current user.
	 * @param context The current context.
	 * @param e The entity to be viewed.
	 * @param listener A listener to handle the result.
	 */
	public static void view (Activity context, Entity e, ViewAddListener listener) {
		proxy.view(context, e, listener);
	}
	
	public static void getView (Activity context, long id, ViewGetListener listener) {
		proxy.getView(context, id, listener);
	}
}
