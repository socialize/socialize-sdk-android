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
package com.socialize.test.integration.notification;

import android.content.Context;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeException;
import com.socialize.launcher.UrlLauncher;
import com.socialize.listener.SocializeInitListener;
import com.socialize.ui.notifications.DirectUrlListener;
import com.socialize.ui.notifications.DirectUrlWebView;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 *
 */
public class DirectUrlIntegrationTest extends DirectUrlNotificationTest {
	
	@Override
	public void testOnMessage() throws Throwable {
		
		final CountDownLatch webViewLatch = new CountDownLatch(1);
		
		// Launcher is triggered in normal socialize context, not notification context.
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
				UrlLauncher urlLauncher = container.getBean(getLauncherBeanName());
				urlLauncher.setDirectUrlListener(new DirectUrlListener() {
					
					@Override
					public boolean onBeforePageLoaded(DirectUrlWebView view, String url) {
						addResult(4, url);
						webViewLatch.countDown();
						return false;
					}

					@Override
					public void onAfterPageLoaded(DirectUrlWebView view, String url) {}

					@Override
					public void onDialogClose() {}
				});
			}
		});
		
		super.testOnMessage();
		
		webViewLatch.await(10, TimeUnit.SECONDS);
		
		String urlAfter = getResult(4);
		
		assertNotNull(urlAfter);
		assertEquals(url, urlAfter);
	}

	@Override
	protected boolean disableLauncher() {
		return false;
	}
}
