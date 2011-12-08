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
package com.socialize.test.ui.facebook;

import android.app.Activity;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.facebook.DefaultFacebookWallPoster;
import com.socialize.ui.facebook.FacebookWallPostListener;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	FacebookWallPostListener.class,
	DeviceUtils.class
})
public class FacebookWallPosterTest extends SocializeActivityTest {

	public void testPostLike() {
		
		FacebookWallPostListener listener = AndroidMock.createMock(FacebookWallPostListener.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Activity parent = getActivity();
		
		final String appName = "foobar_appname";
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		
		AndroidMock.expect(deviceUtils.getAppName()).andReturn(appName);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, String message, FacebookWallPostListener listener) {
				addResult(parent);
				addResult(message);
				addResult(listener);
			}
		};
		
		String expectedString = "Likes foobar_name\n\nPosted from foobar_appname using Socialize for Android. http://www.getsocialize.com";
		
		AndroidMock.replay(deviceUtils);
		
		poster.setDeviceUtils(deviceUtils);
		poster.postLike(parent, entityKey, entityName, null, false, listener);
		
		FacebookWallPostListener listenerAfter = getNextResult();
		String messageAfter = getNextResult();
		Activity parentAfter = getNextResult();
		
		AndroidMock.verify(deviceUtils);
		
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}
	
	public void testPostComment() {
		FacebookWallPostListener listener = AndroidMock.createMock(FacebookWallPostListener.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		Activity parent = getActivity();
		
		final String appName = "foobar_appname";
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String comment = "foobar_comment";
		
		AndroidMock.expect(deviceUtils.getAppName()).andReturn(appName);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, String message, FacebookWallPostListener listener) {
				addResult(parent);
				addResult(message);
				addResult(listener);
			}
		};
		
		String expectedString = "foobar_name\n\nfoobar_comment\n\nPosted from foobar_appname using Socialize for Android. http://www.getsocialize.com";
		
		AndroidMock.replay(deviceUtils);
		
		poster.setDeviceUtils(deviceUtils);
		poster.postComment(parent, entityKey, entityName, comment, false, listener);
		
		FacebookWallPostListener listenerAfter = getNextResult();
		String messageAfter = getNextResult();
		Activity parentAfter = getNextResult();
		
		AndroidMock.verify(deviceUtils);
		
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}
	
	public void testPost1() {

	}	
	
	public void testPost2() {
		
	}		
}
