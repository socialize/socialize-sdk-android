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
package com.socialize.test.facebook;

import android.view.View.OnClickListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookAuthClickListener;
import com.socialize.networks.facebook.FacebookButton;
import com.socialize.test.SocializeUnitTest;
import org.mockito.Mockito;

/**
 * @author Jason Polites
 *
 */
public class FacebookButtonTest extends SocializeUnitTest {

	public void testFacebookButtonInit() {
		FacebookAuthClickListener facebookAuthClickListener = Mockito.mock(FacebookAuthClickListener.class);
		SocializeAuthListener socializeAuthListener = Mockito.mock(SocializeAuthListener.class);
		
		FacebookButton button = new FacebookButton(getContext()) {
			@Override
			public void setOnClickListener(OnClickListener l) {
				addResult(l);
			}

			@Override
			protected void callSuperInit() {
				// Do nothing for this test
			}
		};
		
		button.setFacebookAuthClickListener(facebookAuthClickListener);
		button.init();
		button.setAuthListener(socializeAuthListener);
		
		OnClickListener listener = getNextResult();
		
		assertNotNull(listener);
		assertSame(facebookAuthClickListener, listener);

        Mockito.verify(	facebookAuthClickListener ).setListener(socializeAuthListener);
	}
}
