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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.SocializeSystem;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.ioc.SocializeIOC;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.share.ShareHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.share.EmailCell;
import com.socialize.ui.share.SMSCell;
import com.socialize.ui.share.SharePanelView;

/**
 * @author Jason Polites
 *
 */
public class SharePanelViewTest extends SocializeActivityTest {

	public void testEmailLaunchesEmailClient() throws Throwable {
		
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		
		final CountDownLatch latch1 = new CountDownLatch(1);
		
		// Stub in email handler
		ShareHandler mockEmailHandler = new ShareHandler() {
			
			@Override
			public boolean isAvailableOnDevice(Context context) {
				return true;
			}
			
			@Override
			public void handle(Activity context, SocializeAction action, Location location, String text, SocialNetworkListener listener) {
				addResult(1, action);
				latch1.countDown();
			}
		};
		
		SocializeIOC.registerStub("emailShareHandler", mockEmailHandler);
		
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("message/rfc822");
		
		SocializeSystem system = Socialize.getSocialize().getSystem();
		String[] config = system.getBeanConfig();
		
		Socialize.getSocialize().init(getContext(), config);
		
		final SharePanelView view = SocializeAccess.getBean("sharePanelView");
		view.setEntity(entity);
		view.setDisplayOptions(ShareUtils.DEFAULT);
		
		final CountDownLatch latch0 = new CountDownLatch(1);
		
		final Activity activity = TestUtils.getActivity(this);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.setContentView(view);
				latch0.countDown();
			}
		});
		
		latch0.await(10, TimeUnit.SECONDS);
		
		final EmailCell emailCell = TestUtils.findView(view, EmailCell.class);
		
		assertNotNull(emailCell);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				addResult(0, emailCell.performClick());
			}
		});		
		
		latch1.await(10, TimeUnit.SECONDS);
		
		assertTrue((Boolean)getResult(0));
		
		Share action = getResult(1);
		
		assertNotNull(action);
		assertEquals(ShareType.EMAIL, action.getShareType());
	
	}	
	
	public void testSmsLaunchesSmsClient() throws Throwable {
		
		final Entity entity = Entity.newInstance("http://entity1.com", "http://entity1.com");
		
		final CountDownLatch latch1 = new CountDownLatch(1);
		
		// Stub in email handler
		ShareHandler mockSmsHandler = new ShareHandler() {
			
			@Override
			public boolean isAvailableOnDevice(Context context) {
				return true;
			}
			
			@Override
			public void handle(Activity context, SocializeAction action, Location location, String text, SocialNetworkListener listener) {
				addResult(1, action);
				latch1.countDown();
			}
		};
		
		SocializeIOC.registerStub("smsShareHandler", mockSmsHandler);
		
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.setType("message/rfc822");
		
		SocializeSystem system = Socialize.getSocialize().getSystem();
		String[] config = system.getBeanConfig();
		
		Socialize.getSocialize().init(getContext(), config);
		
		final SharePanelView view = SocializeAccess.getBean("sharePanelView");
		view.setEntity(entity);
		view.setDisplayOptions(ShareUtils.DEFAULT);
		
		final CountDownLatch latch0 = new CountDownLatch(1);
		
		final Activity activity = TestUtils.getActivity(this);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				activity.setContentView(view);
				latch0.countDown();
			}
		});
		
		latch0.await(10, TimeUnit.SECONDS);
		
		final SMSCell cell = TestUtils.findView(view, SMSCell.class);
		
		assertNotNull(cell);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				addResult(0, cell.performClick());
			}
		});		
		
		latch1.await(10, TimeUnit.SECONDS);
		
		assertTrue((Boolean)getResult(0));
		
		Share action = getResult(1);
		
		assertNotNull(action);
		assertEquals(ShareType.SMS, action.getShareType());
	}		
}
