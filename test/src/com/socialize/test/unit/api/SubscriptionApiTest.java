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
package com.socialize.test.unit.api;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.SocializeSubscriptionSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.Subscription;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.subscription.SubscriptionListener;
import com.socialize.notifications.NotificationType;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeUnitTest;

import java.util.List;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({SocializeSession.class, SubscriptionListener.class, SocializeProvider.class})
public class SubscriptionApiTest extends SocializeUnitTest {
	SocializeProvider<Subscription> provider;
	SocializeSession session;
	SubscriptionListener listener;
	
	@SuppressWarnings("unchecked")
	@Override
	public void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		session = AndroidMock.createMock(SocializeSession.class);
		listener = AndroidMock.createMock(SubscriptionListener.class);
	}
	
	public void testAddSubscription() {
		final String key = "foo";
		
		SocializeSubscriptionSystem api = new SocializeSubscriptionSystem(provider){
			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Subscription> object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		api.addSubscription(session, Entity.newInstance(key, null), NotificationType.NEW_COMMENTS, listener);
		
		List<Subscription> list = getNextResult();
		assertNotNull(list);
		assertEquals(1, list.size());
		
		Subscription result = list.get(0);
		
		assertNotNull(result);
		assertEquals(NotificationType.NEW_COMMENTS, result.getNotificationType());
		assertEquals(true, result.isSubscribed());
		assertNotNull(result.getEntity());
		assertEquals(key, result.getEntity().getKey());
	}
	
	public void testRemoveSubscription() {
		final String key = "foo";
		
		SocializeSubscriptionSystem api = new SocializeSubscriptionSystem(provider){
			@Override
			public void postAsync(SocializeSession session, String endpoint, List<Subscription> object, SocializeActionListener listener) {
				addResult(object);
			}
		};
		
		api.removeSubscription(session, Entity.newInstance(key, null), NotificationType.NEW_COMMENTS, listener);
		
		List<Subscription> list = getNextResult();
		assertNotNull(list);
		assertEquals(1, list.size());
		
		Subscription result = list.get(0);
		
		assertNotNull(result);
		assertEquals(NotificationType.NEW_COMMENTS, result.getNotificationType());
		assertEquals(false, result.isSubscribed());
		assertNotNull(result.getEntity());
		assertEquals(key, result.getEntity().getKey());
	}		
	

	public void testGetSubscriptionsByEntity() {
		
		final String key = "foo";
		
		int startIndex = 0, endIndex = 10;
		
		SocializeSubscriptionSystem api = new SocializeSubscriptionSystem(provider) {
			@Override
			public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
				addResult(key);
				addResult(startIndex);
				addResult(endIndex);
			}
		};
		
		api.listSubscriptions(session, startIndex, endIndex, listener);
		
		String after = getNextResult();
		
		assertNotNull(after);
		assertEquals(key, after);
		
		Integer afterStartIndex = getNextResult();
		Integer afterEndIndex = getNextResult();
		
		assertEquals(startIndex, afterStartIndex.intValue());
		assertEquals(endIndex, afterEndIndex.intValue());
	}	
	
	public void testGetSubscription() {
		
		String entityKey = "foobar";
		
		SocializeSubscriptionSystem api = new SocializeSubscriptionSystem(provider) {
			@Override
			public void getByEntityAsync(SocializeSession session, String endpoint, String key, SocializeActionListener listener) {
				addResult(key);
			}
		};
		
		api.getSubscription(session, Entity.newInstance(entityKey, null), NotificationType.NEW_COMMENTS, listener);
		
		String strId = getNextResult();
		
		assertNotNull(strId);
		assertEquals(entityKey, strId);
	}
}
