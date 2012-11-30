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
package com.socialize.test.ui.share;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import com.socialize.ShareUtils;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionImpl;
import com.socialize.api.action.ShareType;
import com.socialize.api.event.EventListener;
import com.socialize.api.event.SocializeEvent;
import com.socialize.api.event.SocializeEventSystem;
import com.socialize.config.SocializeConfig;
import com.socialize.networks.SocialNetwork;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.share.ShareDialogFactory;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;


/**
 * @author Jason Polites
 *
 */
public class ShareDialogFactoryTest extends SocializeActivityTest {

	public void testShareDialogFactoryRecordsEvents() throws Exception {
		
		ShareDialogFactory factory = new ShareDialogFactory() {
			
			@Override
			protected void showDialog(Context context, BeanCreationListener<SharePanelView> beanListener, ShareDialogListener listener, Object... args) {
				listener.onShow(null, null);
				listener.onContinue(null, false, SocialNetwork.TWITTER, SocialNetwork.FACEBOOK);
				listener.onSimpleShare(ShareType.TWITTER);
				listener.onCancel(null);
			}
		};
		
		SocializeConfig config = new SocializeConfig();
		config.setProperty(SocializeConfig.SOCIALIZE_EVENTS_SHARE_ENABLED, "true");
		
		SocializeAccess.setSession(new SocializeSessionImpl());
		
		final CountDownLatch latch = new CountDownLatch(4);
		
		SocializeEventSystem mockEventSystem = new SocializeEventSystem(null) {
			
			int count = 0;
			@Override
			public void addEvent(SocializeSession session, SocializeEvent event, EventListener eventListener) {
				addResult(count, event);
				count++;
				latch.countDown();
			}
		};

		factory.setConfig(config);
		factory.setEventSystem(mockEventSystem);
		
		factory.show(TestUtils.getActivity(this), null, null, null, ShareUtils.DEFAULT);
		
		assertTrue(latch.await(10, TimeUnit.SECONDS));
		
		SocializeEvent event0 = getResult(0);
		SocializeEvent event1 = getResult(1);
		SocializeEvent event2 = getResult(2);
		SocializeEvent event3 = getResult(3);
		
		assertNotNull(event0);
		assertNotNull(event1);
		assertNotNull(event2);
		assertNotNull(event3);
		
		assertEquals("SHARE_DIALOG", event0.getBucket());
		assertEquals("SHARE_DIALOG", event1.getBucket());
		assertEquals("SHARE_DIALOG", event2.getBucket());
		assertEquals("SHARE_DIALOG", event3.getBucket());
		
		JSONObject data0 = event0.getData();
		JSONObject data1 = event1.getData();
		JSONObject data2 = event2.getData();
		JSONObject data3 = event3.getData();
		
		assertNotNull(data0);
		assertNotNull(data1);
		assertNotNull(data2);
		assertNotNull(data3);
		
		assertEquals("show", data0.getString("action"));
		assertEquals("share", data1.getString("action"));
		assertEquals("share", data2.getString("action"));
		assertEquals("close", data3.getString("action"));
		
		JSONArray network = data1.getJSONArray("network");
		
		assertNotNull(network);
		assertEquals(2, network.length());
		assertEquals("TWITTER", network.getString(0));
		assertEquals("FACEBOOK", network.getString(1));
	}
	
	public class PublicShareDialogFactory extends ShareDialogFactory{
		@Override
		public void showDialog(Context context, BeanCreationListener<SharePanelView> beanListener, ShareDialogListener listener, Object... args) {
			super.showDialog(context, beanListener, listener, args);
		}
	}
	
}
