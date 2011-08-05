/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.test.unit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.auth.facebook.FacebookActivityService;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.DialogFactory;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class FacebookActivityTest extends SocializeActivityTest {

	
	@UsesMocks ({
		FacebookActivity.class,
		Intent.class, 
		FacebookService.class,
		FacebookSessionStore.class, 
		ListenerHolder.class, 
		AuthProviderListener.class,
		Facebook.class,
		Drawables.class,
		DialogFactory.class})
	public void testOnCreate() {
		
		final String appId = "foobar";
		final FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		final ListenerHolder listenerHolder = AndroidMock.createMock(ListenerHolder.class);
		final Drawables drawables = AndroidMock.createMock(Drawables.class, getActivity());
		final Facebook facebook = AndroidMock.createMock(Facebook.class, appId, drawables);
		final FacebookActivity context = AndroidMock.createMock(FacebookActivity.class);
		final Intent intent = AndroidMock.createMock(Intent.class);
		final AuthProviderListener listener = AndroidMock.createMock(AuthProviderListener.class);
		final DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class); 
		final FacebookService service = AndroidMock.createMock(FacebookService.class, context, facebook, facebookSessionStore, listener, dialogFactory);
		
		FacebookActivityService activityService = new FacebookActivityService(context) {
			@Override
			public FacebookService getFacebookService() {
				return service;
			}
		};
		
		// Can't mock bundle, so just create one with expected data.
		Bundle extras = new Bundle();
		extras.putString("appId", appId);
		
		AndroidMock.expect(context.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.getExtras()).andReturn(extras);
		AndroidMock.expect(context.getBean("facebookSessionStore")).andReturn(facebookSessionStore);
		AndroidMock.expect(context.getBean("listenerHolder")).andReturn(listenerHolder);
		AndroidMock.expect(context.getBean("drawables")).andReturn(drawables);
		AndroidMock.expect(context.getBean("dialogFactory")).andReturn(dialogFactory);
		
		service.authenticate();
		
		AndroidMock.replay(context);
		AndroidMock.replay(intent);
		AndroidMock.replay(service);
		
		activityService.onCreate();
		
		AndroidMock.verify(context);
		AndroidMock.verify(intent);
		AndroidMock.verify(service);
	}
}
