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
import android.content.Intent;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.event.EventSystem;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchManager;
import com.socialize.launcher.Launcher;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.PublicSocializeLaunchActivity;
import com.socialize.ui.SocializeLaunchActivity;

/**
 * @author Jason Polites
 *
 */
public class SocializeLaunchActivityTest extends SocializeActivityTest {

	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeService.class, SocializeAuthListener.class, EventSystem.class})
	public void testOnCreate() throws Throwable {
		
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		final EventSystem eventSystem = AndroidMock.createMock(EventSystem.class);
		
		final Bundle bundle = new Bundle();
		
		final CountDownLatch latch = new CountDownLatch(2);
		
		final PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity() {

			@Override
			public IOCContainer getContainer() {
				return mockContainer;
			}

			@Override
			public void initSocialize() {
				addResult(1, "initSocialize");
				latch.countDown();
			}
			
			@Override
			protected void doAuthenticate() {
				super.doAuthenticate();
				latch.countDown();
			}

			@Override
			public SocializeService getSocialize() {
				return socialize;
			}

			@Override
			public void superOnCreate(Bundle savedInstanceState) {
				addResult(0, savedInstanceState);
			}

			@Override
			public SocializeAuthListener getAuthListener(IOCContainer container) {
				return listener;
			}

			@Override
			protected void setupLayout() {
				addResult(2, "setupLayout");
			}
		};
		
		AndroidMock.expect(mockContainer.getBean("socializeUIErrorHandler")).andReturn(null).anyTimes();
		AndroidMock.expect(mockContainer.getBean("logger")).andReturn(null).anyTimes();
		AndroidMock.expect(mockContainer.getBean("eventSystem")).andReturn(eventSystem).anyTimes();
		
		
		socialize.authenticate(activity, listener);
		
		AndroidMock.replay(mockContainer, socialize);
		
		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.onCreate(bundle);
			}
			
		});
		
		latch.await(20, TimeUnit.SECONDS);
		
		AndroidMock.verify(mockContainer, socialize);
		
		Bundle savedInstanceState = getResult(0);
		String initSocialize = getResult(1);
		String setupLayout = getResult(2);
		
		assertNotNull(initSocialize);
		assertNotNull(setupLayout);
		assertNotNull(savedInstanceState);
		
		assertEquals("setupLayout", setupLayout);
		assertEquals("initSocialize", initSocialize);
		assertSame(bundle, savedInstanceState);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeSession.class, LaunchManager.class, Launcher.class, Intent.class})
	public void testAuthListenerSuccess() {
		
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final LaunchManager launchManager = AndroidMock.createMock(LaunchManager.class);
		final Launcher launcher = AndroidMock.createMock(Launcher.class);
		final Intent intent = AndroidMock.createMock(Intent.class);
		final SocializeSession session = AndroidMock.createMock(SocializeSession.class);
	
		final Bundle extras = new Bundle(); // can't mock :/
		final String action = "foobar";
		
		extras.putString(SocializeLaunchActivity.LAUNCH_ACTION, action);
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity() {
			@Override
			public Intent getIntent() {
				return intent;
			}
		};
		
		AndroidMock.expect(intent.getExtras()).andReturn(extras);
		AndroidMock.expect(mockContainer.getBean("launchManager")).andReturn(launchManager);
		AndroidMock.expect(launchManager.getLaucher(action)).andReturn(launcher);
		AndroidMock.expect(launcher.isAsync()).andReturn(false);
		AndroidMock.expect(launcher.launch(activity, extras)).andReturn(true);
		AndroidMock.expect(launcher.shouldFinish(activity)).andReturn(true);
		
		AndroidMock.replay(intent, mockContainer, launchManager, launcher);
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onAuthSuccess(session);
		
		AndroidMock.verify(intent, mockContainer, launchManager, launcher);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeException.class})
	public void testAuthListenerAuthFail() {
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeException error = AndroidMock.createNiceMock(SocializeException.class);
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity() {
			@Override
			protected void handleError(Exception error) {
				addResult(0, error);
			}
		};
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onAuthFail(error);
		
		assertSame(error, getResult(0));
	}
	
	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeException.class})
	public void testAuthListenerError() {
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeException error = AndroidMock.createNiceMock(SocializeException.class);
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity() {
			@Override
			protected void handleError(Exception error) {
				addResult(0, error);
			}
		};
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onError(error);
		
		assertSame(error, getResult(0));
	}	
}
