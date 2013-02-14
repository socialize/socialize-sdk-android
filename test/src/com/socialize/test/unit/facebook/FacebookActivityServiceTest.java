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
package com.socialize.test.unit.facebook;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.auth.facebook.FacebookActivityService;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.auth.facebook.FacebookSessionStore;
import com.socialize.config.SocializeConfig;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookAccess;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.networks.facebook.FacebookUtilsProxy;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.DialogFactory;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class FacebookActivityServiceTest extends SocializeActivityTest {

	@UsesMocks ({FacebookActivity.class, Intent.class})
	public void testActivityFinishOnEmptyBundle() {
		
		FacebookActivity activity = AndroidMock.createMock(FacebookActivity.class);
		Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(activity.getIntent()).andReturn(intent);
		AndroidMock.expect(intent.getExtras()).andReturn(null);
		activity.finish();
		
		AndroidMock.replay(activity);
		AndroidMock.replay(intent);
		
		FacebookActivityService service = new FacebookActivityService(activity);
		service.onCreate();
		
		AndroidMock.verify(activity);
		AndroidMock.verify(intent);
	}
	
	@UsesMocks ({FacebookActivity.class, FacebookService.class})
	public void testServiceCancelCalledInActivityOnCancel() {
		
		FacebookActivity activity = AndroidMock.createMock(FacebookActivity.class);
		FacebookService facebookService = AndroidMock.createMock(FacebookService.class);
		
		facebookService.cancel(activity);
		
		AndroidMock.replay(facebookService);
		
		FacebookActivityService activityService = new FacebookActivityService(activity);
		activityService.setService(facebookService);
		
		activityService.onCancel();
		
		AndroidMock.verify(facebookService);
	}
	
	
	@UsesMocks ({FacebookFacade.class, Intent.class})
	public void testAuthorizeCallbackCalledInActivityOnActivityResult() {
		
		Intent intent = AndroidMock.createMock(Intent.class);
		FacebookFacade facebook = AndroidMock.createMock(FacebookFacade.class);
		
		FacebookActivity activity = AndroidMock.createMock(FacebookActivity.class);

		int requestCode = 69;
		int resultCode = 96;
		
		facebook.onActivityResult(activity, requestCode, resultCode, intent);
		
		AndroidMock.replay(facebook);
		
		FacebookActivityService activityService = new FacebookActivityService(activity);
		activityService.setFacebookFacade(facebook);
		
		activityService.onActivityResult(requestCode, resultCode, intent);
		
		AndroidMock.verify(facebook);
	}
	
	
	@UsesMocks ({
		FacebookActivity.class,
		Intent.class, 
		FacebookService.class,
		FacebookSessionStore.class, 
		ListenerHolder.class, 
		AuthProviderListener.class,
		Facebook.class,
		DialogFactory.class,
		SocializeConfig.class,
		FacebookUtilsProxy.class,
		SocializeLogger.class,
		FacebookFacade.class})
	public void testOnCreate() {
		
		FacebookAccess.forceV2();
		
		final String appId = "foobar";
		final FacebookSessionStore facebookSessionStore = AndroidMock.createMock(FacebookSessionStore.class);
		final FacebookFacade facebookFacade = AndroidMock.createMock(FacebookFacade.class);
		final ListenerHolder listenerHolder = AndroidMock.createMock(ListenerHolder.class);
		final Facebook facebook = AndroidMock.createMock(Facebook.class, appId);
		final FacebookUtilsProxy facebookUtils = AndroidMock.createMock(FacebookUtilsProxy.class);
		final FacebookActivity context = AndroidMock.createMock(FacebookActivity.class);
		final Intent intent = AndroidMock.createMock(Intent.class);
		final AuthProviderListener listener = AndroidMock.createMock(AuthProviderListener.class);
		final DialogFactory dialogFactory = AndroidMock.createMock(DialogFactory.class);
		final SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);
		final SocializeLogger logger = AndroidMock.createMock(SocializeLogger.class);
		
		final FacebookService service = AndroidMock.createMock(FacebookService.class, facebook, facebookSessionStore, listener, dialogFactory, logger);
		
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
		AndroidMock.expect(context.getBean("facebookFacadeFactory")).andReturn(facebookFacade);
		AndroidMock.expect(context.getBean("listenerHolder")).andReturn(listenerHolder);
		AndroidMock.expect(context.getBean("config")).andReturn(config);
		AndroidMock.expect(context.getBean("logger")).andReturn(logger);
		AndroidMock.expect(facebookUtils.getFacebook(context)).andReturn(facebook);
		
//		AndroidMock.expect(config.getBooleanProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, true)).andReturn(true);
		
		service.authenticateForRead(context, false, FacebookFacade.READ_PERMISSIONS);
		
		AndroidMock.replay(config);
		AndroidMock.replay(context);
		AndroidMock.replay(intent);
		AndroidMock.replay(service);
		
		activityService.onCreate();
		
		AndroidMock.verify(config);
		AndroidMock.verify(context);
		AndroidMock.verify(intent);
		AndroidMock.verify(service);
	}
}
