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
package com.socialize.api.action.comment;

import android.app.Activity;
import com.socialize.api.action.SocializeActionUtilsBase;
import com.socialize.entity.Entity;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.notifications.NotificationType;


/**
 * @author Jason Polites
 *
 */
public class SocializeSubscriptionUtils extends SocializeActionUtilsBase implements SubscriptionUtilsProxy {
	
	private SubscriptionSystem subscriptionSystem;

	@Override
	public void subscribe(Activity context, Entity e, SubscriptionResultListener listener) {
		subscriptionSystem.addSubscription(getSocialize().getSession(), e, NotificationType.NEW_COMMENTS, listener);
	}

	@Override
	public void unsubscribe(Activity context, Entity e, SubscriptionResultListener listener) {
		subscriptionSystem.removeSubscription(getSocialize().getSession(), e, NotificationType.NEW_COMMENTS, listener);
	}
	
	@Override
	public void isSubscribed(Activity context, Entity e, SubscriptionGetListener listener) {
		subscriptionSystem.getSubscription(getSocialize().getSession(), e, listener);
	}

	public void setSubscriptionSystem(SubscriptionSystem subscriptionSystem) {
		this.subscriptionSystem = subscriptionSystem;
	}
	
}
