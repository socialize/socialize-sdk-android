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
import com.socialize.test.util.TestUtils;
import com.socialize.util.EntityLoaderUtils;
import org.mockito.Mockito;


/**
 * @author Jason Polites
 *
 */
public class CommentListLauncherTest extends SocializeActivityTest {

	public void testLaunch() throws Exception {
		
        EntityLoaderUtils entityLoaderUtils = Mockito.mock(EntityLoaderUtils.class);
		NotificationAuthenticator notificationAuthenticator = Mockito.mock(NotificationAuthenticator.class);
		ActivitySystem activitySystem = Mockito.mock(ActivitySystem.class);
		SocializeSession session = Mockito.mock(SocializeSession.class);
		SocializeAction action = Mockito.mock(SocializeAction.class);

		final Entity entity = Entity.newInstance("foo", "bar");
		final long id = 69;
		final String type = "COMMENT";
		
		Bundle bundle = new Bundle();
		bundle.putLong(Socialize.ACTION_ID, id);
		bundle.putString(Socialize.ACTION_TYPE, type);

		Mockito.when(entityLoaderUtils.initEntityLoader()).thenReturn(null);
        Mockito.when(notificationAuthenticator.authenticate(TestUtils.getActivity(this))).thenReturn(session);
		Mockito.when(activitySystem.getAction(session, id, ActionType.valueOf(type))).thenReturn(action);
		Mockito.when(action.getEntity()).thenReturn(entity);
		
		SocializeCommentUtils mockCommentUtils = new SocializeCommentUtils() {
			@Override
			public void showCommentView(Activity context, Entity entity) {
				addResult(0, entity);
			}
		};
		
		SocializeAccess.setCommentUtilsProxy(mockCommentUtils);
		
		CommentListLauncher launcher = new CommentListLauncher();
		launcher.setActivitySystem(activitySystem);
		launcher.setEntityLoaderUtils(entityLoaderUtils);
		launcher.setNotificationAuthenticator(notificationAuthenticator);
		launcher.launch(TestUtils.getActivity(this), bundle);
		
		Entity result = getResult(0);
		
		assertSame(entity, result);
	}
	
}
