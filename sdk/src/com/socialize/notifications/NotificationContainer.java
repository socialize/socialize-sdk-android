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
package com.socialize.notifications;

import android.content.Context;
import android.util.Log;
import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.Logger;
import com.socialize.config.SocializeConfig;
import com.socialize.ioc.SocializeIOC;
import com.socialize.log.SocializeLogger;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.ResourceLocator;

import java.util.concurrent.CountDownLatch;

/**
 * Houses beans used by the notification service.
 * @author Jason Polites
 */
public class NotificationContainer {

	private SocializeIOC container;
	
	static String[] NotificationBeans = {SocializeConfig.SOCIALIZE_CORE_BEANS_PATH, SocializeConfig.SOCIALIZE_NOTIFICATION_BEANS_PATH};
	
	public void onCreate(final Context context) throws Exception {
		Logger.LOG_KEY = Socialize.LOG_KEY;
		Logger.logLevel = Log.WARN;
		container = newSocializeIOC();
		final ResourceLocator locator = newResourceLocator();
		final SocializeLogger logger = newLogger();
		locator.setLogger(logger);
		ClassLoaderProvider provider = newClassLoaderProvider();
		locator.setClassLoaderProvider(provider);

		final CountDownLatch latch = new CountDownLatch(1);

		// Andorid 4.0 will not allow disk access on main UI thread
		new Thread(){
			@Override
			public void run() {
				try {
					container.init(context, locator, NotificationBeans);
				}
				catch (Exception e) {
					logger.error("Error initializing notification container", e);
				}
				finally {
					latch.countDown();
				}
			}
		}.start();

		latch.await();
	}
	
	public void onDestroy(Context context) {}
	
	public IOCContainer getContainer() {
		return container;
	}
	
	// So we can mock
	protected SocializeIOC newSocializeIOC() {
		return new SocializeIOC();
	}
	
	// So we can mock
	protected ResourceLocator newResourceLocator() {
		return new ResourceLocator();
	}
	
	// So we can mock
	protected SocializeLogger newLogger() {
		return new SocializeLogger();
	}
	
	// So we can mock
	protected ClassLoaderProvider newClassLoaderProvider() {
		return new ClassLoaderProvider();
	}
}
