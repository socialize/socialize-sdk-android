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
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.android.ioc.ProxyObject;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.util.EntityLoaderUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * @author Jason Polites
 *
 */
public class DirectEntityIntegrationTest extends DirectEntityNotificationTest {

	@UsesMocks ({EntityLoaderUtils.class})
	@Override
	public void testOnMessage() throws Throwable {
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		final SocializeEntityLoader testLoader = new SocializeEntityLoader() {
			
			@Override
			public boolean canLoad(Context context, Entity entity) {
				return true;
			}

			@Override
			public void loadEntity(Activity activity, Entity entity) {
				// Parent class will use indexes 0...3
				addResult(4, entity);
				latch.countDown();
			}
		};		
		
		final EntityLoaderUtils mockUtils = AndroidMock.createMock(EntityLoaderUtils.class);
		
		// Setup a proxy for the entity loader
		SocializeAccess.setInitListener(new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				fail();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
				ProxyObject<EntityLoaderUtils> entityLoaderProxy = container.getProxy("entityLoaderUtils");
				entityLoaderProxy.setDelegate(mockUtils);
			}
		});
		

		AndroidMock.expect(mockUtils.initEntityLoader()).andReturn(testLoader);		
		AndroidMock.replay(mockUtils);
		
		// Call super
		super.testOnMessage();
		
		latch.await(10, TimeUnit.SECONDS);
		
		AndroidMock.verify(mockUtils);
		
		Entity entity = getResult(4);
		
		assertNotNull(entity);
		assertEquals(getEntityId(), entity.getId().longValue());
	}

	@Override
	protected boolean disableLauncher() {
		return false;
	}
}
