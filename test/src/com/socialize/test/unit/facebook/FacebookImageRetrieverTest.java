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
package com.socialize.test.unit.facebook;

import java.io.IOException;
import java.io.InputStream;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.auth.facebook.FacebookUrlBuilder;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.Base64Utils;
import com.socialize.util.IOUtils;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class FacebookImageRetrieverTest extends SocializeUnitTest {

	@UsesMocks ({
		IOUtils.class, 
		FacebookUrlBuilder.class, 
		Base64Utils.class, 
		InputStream.class})
	public void testGetEncodedProfileImage() throws IOException {
		
//		final String id = "foobarId";
//		final String encoded = "foobar_encoded";
//		final byte[] readBytes = {};
//		
//		IOUtils ioUtils = AndroidMock.createMock(IOUtils.class);
//		FacebookUrlBuilder facebookUrlBuilder = AndroidMock.createMock(FacebookUrlBuilder.class);
//		Base64Utils base64Utils = AndroidMock.createMock(Base64Utils.class);
//		InputStream in = AndroidMock.createMock(InputStream.class);
//		
//		AndroidMock.expect(facebookUrlBuilder.getProfileImageStream(id)).andReturn(in);
//		AndroidMock.expect(ioUtils.readBytes(in)).andReturn(readBytes);
//		AndroidMock.expect(base64Utils.encode(readBytes)).andReturn(encoded);
//		
//		AndroidMock.replay(facebookUrlBuilder);
//		AndroidMock.replay(ioUtils);
//		AndroidMock.replay(base64Utils);
//	
//		FacebookImageRetriever facebookImageRetriever = new FacebookImageRetriever();
//		
//		facebookImageRetriever.setBase64Utils(base64Utils);
//		facebookImageRetriever.setFacebookUrlBuilder(facebookUrlBuilder);
//		facebookImageRetriever.setIoUtils(ioUtils);
//		
//		assertEquals(encoded, facebookImageRetriever.getEncodedProfileImage(id));
//		
//		AndroidMock.verify(facebookUrlBuilder);
//		AndroidMock.verify(ioUtils);
//		AndroidMock.verify(base64Utils);
	}
	
}
