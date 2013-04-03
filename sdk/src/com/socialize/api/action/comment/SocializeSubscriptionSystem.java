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

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.entity.Subscription;
import com.socialize.listener.subscription.SubscriptionListener;
import com.socialize.notifications.NotificationType;
import com.socialize.provider.SocializeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Polites
 *
 */
public class SocializeSubscriptionSystem extends SocializeApi<Subscription, SocializeProvider<Subscription>> implements SubscriptionSystem {

	public SocializeSubscriptionSystem(SocializeProvider<Subscription> provider) {
		super(provider);
	}

	@Override
	public void getSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
		getByEntityAsync(session, ENDPOINT, entity.getKey(), listener);
	}

	@Override
	public void listSubscriptions(SocializeSession session, SubscriptionListener listener) {
		listAsync(session, ENDPOINT, listener);
	}

	@Override
	public void listSubscriptions(SocializeSession session, int startIndex, int endIndex, SubscriptionListener listener) {
		listAsync(session, ENDPOINT, startIndex, endIndex, listener);
	}

	@Override
	public void addSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
		doSubscribe(session, entity, type, listener, true);	
	}

	@Override
	public void removeSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
		doSubscribe(session, entity, type, listener, false);	
	}
	

	protected void doSubscribe(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener, boolean subscribed) {
		Subscription c = new Subscription();
		c.setUser(session.getUser());
		c.setEntity(entity);
		c.setSubscribed(subscribed);
		c.setNotificationType(type);
		
		List<Subscription> list = new ArrayList<Subscription>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);		
	}
}
