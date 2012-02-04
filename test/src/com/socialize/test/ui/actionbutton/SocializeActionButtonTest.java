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
package com.socialize.test.ui.actionbutton;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.test.mock.MockContext;
import android.util.AttributeSet;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.SocializeAction;
import com.socialize.test.ui.SocializeUITestCase;
import com.socialize.ui.actionbutton.ActionButtonConfig;
import com.socialize.ui.actionbutton.SocializeActionButton;

/**
 * @author Jason Polites
 */
public class SocializeActionButtonTest extends SocializeUITestCase {
	
	public void testConstructorA() {
		new SocializeActionButton<SocializeAction>(getContext(), (AttributeSet) null) {
			@Override
			protected void doConfigBuild(Context context, AttributeSet attrs) {
				addResult(true);
			}
		};
		Boolean after = getNextResult();
		assertNotNull(after);
		assertTrue(after);
	}
	
	@UsesMocks ({MockContext.class, XmlResourceParser.class, ActionButtonConfig.class})
	public void testConfigBuild() {
		MockContext context = AndroidMock.createNiceMock(MockContext.class);
		XmlResourceParser attrs = AndroidMock.createNiceMock(XmlResourceParser.class);
		final ActionButtonConfig config = AndroidMock.createMock(ActionButtonConfig.class);
		
		config.build(context, attrs);
		
		AndroidMock.replay(attrs, config, context);
		
		PublicActionButton<SocializeAction> socializeActionButton = new PublicActionButton<SocializeAction>(getContext(), config) {
			@Override
			public ActionButtonConfig newActionButtonConfig() {
				return config;
			}

			@Override
			public void onAfterBuild(ActionButtonConfig config) {
				addResult(config);
			}
		};
		
		socializeActionButton.doConfigBuild(context, attrs);
		
		AndroidMock.verify(attrs, config);
		
		ActionButtonConfig after = getNextResult();
		assertNotNull(after);
		assertSame(config, after);
	}	
}
