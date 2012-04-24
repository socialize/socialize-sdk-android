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
package com.socialize.test.unit;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.socialize.test.SocializeUnitTest;
import com.socialize.util.BitmapBuilder;
import com.socialize.util.BitmapUtils;

/**
 * @author Jason Polites
 * 
 */
public class BitmapUtilsTest extends SocializeUnitTest {

	// We can't mock bitmaps.. so can only do integration tests! :(
	public void testGetScaledBitmapVerticalCropOnReduce() {

		int width = 100;
		int height = 200;

		int scaledWidth = 50;
		int scaledHeight = 50;

		// Create a new bitmap
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		// Set the top and bottom of the bmp to black, and the center to white
		// This way we'll know that it was cropped/scaled correctly
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (y < 50 || y >= 150) {
					bitmap.setPixel(x, y, Color.GREEN);
				} else {
					bitmap.setPixel(x, y, Color.WHITE);
				}
			}
		}

		BitmapBuilder builder = new BitmapBuilder();
		BitmapUtils utils = new BitmapUtils();
		utils.setBitmapBuilder(builder);

		// We expect the lowest value to scale, and the highest to crop
		Bitmap scaled = utils.getScaledBitmap(bitmap, scaledWidth, scaledHeight);

		assertFalse(scaled.isRecycled());
		assertTrue(bitmap.isRecycled());

		assertEquals(scaledWidth, scaled.getWidth());
		assertEquals(scaledHeight, scaled.getHeight());

		// All pixels should be white
		for (int x = 0; x < scaledWidth; x++) {
			for (int y = 0; y < scaledHeight; y++) {
				int pixel = scaled.getPixel(x, y);
				assertEquals(Color.WHITE, pixel);
			}
		}

		scaled.recycle();
	}

	// We can't mock bitmaps.. so can only do integration tests! :(
	public void testGetScaledBitmapHorizontalCropOnReduce() {

		int width = 200;
		int height = 100;

		int scaledWidth = 50;
		int scaledHeight = 50;

		// Create a new bitmap
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		// Set the top and bottom of the bmp to black, and the center to white
		// This way we'll know that it was cropped/scaled correctly
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x < 50 || x >= 150) {
					bitmap.setPixel(x, y, Color.GREEN);
				} else {
					bitmap.setPixel(x, y, Color.WHITE);
				}
			}
		}

		BitmapBuilder builder = new BitmapBuilder();
		BitmapUtils utils = new BitmapUtils();
		utils.setBitmapBuilder(builder);

		// We expect the lowest value to scale, and the highest to crop
		Bitmap scaled = utils.getScaledBitmap(bitmap, scaledWidth, scaledHeight);

		assertFalse(scaled.isRecycled());
		assertTrue(bitmap.isRecycled());

		assertEquals(scaledWidth, scaled.getWidth());
		assertEquals(scaledHeight, scaled.getHeight());

		// All pixels should be white
		for (int x = 0; x < scaledWidth; x++) {
			for (int y = 0; y < scaledHeight; y++) {
				int pixel = scaled.getPixel(x, y);
				assertEquals(Color.WHITE, pixel);
			}
		}

		scaled.recycle();
	}

}
