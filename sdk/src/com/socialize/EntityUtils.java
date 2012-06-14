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
	 * Saves or Creates an entity
	 * @param context The current context.
	 * @param e The entity to be created.
	 * @param listener A listener to handle the result.
	 */
	public static void saveEntity (Activity context, Entity e, EntityAddListener listener) {
		proxy.saveEntity(context, e, listener);
	}
	
	/**
	 * Retrieves an entity based on its key.
	 * @param context The current context.
	 * @param key The entity key.
	 * @param listener A listener to handle the result.
	 */
	public static void getEntity (Activity context, String key, EntityGetListener listener) {
		proxy.getEntity(context, key, listener);
	}
	
	/**
	 * Retrieves an entity based on its id.
	 * @param context The current context.
	 * @param id The entity id.
	 * @param listener A listener to handle the result.
	 */
	public static void getEntity (Activity context, long id, EntityGetListener listener) {
		proxy.getEntity(context, id, listener);
	}	
	
	/**
	 * Retrieves all entities sorted by creation date.
	 * @param context The current context.
	 * @param start The start index for pagination (0 based).
	 * @param end The end index for pagination (0 based).
	 * @param listener A listener to handle the result.
	 */
	public static void getEntities (Activity context, int start, int end, EntityListListener listener) {
		proxy.getEntities(context, start, end, listener);
	}
	
	/**
	 * Retrieves the entities designated by the given keys.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 * @param keys One or more entity keys.
	 */
	public static void getEntities (Activity context, EntityListListener listener, String...keys) {
		proxy.getEntities(context, listener, keys);	
	}
}
