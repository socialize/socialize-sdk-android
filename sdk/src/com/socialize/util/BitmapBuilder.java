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
package com.socialize.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Wrapper around the BitmapFactory used to externalize bitmap creation.
 * @author Jason Polites
 *
 */
public class BitmapBuilder {

	public Bitmap decode(InputStream in) {
		return BitmapFactory.decodeStream(in);
	}
	
	public Bitmap decode(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	public Bitmap crop(Bitmap source, int x, int y, int width, int height) {
		return Bitmap.createBitmap(source, x, y, width, height);
	}
	
	public Bitmap scale(Bitmap source, int width, int height) {
		return Bitmap.createScaledBitmap(source, width, height, true);
	}
}
