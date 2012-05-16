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

import java.lang.reflect.Proxy;
import android.app.Activity;
import com.socialize.api.action.entity.EntityUtilsProxy;
import com.socialize.entity.Entity;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;


/**
 * @author Jason Polites
 *
 */
public class EntityUtils {
	
	static EntityUtilsProxy proxy;
	
	static {
		proxy = (EntityUtilsProxy) Proxy.newProxyInstance(
				EntityUtilsProxy.class.getClassLoader(),
				new Class[]{EntityUtilsProxy.class},
				new SocializeActionProxy("entityUtils"));	// Bean name
	}
	
	/**
	 * 
	 * @param context
	 * @param e
	 * @param listener
	 */
	public static void addEntity (Activity context, Entity e, EntityAddListener listener) {
		proxy.addEntity(context, e, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param key
	 * @param listener
	 */
	public static void getEntity (Activity context, String key, EntityGetListener listener) {
		proxy.getEntity(context, key, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param start
	 * @param end
	 * @param listener
	 */
	public static void getEntities (Activity context, int start, int end, EntityListListener listener) {
		proxy.getEntities(context, start, end, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param start
	 * @param end
	 * @param listener
	 * @param key
	 */
	public static void getEntities (Activity context, EntityListListener listener, String...keys) {
		proxy.getEntities(context, listener, keys);	
	}
}
