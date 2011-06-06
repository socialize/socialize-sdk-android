/*
 * Copyright (c) 2011 Socialize Inc.
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
package com.socialize.test;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.AndroidIOC;
import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerBuilder;
import com.socialize.error.SocializeApiError;
import com.socialize.util.HttpUtils;

/**
 * @author jasonpolites
 *
 */
public class SocializeErrorTest extends SocializeActivityTest {

	@UsesMocks ({ContainerBuilder.class, Container.class})
	public void testSocializeApiError() throws Exception {
		final int code = 404;
		
		HttpUtils utils = new HttpUtils();
		SocializeApiError error = new SocializeApiError(utils, code);
		

		
		Container container = AndroidMock.createMock(Container.class);
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class, getActivity());
		
		AndroidMock.expect(builder.build(getActivity(), (String)null)).andReturn(container);
		AndroidMock.expect(container.getBean("httputils")).andReturn(utils).anyTimes();
		
		AndroidMock.replay(builder);
		AndroidMock.replay(container);
		
		AndroidIOC ioc = new AndroidIOC();
		ioc.init(getActivity(), builder);

		// Get the result we expect:
		
		String expected = utils.getMessageFor(code);
		
		assertEquals(expected, error.getMessage());
		assertEquals(expected, error.getLocalizedMessage());
		assertEquals(code, error.getResultCode());
		
		AndroidMock.verify(builder);
		AndroidMock.verify(container);
	}
	
}
