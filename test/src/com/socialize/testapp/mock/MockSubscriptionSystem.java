package com.socialize.testapp.mock;

import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.SubscriptionSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.listener.subscription.SubscriptionListener;
import com.socialize.notifications.NotificationType;

public class MockSubscriptionSystem implements SubscriptionSystem {

	private Subscription subscription;
	private ListResult<Subscription> entityList;
	
	public MockSubscriptionSystem() {
		super();
		subscription = new Subscription();
		subscription.setId(0L);
	}
	
	@Override
	public void getSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
		if(listener != null) listener.onGet(subscription);
	}

	@Override
	public void listSubscriptions(SocializeSession session, int startIndex, int endIndex, SubscriptionListener listener) {
		if(listener != null) listener.onList(entityList);
	}

	@Override
	public void listSubscriptions(SocializeSession session, SubscriptionListener listener) {
		if(listener != null) listener.onList(entityList);
	}

	@Override
	public void addSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
		if(listener != null) listener.onGet(subscription);
	}

	@Override
	public void removeSubscription(SocializeSession session, Entity entity, NotificationType type, SubscriptionListener listener) {
		if(listener != null) listener.onGet(subscription);
	}

}
