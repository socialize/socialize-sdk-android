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
package com.socialize.test.ui.auth;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.View;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.SocializeSystem;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.ioc.SocializeIOC;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookAuthClickListener;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterAuthClickListener;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.share.ShareHandler;
import com.socialize.test.ui.SocializeUIActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.share.EmailCell;
import com.socialize.ui.share.SMSCell;

/**
 * @author Jason Polites
 *
 */
public class AuthPanelViewTest extends SocializeUIActivityTest {

	public void testAuthPanelViewRenderAndClick() throws Throwable {
		
		FacebookAuthClickListener mockFBListener = new FacebookAuthClickListener() {
			@Override
			public void onClick(View view) {
				addResult(0, true);
			}
		};
		
		TwitterAuthClickListener mockTWListener = new TwitterAuthClickListener() {
			@Override
			public void onClick(View view) {
				addResult(1, true);
			}
		};	
		
		// Stub in click handlers
		SocializeIOC.registerStub("facebookAuthClickListener", mockFBListener);
		SocializeIOC.registerStub("twitterAuthClickListener", mockTWListener);
		
		SocializeSystem system = Socialize.getSocialize().getSystem();
		String[] config = system.getBeanConfig();
		
		Socialize.getSocialize().init(getContext(), config);
		
		// For some reason, this test fails when run in a suite because
		// the twitter button is not shown.  Forcing twitter config here
		Socialize.getSocialize().getConfig().setTwitterKeySecret("foo", "bar");
		
		final AuthPanelView view = SocializeAccess.getBean("authPanelView");
		
		
		final CountDownLatch latch0 = new CountDownLatch(1);
		final CountDownLatch latch1 = new CountDownLatch(1);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(view);
				latch0.countDown();
			}
		});
		
		latch0.await(10, TimeUnit.SECONDS);
		
		final FacebookSignInCell fbButton = TestUtils.findView(view, FacebookSignInCell.class);
		final TwitterSignInCell twButton = TestUtils.findView(view, TwitterSignInCell.class);
		
		assertNotNull(fbButton);
		assertNotNull(twButton);
		
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				boolean clicked = fbButton.performClick();
				addResult(2, clicked);
				addResult(3, twButton.performClick());
				latch1.countDown();
			}
		});		
		
		latch1.await(10, TimeUnit.SECONDS);
		
		assertTrue((Boolean)getResult(0));
		assertTrue((Boolean)getResult(1));
		assertTrue((Boolean)getResult(2));
		assertTrue((Boolean)getResult(3));
	}
	
	@Override
	protected void tearDown() throws Exception {
		SocializeIOC.unregisterStub("facebookAuthClickListener");
		SocializeIOC.unregisterStub("twitterAuthClickListener");
		super.tearDown();
	}
}
