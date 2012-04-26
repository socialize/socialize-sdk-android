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
package com.socialize.api.action.comment;

import android.app.Activity;
import com.socialize.entity.Entity;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionResultListener;


/**
 * @author Jason Polites
 *
 */
public interface SubscriptionUtilsProxy {
	/**
	 * Subscribes the current user to notifications for new comments on this entity.
	 * @param context The current context.
	 * @param e The entity.
	 * @param listener A listener to handle the result.
	 */
	public void subscribe (Activity context, Entity e, SubscriptionResultListener listener);
	
	/**
	 * Un-Subscribes the current user from notifications for new comments on this entity.
	 * @param context The current context.
	 * @param e The entity.
	 * @param listener A listener to handle the result.
	 */
	public void unsubscribe (Activity context, Entity e, SubscriptionResultListener listener);
	
	/**
	 * Determines if the current user is subscribed to notifications on new comments for the given entity.
	 * @param context The current context.
	 * @param e The entity.
	 * @param listener A listener to handle the result.
	 */
	public void isSubscribed (Activity context, Entity e, SubscriptionGetListener listener);
}
