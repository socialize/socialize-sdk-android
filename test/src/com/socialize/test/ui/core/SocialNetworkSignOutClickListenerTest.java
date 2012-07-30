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
package com.socialize.test.ui.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.networks.SocialNetworkSignOutTask;
import com.socialize.networks.SocializeDeAuthListener;
import com.socialize.networks.facebook.FacebookAccess;
import com.socialize.networks.facebook.FacebookUtilsImpl;
import com.socialize.networks.twitter.TwitterAccess;
import com.socialize.networks.twitter.TwitterUtilsImpl;
import com.socialize.test.PublicSocialNetworkSignOutClickListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class SocialNetworkSignOutClickListenerTest extends SocializeActivityTest {

	@Override
	protected void tearDown() throws Exception {
		TwitterAccess.revertTwitterUtilsProxy();
		FacebookAccess.revertFacebookUtilsProxy();
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testSignOutTwitter() throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		TwitterUtilsImpl mockTwitterUtils = new TwitterUtilsImpl() {
			@Override
			public void unlink(Context context) {
				latch.countDown();
			}
		};
		
		TwitterAccess.setTwitterUtilsProxy(mockTwitterUtils);
		
		Socialize.init(TestUtils.getActivity(this));
		
		PublicSocialNetworkSignOutClickListener listener = new PublicSocialNetworkSignOutClickListener();
		listener.setSignOutTaskFactory((IBeanFactory<SocialNetworkSignOutTask>) SocializeAccess.getBean("twitterSignOutTaskFactory"));
		listener.doSignOut(TestUtils.getActivity(this));
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}
	
	@SuppressWarnings("unchecked")
	public void testSignOutFacebook() throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		FacebookUtilsImpl mockFacebookUtils = new FacebookUtilsImpl() {
			
			@Override
			public void unlink(Context context, SocializeDeAuthListener listener) {
				latch.countDown();
			}
		};
		
		FacebookAccess.setFacebookUtilsProxy(mockFacebookUtils);
		
		Socialize.init(TestUtils.getActivity(this));
		
		PublicSocialNetworkSignOutClickListener listener = new PublicSocialNetworkSignOutClickListener();
		listener.setSignOutTaskFactory((IBeanFactory<SocialNetworkSignOutTask>) SocializeAccess.getBean("facebookSignOutTaskFactory"));
		listener.doSignOut(TestUtils.getActivity(this));
		assertTrue(latch.await(10, TimeUnit.SECONDS));
	}	
	
}
