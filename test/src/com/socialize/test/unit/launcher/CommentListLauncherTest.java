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
package com.socialize.test.unit.launcher;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.activity.ActivitySystem;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.launcher.CommentListLauncher;
import com.socialize.notifications.NotificationAuthenticator;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.EntityLoaderUtils;


/**
 * @author Jason Polites
 *
 */
public class CommentListLauncherTest extends SocializeActivityTest {

	@UsesMocks ({
		EntityLoaderUtils.class, 
		NotificationAuthenticator.class, 
		ActivitySystem.class,
		SocializeSession.class,
		SocializeAction.class})
	public void testLaunch() throws Exception {
		
		EntityLoaderUtils entityLoaderUtils = AndroidMock.createMock(EntityLoaderUtils.class);
		NotificationAuthenticator notificationAuthenticator = AndroidMock.createMock(NotificationAuthenticator.class);
		ActivitySystem activitySystem = AndroidMock.createMock(ActivitySystem.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		SocializeAction action = AndroidMock.createMock(SocializeAction.class);

		final Entity entity = Entity.newInstance("foo", "bar");
		final long id = 69;
		final String type = "COMMENT";
		
		Bundle bundle = new Bundle();
		bundle.putLong(Socialize.ACTION_ID, id);
		bundle.putString(Socialize.ACTION_TYPE, type);
		
		
		AndroidMock.expect(entityLoaderUtils.initEntityLoader()).andReturn(null);
		AndroidMock.expect(notificationAuthenticator.authenticate(TestUtils.getActivity(this))).andReturn(session);
		AndroidMock.expect(activitySystem.getAction(session, id, ActionType.valueOf(type))).andReturn(action);
		AndroidMock.expect(action.getEntity()).andReturn(entity);
		
		SocializeCommentUtils mockCommentUtils = new SocializeCommentUtils() {
			@Override
			public void showCommentView(Activity context, Entity entity) {
				addResult(0, entity);
			}
		};
		
		SocializeAccess.setCommentUtilsProxy(mockCommentUtils);
		
		AndroidMock.replay(entityLoaderUtils, notificationAuthenticator, activitySystem, action, session);
		
		CommentListLauncher launcher = new CommentListLauncher();
		launcher.setActivitySystem(activitySystem);
		launcher.setEntityLoaderUtils(entityLoaderUtils);
		launcher.setNotificationAuthenticator(notificationAuthenticator);
		
		launcher.launch(TestUtils.getActivity(this), bundle);
		
		AndroidMock.verify(entityLoaderUtils, notificationAuthenticator, activitySystem, action, session);
		
		Entity result = getResult(0);
		
		assertSame(entity, result);
	}
	
}
