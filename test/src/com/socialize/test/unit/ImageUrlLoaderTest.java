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

import java.io.IOException;
import com.socialize.test.SocializeUnitTest;
import com.socialize.ui.image.ImageUrlLoader;
import com.socialize.util.CacheableDrawable;

/**
 * @author Isaac Mosquera
 * 
 */
public class ImageUrlLoaderTest extends SocializeUnitTest {

	public void test_loadImageFromUrl() throws IOException {
		// For this test we have to do more of a black box test
		// because a URLConnection and Bitmaps are not mockable
		// the icon.png is placed on the sdcard when the ant task is run
		final String testUrl = "file:///sdcard/icon.png";

		ImageUrlLoader loader = new ImageUrlLoader();
		// Call the method...
		CacheableDrawable cacheable = (CacheableDrawable) loader.loadImageFromUrl(testUrl, -1, -1);
		assertEquals(cacheable.getKey(), testUrl);
	}
}
