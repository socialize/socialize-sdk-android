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
package com.socialize.test.unit.facebook;

import android.content.Intent;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.auth.facebook.FacebookActivityService;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.facebook.Facebook;
import com.socialize.test.SocializeActivityTest;

/**
 * @author Jason Polites
 *
 */
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
		
		facebookService.cancel();
		
		AndroidMock.replay(facebookService);
		
		FacebookActivityService activityService = new FacebookActivityService(activity);
		activityService.setService(facebookService);
		
		activityService.onCancel();
		
		AndroidMock.verify(facebookService);
	}
	
	
	@UsesMocks ({Facebook.class, Intent.class})
	public void testAuthorizeCallbackCalledInActivityOnActivityResult() {
		
		Intent intent = AndroidMock.createMock(Intent.class);
		Facebook facebook = AndroidMock.createMock(Facebook.class);
		
		FacebookActivity activity = AndroidMock.createMock(FacebookActivity.class);

		int requestCode = 69;
		int resultCode = 96;
		
		facebook.authorizeCallback(requestCode, resultCode, intent);
		
		AndroidMock.replay(facebook);
		
		FacebookActivityService activityService = new FacebookActivityService(activity);
		activityService.setFacebook(facebook);
		
		activityService.onActivityResult(requestCode, resultCode, intent);
		
		AndroidMock.verify(facebook);
	}
	
}
