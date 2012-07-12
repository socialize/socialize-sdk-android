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
package com.socialize.test.blackbox;

import android.content.Context;
import com.socialize.SocializeServiceImpl;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.ioc.SocializeIOC;
import com.socialize.listener.SocializeInitListener;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeBlackboxTest extends SocializeActivityTest {

	public void testDefaultSocializeInit() {
		SocializeServiceImpl socialize = new SocializeServiceImpl() {
			@Override
			public synchronized void init(Context context, IOCContainer container, SocializeInitListener listener) {
				addResult(container);
			}
		};
		
		socialize.init(TestUtils.getActivity(this));
		
		IOCContainer container = getNextResult();
		
		assertNotNull(container);
		assertTrue(container instanceof SocializeIOC);
		
		SocializeIOC ioc = (SocializeIOC) container;
		
		assertTrue(ioc.size() > 0);
		
	}
	
}
