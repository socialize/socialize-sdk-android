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
package com.socialize.test.ui.auth;

import android.view.View;
import android.widget.TextView;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.networks.facebook.FacebookButton;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.auth.AuthConfirmDialogView;
import com.socialize.ui.auth.AuthRequestDialogView;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogViewTest extends SocializeUnitTest {

	
	// Just test that the view renders ok
	@Deprecated
	@UsesMocks ({DisplayUtils.class, FacebookButton.class})
	public void testAuthRequestDialogViewInit() {
		
		String text = "foobar";
		DisplayUtils deviceUtils = AndroidMock.createMock(DisplayUtils.class);
		FacebookButton button = AndroidMock.createMock(FacebookButton.class, getContext());
		AndroidMock.expect(deviceUtils.getDIP(AndroidMock.anyInt())).andReturn(1).anyTimes();
		
		AndroidMock.replay(deviceUtils);
		
		AuthRequestDialogView view = new AuthConfirmDialogView(getContext()) {
			@Override
			public void addView(View child) {
				addResult(child);
			}
		};
		
		view.setDisplayUtils(deviceUtils);
		view.setFacebookSignInButton(button);
		view.setText(text);
		view.init();
		
		View result0 = getNextResult();
		View result1 = getNextResult();
		
		assertNotNull(result0);
		assertNotNull(result1);
		
		assertTrue((result0 instanceof TextView));
		assertTrue((result1 instanceof FacebookButton));
		
		TextView tv = (TextView) result0;
		
		assertSame(button, result1);
		assertEquals(text, tv.getText().toString());
	}
	
}
