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
package com.socialize.ui.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.socialize.util.CacheableDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Jason Polites
 * 
 */
public class ImageUrlLoader {

	public CacheableDrawable loadImageFromUrl(String url, int width, int height) throws IOException {
		URL imageUrl = 	new URL(url);
		URLConnection conn = null;
		InputStream is = null;
		
		try {
			conn = imageUrl.openConnection();
			is = conn.getInputStream();
			Bitmap bitmap = newBitmapDrawable(is);
			
			if(width > 0 && height > 0) {
				Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
				bitmap.recycle();
				return newCacheableDrawable(scaled, url);
			}
			else {
				return newCacheableDrawable(bitmap, url);
			}
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	protected CacheableDrawable newCacheableDrawable(Bitmap bitmap,String url) {
		return new CacheableDrawable(bitmap, url); 
	}
	protected Bitmap newBitmapDrawable (InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

}
