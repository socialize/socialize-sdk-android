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
import com.socialize.api.action.comment.SubscriptionUtilsProxy;
import com.socialize.entity.Entity;
import com.socialize.listener.subscription.SubscriptionCheckListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.notifications.SubscriptionType;

import java.lang.reflect.Proxy;


/**
 * @author Jason Polites
 *
 */
public class SubscriptionUtils {
	static SubscriptionUtilsProxy subscriptionUtils;
	static {
		subscriptionUtils = (SubscriptionUtilsProxy) Proxy.newProxyInstance(
				SubscriptionUtilsProxy.class.getClassLoader(),
				new Class[]{SubscriptionUtilsProxy.class},
				new SocializeActionProxy("subscriptionUtils"));	// Bean name		
	}	
	

	/**
	 * Subscribes the current user to notifications for new comments on this entity.
	 * @param context The current context.
	 * @param e The entity.
	 * @param type The subscription type.
	 * @param listener A listener to handle the result.
	 */
	public static void subscribe (Activity context, Entity e, SubscriptionType type, SubscriptionResultListener listener) {
		subscriptionUtils.subscribe(context, e, type, listener);
	}
	
	/**
	 * Un-Subscribes the current user from notifications for new comments on this entity.
	 * @param context The current context.
	 * @param e The entity.
	 * @param type The subscription type.
	 * @param listener A listener to handle the result.
	 */
	public static void unsubscribe (Activity context, Entity e, SubscriptionType type, SubscriptionResultListener listener) {
		subscriptionUtils.unsubscribe(context, e, type, listener);
	}
	
	/**
	 * Determines if the current user is subscribed to notifications on new comments for the given entity.
	 * @param context The current context.
	 * @param e The entity.
	 * @param type The subscription type.
	 * @param listener A listener to handle the result.
	 */
	public static void isSubscribed (Activity context, Entity e, SubscriptionType type, SubscriptionCheckListener listener) {
		subscriptionUtils.isSubscribed(context, e, type, listener);
	}
		
}
