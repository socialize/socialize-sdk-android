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
package com.socialize.test.core;

import android.content.Intent;
import android.os.Bundle;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.event.EventSystem;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchManager;
import com.socialize.launcher.Launcher;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.test.PublicSocializeLaunchActivity;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.SocializeLaunchActivity;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Polites
 *
 */
public class SocializeLaunchActivityTest extends SocializeActivityTest {

	public void testOnCreate() throws Throwable {
		
		final IOCContainer mockContainer = Mockito.mock(IOCContainer.class);
		final SocializeService socialize = Mockito.mock(SocializeService.class);
		final SocializeAuthListener listener = Mockito.mock(SocializeAuthListener.class);
		final EventSystem eventSystem = Mockito.mock(EventSystem.class);
		
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
		
		Mockito.when(mockContainer.getBean("socializeUIErrorHandler")).thenReturn(null);
		Mockito.when(mockContainer.getBean("logger")).thenReturn(null);
		Mockito.when(mockContainer.getBean("eventSystem")).thenReturn(eventSystem);

		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.onCreate(bundle);
			}
			
		});
		
		latch.await(20, TimeUnit.SECONDS);


        Mockito.verify(socialize).authenticate(activity, listener);

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
	
	public void testAuthListenerSuccess() {
		
		final IOCContainer mockContainer = Mockito.mock(IOCContainer.class);
		final LaunchManager launchManager = Mockito.mock(LaunchManager.class);
		final Launcher launcher = Mockito.mock(Launcher.class);
		final Intent intent = Mockito.mock(Intent.class);
		final SocializeSession session = Mockito.mock(SocializeSession.class);
	
		final Bundle extras = new Bundle(); // can't mock :/
		final String action = "foobar";
		
		extras.putString(SocializeLaunchActivity.LAUNCH_ACTION, action);
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity() {
			@Override
			public Intent getIntent() {
				return intent;
			}
		};
		
		Mockito.when(intent.getExtras()).thenReturn(extras);
		Mockito.when(mockContainer.getBean("launchManager")).thenReturn(launchManager);
		Mockito.when(launchManager.getLaucher(action)).thenReturn(launcher);
		Mockito.when(launcher.isAsync()).thenReturn(false);
		Mockito.when(launcher.launch(activity, extras)).thenReturn(true);
		Mockito.when(launcher.shouldFinish(activity)).thenReturn(true);
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onAuthSuccess(session);

        // TODO: add verify
		
	}
	
	public void testAuthListenerAuthFail() {
		final IOCContainer mockContainer = Mockito.mock(IOCContainer.class);
		final SocializeException error = Mockito.mock(SocializeException.class);
		
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
	
	public void testAuthListenerError() {
		final IOCContainer mockContainer = Mockito.mock(IOCContainer.class);
        final SocializeException error = Mockito.mock(SocializeException.class);
		
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
