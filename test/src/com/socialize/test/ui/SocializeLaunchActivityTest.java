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
package com.socialize.test.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchManager;
import com.socialize.launcher.Launcher;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.SocializeLaunchActivity;

/**
 * @author Jason Polites
 *
 */
public class SocializeLaunchActivityTest extends SocializeUIActivityTest {

	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeService.class, SocializeAuthListener.class})
	public void testOnCreate() {
		
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeService socialize = AndroidMock.createMock(SocializeService.class);
		final SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		
		Bundle bundle = new Bundle();
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity() {

			@Override
			public IOCContainer getContainer() {
				return mockContainer;
			}

			@Override
			public void initSocialize() {
				addResult(1, "initSocialize");
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
		};
		
		socialize.authenticate(activity, listener);
		
		AndroidMock.replay(mockContainer, socialize);
		
		activity.onCreate(bundle);
		
		AndroidMock.verify(mockContainer, socialize);
		
		Bundle savedInstanceState = getResult(0);
		String initSocialize = getResult(1);
		
		assertNotNull(initSocialize);
		assertNotNull(savedInstanceState);
		
		assertEquals("initSocialize", initSocialize);
		assertSame(bundle, savedInstanceState);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeSession.class, LaunchManager.class, Launcher.class, Intent.class})
	public void testAuthListenerSuccess() {
		
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeErrorHandler errorHandler = AndroidMock.createMock(SocializeErrorHandler.class);
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
		AndroidMock.expect(mockContainer.getBean("socializeUIErrorHandler")).andReturn(errorHandler);
		AndroidMock.expect(mockContainer.getBean("launchManager")).andReturn(launchManager);
		AndroidMock.expect(launchManager.getLaucher(action)).andReturn(launcher);
		AndroidMock.expect(launcher.launch(activity, extras)).andReturn(true);
		
		AndroidMock.replay(intent, mockContainer, launchManager, launcher);
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onAuthSuccess(session);
		
		AndroidMock.verify(intent, mockContainer, launchManager, launcher);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeException.class})
	public void testAuthListenerAuthFail() {
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeErrorHandler errorHandler = AndroidMock.createMock(SocializeErrorHandler.class);
		final SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity();
		
		AndroidMock.expect(mockContainer.getBean("socializeUIErrorHandler")).andReturn(errorHandler);
		
		error.printStackTrace();
		errorHandler.handleError(activity, error);
		
		AndroidMock.replay(mockContainer, errorHandler, error);
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onAuthFail(error);

		AndroidMock.verify(mockContainer, errorHandler, error);
	}
	
	@UsesMocks ({IOCContainer.class, SocializeErrorHandler.class, SocializeException.class})
	public void testAuthListenerError() {
		final IOCContainer mockContainer = AndroidMock.createMock(IOCContainer.class);
		final SocializeErrorHandler errorHandler = AndroidMock.createMock(SocializeErrorHandler.class);
		final SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		PublicSocializeLaunchActivity activity = new PublicSocializeLaunchActivity();
		
		AndroidMock.expect(mockContainer.getBean("socializeUIErrorHandler")).andReturn(errorHandler);
		
		error.printStackTrace();
		errorHandler.handleError(activity, error);
		
		AndroidMock.replay(mockContainer, errorHandler, error);
		
		SocializeAuthListener authListener = activity.getAuthListener(mockContainer);
		authListener.onError(error);

		AndroidMock.verify(mockContainer, errorHandler, error);
	}	
}
