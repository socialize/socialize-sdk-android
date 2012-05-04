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
package com.socialize;

import java.lang.reflect.Proxy;
import android.app.Activity;
import com.socialize.api.action.activity.ActionUtilsProxy;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.activity.ActionListListener;


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
	 * 
	 * @param context
	 * @param start
	 * @param end
	 * @param listener
	 */
	public static void getActionsByApplication (Activity context, int start, int end, ActionListListener listener) {
		proxy.getActionsByApplication(context, start, end, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param user
	 * @param start
	 * @param end
	 * @param listener
	 */
	public static void getActionsByUser (Activity context, User user, int start, int end, ActionListListener listener) {
		proxy.getActionsByUser(context, user, start, end, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param e
	 * @param start
	 * @param end
	 * @param listener
	 */
	public static void getActionsByEntity (Activity context, Entity e, int start, int end, ActionListListener listener) {
		proxy.getActionsByEntity(context, e, start, end, listener);
	}
	
	/**
	 * 
	 * @param context
	 * @param user
	 * @param e
	 * @param start
	 * @param end
	 * @param listener
	 */
	public static void getActionsByUserAndEntity (Activity context, User user, Entity e, int start, int end, ActionListListener listener) {
		proxy.getActionsByUserAndEntity(context, user, e, start, end, listener);
	}
}
