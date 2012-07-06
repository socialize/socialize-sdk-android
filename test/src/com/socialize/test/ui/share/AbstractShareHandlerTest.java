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
import android.content.Context;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.action.ShareType;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.PropagationInfoResponse;
import com.socialize.entity.SocializeAction;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.share.AbstractShareHandler;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;


/**
 * @author Jason Polites
 *
 */
public class AbstractShareHandlerTest extends SocializeActivityTest {

	@UsesMocks ({SocializeAction.class, SocialNetworkListener.class, PropagationInfoResponse.class, PropagationInfo.class})
	public void testHandle() {
		PropagationInfo info = AndroidMock.createMock(PropagationInfo.class);
		PropagationInfoResponse response = AndroidMock.createMock(PropagationInfoResponse.class);
		SocialNetworkListener listener = AndroidMock.createMock(SocialNetworkListener.class);
		SocializeAction action = AndroidMock.createMock(SocializeAction.class);
		
		final ShareType shareType = ShareType.FACEBOOK;
		final Activity context = TestUtils.getActivity(this);
		final String text = "foobar";
		
		AndroidMock.expect(action.getPropagationInfoResponse()).andReturn(response);
		AndroidMock.expect(response.getPropagationInfo(shareType)).andReturn(info);
		
		AndroidMock.replay(action, response, listener);
		
		AbstractShareHandler handler = new AbstractShareHandler() {
			
			@Override
			public boolean isAvailableOnDevice(Context context) {
				return true;
			}
			
			@Override
			protected void handle(Activity context, SocializeAction action, String text, PropagationInfo info, SocialNetworkListener listener) throws Exception {
				addResult(0, action);
				addResult(1, text);
				addResult(2, info);
			}
			
			@Override
			protected ShareType getShareType() {
				return shareType;
			}
		};
		
		handler.handle(context, action, null, text, listener);
		
		AndroidMock.verify(action, response, listener);
		
		SocializeAction actionAfter = getResult(0);
		String textAfter = getResult(1);
		PropagationInfo infoAfter = getResult(2);
		
		assertNotNull(actionAfter);
		assertNotNull(textAfter);
		assertNotNull(infoAfter);
		
		assertSame(action, actionAfter);
		assertEquals(text, textAfter);
		assertSame(info, infoAfter);
	}
}
