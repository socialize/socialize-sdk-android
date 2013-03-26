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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.launcher.BaseLauncher;
import com.socialize.launcher.Launcher;
import com.socialize.listener.SocializeInitListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 *
 */
public abstract class NewCommentsIntegrationTest extends NewCommentsNotificationTest {
	
	protected void doTest(final String launcherName, final boolean listEnabled) throws Throwable {
		
		final CountDownLatch launchLatch = new CountDownLatch(1);

		// Launcher is triggered in normal socialize context, not notification context.
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				fail();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
				SocializeConfig config = container.getBean("config");
				config.setProperty(SocializeConfig.SOCIALIZE_SHOW_COMMENT_LIST_ON_NOTIFY, String.valueOf(listEnabled));
				
				ProxyObject<Launcher> launcherProxy = container.getProxy(launcherName);
				launcherProxy.setDelegate(new BaseLauncher() {
					
					@Override
					public boolean launch(Activity context, Bundle data) {
						addResult(4, true);
						launchLatch.countDown();
						return true;
					}
				});
			}
		});
		
		super.testOnMessage();
		
		launchLatch.await(20, TimeUnit.SECONDS);
		
		Boolean after = getResult(4);
		
		assertNotNull(after);
		assertEquals(true, after.booleanValue());
	}

	@Override
	protected boolean disableLauncher() {
		return false;
	}
}
