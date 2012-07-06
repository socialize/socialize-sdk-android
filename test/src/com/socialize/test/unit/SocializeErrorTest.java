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
package com.socialize.test.unit;

import java.io.InputStream;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerBuilder;
import com.socialize.error.SocializeApiError;
import com.socialize.ioc.SocializeIOC;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.HttpUtils;

/**
 * @author jasonpolites
 * 
 */
public class SocializeErrorTest extends SocializeActivityTest {

	@UsesMocks({ ContainerBuilder.class, Container.class, HttpUtils.class })
	public void testSocializeApiError() throws Exception {
		final int resultCode = 404;
		final String message = "foobar";

		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		Container container = AndroidMock.createMock(Container.class);
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class, TestUtils.getActivity(this));

		SocializeApiError error = new SocializeApiError(utils, resultCode, message);

		AndroidMock.expect(utils.getMessageFor(AndroidMock.anyInt())).andReturn("foobar").anyTimes();
		AndroidMock.expect(builder.build((InputStream[]) null)).andReturn(container);
		AndroidMock.expect(container.getBean("httputils")).andReturn(utils).anyTimes();

		AndroidMock.replay(utils);
		AndroidMock.replay(builder);
		AndroidMock.replay(container);

		SocializeIOC ioc = new SocializeIOC();
		ioc.init(TestUtils.getActivity(this), builder, (InputStream[]) null);

		// Get the result we expect:
		String expected = utils.getMessageFor(resultCode) + " (" + resultCode + "), " + message;

		assertEquals(expected, error.getMessage());
		assertEquals(expected, error.getLocalizedMessage());
		assertEquals(resultCode, error.getResultCode());

		AndroidMock.verify(utils);
		AndroidMock.verify(builder);
		AndroidMock.verify(container);
	}

}
