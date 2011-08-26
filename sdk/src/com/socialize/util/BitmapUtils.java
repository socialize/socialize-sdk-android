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
package com.socialize.util;

import android.graphics.Bitmap;

/**
 * @author Jason Polites
 */
public class BitmapUtils {
	
	private BitmapBuilder bitmapBuilder;
	
	public BitmapUtils(BitmapBuilder bitmapBuilder) {
		super();
		this.bitmapBuilder = bitmapBuilder;
	}

	public Bitmap getScaledBitmap(Bitmap bitmap, int scaleToWidth, int scaleToHeight) {

		Bitmap original = bitmap;

		if (scaleToWidth > 0 || scaleToHeight > 0) {

			int width = bitmap.getWidth();
			int height = bitmap.getHeight();

			// scale lowest and crop highes
			if (height != scaleToHeight || width != scaleToWidth) {

				float ratio = 1.0f;

				// Scale to smallest
				if (height > width) {

					ratio = (float) scaleToWidth / (float) width;
					width = scaleToWidth;
					height = Math.round((float) height * ratio);

					bitmap = bitmapBuilder.scale(bitmap, width, height);

					width = bitmap.getWidth();
					height = bitmap.getHeight();

					if (height > scaleToHeight) {
						// crop height
						int diff = height - scaleToHeight;
						int half = Math.round((float) diff / 2.0f);
						
						bitmap = bitmapBuilder.crop(bitmap, 0, half, width, scaleToHeight);
					}
				}
				else {

					ratio = (float) scaleToHeight / (float) height;
					height = scaleToHeight;
					width = Math.round((float) width * ratio);

					bitmap = bitmapBuilder.scale(bitmap, width, height);

					width = bitmap.getWidth();
					height = bitmap.getHeight();

					if (width > scaleToWidth) {
						// crop width
						int diff = width - scaleToWidth;
						int half = Math.round((float) diff / 2.0f);
						bitmap = bitmapBuilder.crop(bitmap, half, 0, scaleToWidth, height);
					}
				}

				original.recycle();
			}
		}

		return bitmap;
	}
}
