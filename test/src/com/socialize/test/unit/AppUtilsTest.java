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

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.config.SocializeConfig;
import com.socialize.sample.R;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.DefaultAppUtils;

/**
 * @author Jason Polites
 *
 */
public class AppUtilsTest extends SocializeUnitTest {

	@UsesMocks ({SocializeConfig.class})
	public void test_getAppUrl() {
		final String consumerKey = "foobar";
		final String host = "foo_host_bar";
		SocializeConfig config = AndroidMock.createMock(SocializeConfig.class);

		AndroidMock.expect(config.getProperty(SocializeConfig.REDIRECT_HOST)).andReturn(host);
		AndroidMock.expect(config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY)).andReturn(consumerKey);

		AndroidMock.replay(config);
		
		DefaultAppUtils utils = new DefaultAppUtils();
		utils.setConfig(config);
		
		String url = utils.getAppUrl();
		
		AndroidMock.verify(config);
		
		assertEquals(host + "/a/" + consumerKey, url);
	}
	
	public void testGetAppIconId() {
		DefaultAppUtils utils = new DefaultAppUtils();
		int appIconId = utils.getAppIconId(getContext());
		assertEquals(R.drawable.ic_icon, appIconId);
	}
	
}
