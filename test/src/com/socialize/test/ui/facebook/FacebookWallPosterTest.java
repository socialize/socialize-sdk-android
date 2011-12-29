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
import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.DefaultFacebookWallPoster;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.share.ShareMessageBuilder;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	SocialNetworkListener.class,
	DeviceUtils.class,
	ShareMessageBuilder.class
})
public class FacebookWallPosterTest extends SocializeActivityTest {

	public void testPostLike() {
		
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		ShareMessageBuilder builder = AndroidMock.createMock(ShareMessageBuilder.class);
		Activity parent = getActivity();
		
		final String appName = "foobar_appname";
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String entityLink = "foobar_link";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		
		AndroidMock.expect(deviceUtils.getAppName()).andReturn(appName);
		AndroidMock.expect(builder.getEntityLink(entity, false)).andReturn(entityLink);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, String message, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
			}
		};
		
		String expectedString = "Likes foobar_link\n\nPosted from foobar_appname using Socialize for Android. http://www.getsocialize.com";
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(builder);
		
		
		poster.setDeviceUtils(deviceUtils);
		poster.setShareMessageBuilder(builder);
		poster.postLike(parent, entity, null, listener);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(builder);
		
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}
	
	public void testPostComment() {
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		DeviceUtils deviceUtils = AndroidMock.createMock(DeviceUtils.class);
		ShareMessageBuilder builder = AndroidMock.createMock(ShareMessageBuilder.class);
		Activity parent = getActivity();
		
		final String appName = "foobar_appname";
		final String entityKey = "foobar_key";
		final String entityName = "foobar_name";
		final String comment = "foobar_comment";
		final String entityLink = "foobar_link";
		
		final Entity entity = Entity.newInstance(entityKey, entityName);
		
		AndroidMock.expect(deviceUtils.getAppName()).andReturn(appName);
		AndroidMock.expect(builder.getEntityLink(entity, false)).andReturn(entityLink);
		
		DefaultFacebookWallPoster poster = new DefaultFacebookWallPoster() {

			@Override
			public void post(Activity parent, String message, SocialNetworkListener listener) {
				addResult(0, parent);
				addResult(1, message);
				addResult(2, listener);
			}
		};
		
		String expectedString = "foobar_link\n\nfoobar_comment\n\nPosted from foobar_appname using Socialize for Android. http://www.getsocialize.com";
		
		AndroidMock.replay(deviceUtils);
		AndroidMock.replay(builder);
		
		poster.setDeviceUtils(deviceUtils);
		poster.setShareMessageBuilder(builder);
		poster.postComment(parent, entity, comment, listener);
		
		SocialNetworkListener listenerAfter = getResult(2);
		String messageAfter = getResult(1);
		Activity parentAfter = getResult(0);
		
		AndroidMock.verify(deviceUtils);
		AndroidMock.verify(builder);
		
		assertSame(listener, listenerAfter);
		assertEquals(expectedString, messageAfter);
		assertSame(parent, parentAfter);
	}
}
