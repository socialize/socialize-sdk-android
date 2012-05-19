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

import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.listener.subscription.SubscriptionListener;
import com.socialize.notifications.NotificationType;

/**
 * @author Jason Polites
 *
 */
public interface SubscriptionSystem {
	
	public static final String ENDPOINT = "/user/subscription/";

	public void getSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener);
	
	public void listSubscriptions(SocializeSession session, int startIndex, int endIndex, SubscriptionListener listener);
	
	public void listSubscriptions(SocializeSession session, SubscriptionListener listener);
	
	public void addSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener);
	
	public void removeSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener);
}
