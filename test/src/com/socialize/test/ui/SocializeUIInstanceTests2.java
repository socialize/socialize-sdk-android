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

import android.app.Activity;
import android.content.Intent;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.api.action.comment.SocializeCommentUtils;
import com.socialize.api.action.user.SocializeUserUtils;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.action.ActionDetailActivity;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.OnCommentViewActionListener;
import com.socialize.ui.profile.ProfileActivity;

/**
 * @author Jason Polites
 *
 */
public class SocializeUIInstanceTests2 extends SocializeActivityTest {
	public void testShowCommentViewCallFlow() {
		final String url = "foobar";
		final Entity entity = Entity.newInstance(url, null);
		
		SocializeCommentUtils socialize = new SocializeCommentUtils() {
			@Override
			public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener) {
				addResult(entity);
			}
		};
		
		socialize.showCommentView(getContext(), entity);
		
		Entity entityAfter = getNextResult();
		assertNotNull(entityAfter);
		assertSame(entity, entityAfter);
	}
	
	
	@UsesMocks ({OnCommentViewActionListener.class, Intent.class})
	public void testShowCommentViewLoadsCommentActivity() {
		
		final String url = "foobar";
		final Entity entity = Entity.newInstance(url, null);
		final OnCommentViewActionListener listener = AndroidMock.createMock(OnCommentViewActionListener.class);
		
		final Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(intent.putExtra(Socialize.ENTITY_OBJECT, entity)).andReturn(intent);
		
		Activity context = new Activity() {
			@Override
			public void startActivity(Intent intent) {
				addResult(intent);
			}
		};
		
		SocializeCommentUtils socialize = new SocializeCommentUtils() {

			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				addResult(cls);
				return intent;
			}
		};
		
		AndroidMock.replay(intent);
		
		socialize.showCommentView(context, entity, listener);
		
		AndroidMock.verify(intent);
		
		Class<?> clsAfter = getNextResult();
		Intent intentAfter = getNextResult();
		
		assertEquals(CommentActivity.class, clsAfter);
		assertSame(intent, intentAfter);
		
//		OnCommentViewActionListener listenerAfter = (OnCommentViewActionListener) Socialize.STATIC_LISTENERS.get(CommentView.COMMENT_LISTENER);
//		
//		assertNotNull(listenerAfter);
//		assertSame(listener, listenerAfter);	
	}	
	
	@UsesMocks ({Intent.class})
	public void testShowProfileViewLoadsProfileActivity() {
		
		final Long userId = 123L;
		final Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(intent.putExtra(Socialize.USER_ID, userId.toString())).andReturn(intent);
		
		Activity context = new Activity() {

			@Override
			public void startActivity(Intent intent) {
				addResult(intent);
			}
		};
		
		SocializeUserUtils socialize = new SocializeUserUtils() {

			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				addResult(cls);
				return intent;
			}
		};
		
		AndroidMock.replay(intent);
		
		socialize.showUserSettingsView(context, userId);
		
		AndroidMock.verify(intent);
		
		Class<?> clsAfter = getNextResult();
		Intent intentAfter = getNextResult();
		
		assertEquals(ProfileActivity.class, clsAfter);
		assertSame(intent, intentAfter);
	}
	
	@UsesMocks ({Intent.class})
	public void testShowProfileViewForResultLoadsProfileActivity() {
		
		final Long userId = 123L;
		final int requestCode = 69;
		final Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(intent.putExtra(Socialize.USER_ID, userId.toString())).andReturn(intent);
		
		Activity context = new Activity() {

			@Override
			public void startActivityForResult(Intent intent, int requestCode) {
				addResult(intent);
				addResult(requestCode);
			}
		};
		
		SocializeUserUtils socialize = new SocializeUserUtils() {

			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				addResult(cls);
				return intent;
			}
		};
		
		AndroidMock.replay(intent);
		
		socialize.showUserSettingsViewForResult(context, userId, requestCode);
		
		AndroidMock.verify(intent);		
		
		Class<?> clsAfter = getNextResult();
		Intent intentAfter = getNextResult();
		Integer code = getNextResult();
		
		assertEquals(ProfileActivity.class, clsAfter);
		assertSame(intent, intentAfter);
		assertEquals(requestCode, code.intValue());	
	}			

	@UsesMocks ({Intent.class})
	public void test_showActionDetailViewForResult() {

		final Long userId = 101L;
		final Long actionId = 201L;
		
		final User user = new User();
		final SocializeAction action = new Comment();
		
		final Intent intent = AndroidMock.createMock(Intent.class);
		
		AndroidMock.expect(intent.putExtra(Socialize.USER_ID, userId.toString())).andReturn(intent);
		AndroidMock.expect(intent.putExtra(Socialize.ACTION_ID, actionId.toString())).andReturn(intent);
		AndroidMock.expect(intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)).andReturn(intent);
		
		user.setId(userId);
		action.setId(actionId);
		
		Activity context = new Activity() {

			@Override
			public void startActivity(Intent intent) {
				addResult(intent);
			}
		};
		
		SocializeUserUtils socialize = new SocializeUserUtils() {

			@Override
			public Intent newIntent(Activity context, Class<?> cls) {
				addResult(cls);
				return intent;
			}
		};
		
		AndroidMock.replay(intent);
		
		socialize.showUserProfileView(context, user, action);
		
		AndroidMock.verify(intent);		
		
		Class<?> clsAfter = getNextResult();
		Intent intentAfter = getNextResult();
		
		assertEquals(ActionDetailActivity.class, clsAfter);
		assertSame(intent, intentAfter);
	}		
	
}
