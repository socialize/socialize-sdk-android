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
package com.socialize.test.ui.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.Socialize;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ShareType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.launcher.LaunchAction;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.SocializeLaunchActivity;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.share.InternalShareClickListener;


/**
 * @author Jason Polites
 *
 */
@UsesMocks ({ActionBarView.class, ShareMessageBuilder.class, Entity.class})
public class InternalShareClickListenerTest extends SocializeActivityTest {

	public void test_doShareInline() {
		
		final String subject = "foobar";
		final String comment = "foobar_comment";
		final String body = "foobar_body";
		
		ActionBarView actionBarView = AndroidMock.createNiceMock(ActionBarView.class, getActivity());
		ShareMessageBuilder shareMessageBuilder = AndroidMock.createMock(ShareMessageBuilder.class);
		Entity entity = AndroidMock.createMock(Entity.class);
		
		AndroidMock.expect(shareMessageBuilder.buildShareSubject(entity)).andReturn(subject);
		AndroidMock.expect(shareMessageBuilder.buildShareMessage(entity, comment, false, false)).andReturn(body);
		
		PublicInternalShareClickListener listener = new PublicInternalShareClickListener(actionBarView);
		
		listener.setShareMessageBuilder(shareMessageBuilder);
		
		AndroidMock.replay(shareMessageBuilder);
		
		listener.doShare(getActivity(), entity, comment, null);
		
		AndroidMock.verify(shareMessageBuilder);
		
		assertEquals("Share", getResult(0));
		assertEquals(subject, getResult(1));
		assertEquals(body, getResult(2));
		assertEquals(comment, getResult(3));
	}
	
	@UsesMocks ({ListenerHolder.class, ShareAddListener.class})
	public void test_doShareNonInline() {
		Entity entity = AndroidMock.createMock(Entity.class);
		ActionBarView actionBarView = AndroidMock.createNiceMock(ActionBarView.class, getActivity());
		ListenerHolder holder = AndroidMock.createMock(ListenerHolder.class);
		ShareAddListener listener = AndroidMock.createMock(ShareAddListener.class);
		
		final String comment = "foobar_comment";
		final String key = "foobar_key";
		
		PublicInternalShareClickListener clickeListener = new PublicInternalShareClickListener(actionBarView) {
			@Override
			protected void startActivity(Context context, Intent intent) {
				addResult(0, intent);
			}
		};
		
		clickeListener.inline = false;
		
		AndroidMock.expect(entity.getKey()).andReturn(key).anyTimes();
		holder.put(key, listener);
		
		clickeListener.setListenerHolder(holder);
		
		AndroidMock.replay(entity, holder);
		
		clickeListener.doShare(getActivity(), entity, comment, listener);
		
		AndroidMock.verify(entity, holder);
		
		Intent after = getResult(0);
		
		assertNotNull(after);
		
		ComponentName component = after.getComponent();
		
		assertEquals(component.getClassName(), SocializeLaunchActivity.class.getName());
		
		Bundle extras = after.getExtras();
		
		assertNotNull(extras);
		assertNotNull(extras.get(SocializeLaunchActivity.LAUNCH_ACTION));
		assertNotNull(extras.get(Socialize.ENTITY_OBJECT));
		assertNotNull(extras.get(SocializeConfig.SOCIALIZE_SHARE_IS_HTML));
		assertNotNull(extras.get(SocializeConfig.SOCIALIZE_SHARE_COMMENT));
		assertNotNull(extras.get(SocializeConfig.SOCIALIZE_SHARE_MIME_TYPE));
		assertNotNull(extras.get(SocializeConfig.SOCIALIZE_SHARE_LISTENER_KEY));
		
		assertEquals(LaunchAction.LOCAL_SHARE.name(), extras.getString(SocializeLaunchActivity.LAUNCH_ACTION));
		assertEquals(entity, extras.get(Socialize.ENTITY_OBJECT));
		assertEquals(false, extras.getBoolean(SocializeConfig.SOCIALIZE_SHARE_IS_HTML));
		assertEquals(comment, extras.getString(SocializeConfig.SOCIALIZE_SHARE_COMMENT));
		assertEquals("text/plain", extras.getString(SocializeConfig.SOCIALIZE_SHARE_MIME_TYPE));
		assertEquals(key, extras.getString(SocializeConfig.SOCIALIZE_SHARE_LISTENER_KEY));
	}
	
	public class PublicInternalShareClickListener extends InternalShareClickListener {
		
		boolean inline = true;
		
		public PublicInternalShareClickListener(ActionBarView actionBarView) {
			super(actionBarView);
		}

		@Override
		protected boolean isIncludeSocialize() {
			return false;
		}
		
		@Override
		protected boolean isHtml() {
			return false;
		}
		
		@Override
		public boolean isAvailableOnDevice(Activity parent) {
			return true;
		}
		
		@Override
		protected ShareType getShareType() {
			return ShareType.EMAIL;
		}
		
		
		@Override
		protected boolean isDoShareInline() {
			return inline;
		}

		@Override
		protected boolean isGenerateShareMessage() {
			return true;
		}

		@Override
		public void doShare(Activity parent, String title, String subject, String body, String comment) {
			addResult(0, title);
			addResult(1, subject);
			addResult(2, body);
			addResult(3, comment);
		}

		@Override
		public void doShare(Activity context, Entity entity, String comment, ShareAddListener listener) {
			super.doShare(context, entity, comment, listener);
		}
	};
}
