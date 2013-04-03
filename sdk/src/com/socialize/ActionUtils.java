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
import com.socialize.api.action.activity.ActionUtilsProxy;
import com.socialize.listener.activity.ActionListListener;

import java.lang.reflect.Proxy;


/**
 * @author Jason Polites
 *
 */
public class ActionUtils {
	
	static ActionUtilsProxy proxy;
	
	static {
		proxy = (ActionUtilsProxy) Proxy.newProxyInstance(
				ActionUtilsProxy.class.getClassLoader(),
				new Class[]{ActionUtilsProxy.class},
				new SocializeActionProxy("actionUtils"));	// Bean name
	}	
	
	/**
	 * Gets the application-wide actions.
	 * @param context The current context.
	 * @param start The start index for pagination (0 based).
	 * @param end The end index for pagination (0 based).
	 * @param listener A listener to handle the result.
	 */
	public static void getActionsByApplication (Activity context, int start, int end, ActionListListener listener) {
		proxy.getActionsByApplication(context, start, end, listener);
	}
	
	/**
	 * Gets the actions of a single user.
	 * @param context The current context.
	 * @param userId The user for whom the actions will be returned.
	 * @param start The start index for pagination (0 based).
	 * @param end The end index for pagination (0 based).
	 * @param listener A listener to handle the result.
	 */
	public static void getActionsByUser (Activity context, long userId, int start, int end, ActionListListener listener) {
		proxy.getActionsByUser(context, userId, start, end, listener);
	}
	
	/**
	 * Gets the actions for a given entity. 
	 * @param context The current context.
	 * @param entityKey The entity key.
	 * @param start The start index for pagination (0 based).
	 * @param end The end index for pagination (0 based).
	 * @param listener A listener to handle the result.
	 */
	public static void getActionsByEntity (Activity context, String entityKey, int start, int end, ActionListListener listener) {
		proxy.getActionsByEntity(context, entityKey, start, end, listener);
	}
	
	/**
	 * Gets the actions of a single user on a single entity.
	 * @param context The current context.
	 * @param userId The user for whom the actions will be returned.
	 * @param entityKey The entity key.
	 * @param start The start index for pagination (0 based).
	 * @param end The end index for pagination (0 based).
	 * @param listener A listener to handle the result.
	 */
	public static void getActionsByUserAndEntity (Activity context, long userId, String entityKey, int start, int end, ActionListListener listener) {
		proxy.getActionsByUserAndEntity(context, userId, entityKey, start, end, listener);
	}
}
