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
import android.content.Intent;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.SocializeAction;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.share.EmailShareHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class EmailShareHandlerTest extends SocializeActivityTest {

	@UsesMocks ({
		Intent.class, 
		ShareMessageBuilder.class, 
		SocializeAction.class, 
		Entity.class, 
		PropagationInfo.class, 
		SocializeConfig.class})
	public void testHandle() throws Exception {
		final Intent msg = AndroidMock.createMock(Intent.class);
		final ShareMessageBuilder shareMessageBuilder = AndroidMock.createMock(ShareMessageBuilder.class);
		final SocializeAction action = AndroidMock.createMock(SocializeAction.class);
		final Entity entity = AndroidMock.createMock(Entity.class);
		final PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		final String text = "foobar";
		final String subject = "foo_subject";
		final String body = "foo_body";
		final String title = "Share";
		final Activity context = TestUtils.getActivity(this);
		
		PublicEmailShareHandler handler = new PublicEmailShareHandler() {
			@Override
			protected Intent getIntent() {
				return msg;
			}

			@Override
			protected boolean isHtml() {
				return false;
			}

			@Override
			protected void startActivity(Activity context, Intent intent, String title) {
				addResult(0, intent);
				addResult(1, title);
			}
		};
		
		handler.setShareMessageBuilder(shareMessageBuilder);
		
		AndroidMock.expect(action.getEntity()).andReturn(entity);
		AndroidMock.expect(shareMessageBuilder.buildShareSubject(entity)).andReturn(subject);
		AndroidMock.expect(shareMessageBuilder.buildShareMessage(entity, info, text, false, true)).andReturn(body);
		
		AndroidMock.expect(msg.putExtra(Intent.EXTRA_TITLE, title)).andReturn(msg);
		AndroidMock.expect(msg.putExtra(Intent.EXTRA_TEXT, body)).andReturn(msg);
		AndroidMock.expect(msg.putExtra(Intent.EXTRA_SUBJECT, subject)).andReturn(msg);
		
		AndroidMock.replay(action, shareMessageBuilder, msg);
		
		handler.handle(context, action, text, info, null);
		
		AndroidMock.verify(action, shareMessageBuilder, msg);
		
		Intent intent = getResult(0);
		String titleAfter = getResult(1);
		
		assertNotNull(intent);
		assertNotNull(titleAfter);
		
		assertSame(msg,  intent);
		assertEquals(title, titleAfter);
	}
	
	class PublicEmailShareHandler extends EmailShareHandler {

		@Override
		public void handle(Activity context, SocializeAction action, String text, PropagationInfo info, SocialNetworkListener listener) throws Exception {
			super.handle(context, action, text, info, listener);
		}
		
	}
	
	
}
