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

import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookActivity;
import com.socialize.auth.facebook.FacebookActivityService;
import com.socialize.test.SocializeActivityTest;

/**
 * @author Jason Polites
 *
 */
public class FacebookActivityTest extends SocializeActivityTest {

	
	@UsesMocks ({FacebookActivityService.class})
	public void testOnCreate() {
		
		final Bundle bundle = new Bundle();
		final FacebookActivityService service = AndroidMock.createMock(FacebookActivityService.class);
		
		FacebookActivity activity = new FacebookActivity() {
			@Override
			public FacebookActivityService getFacebookActivityService() {
				return service;
			}

			@Override
			protected void superOnCreate(Bundle bundle) {
				addResult(0, bundle);
			}
		};

		service.onCreate();
		
		AndroidMock.replay(service);
		
		activity.onCreate(bundle);
		
		AndroidMock.verify(service);
		
		Bundle after = getResult(0);
		
		assertNotNull(after);
		assertSame(bundle, after);
	}
}
