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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public class DisplayUtils {

	private SocializeLogger logger;
	
	private float density = 1f;
	private int orientation;
	private int displayHeight;
	private int displayWidth;

	public void init(Context context) {
	
		if (context instanceof Activity) {
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			display.getMetrics(metrics);
			density = metrics.density;
			
			displayHeight = display.getHeight();
			displayWidth = display.getWidth();
			
			if (displayWidth == displayHeight) {
				orientation = Configuration.ORIENTATION_SQUARE;
			} 
			else { 
				if (displayWidth < displayHeight) {
					orientation = Configuration.ORIENTATION_PORTRAIT;
				} 
				else { 
					orientation = Configuration.ORIENTATION_LANDSCAPE;
				}
			}
		}
		else {
			String errroMsg = "Unable to determine device screen density.  Socialize must be intialized from an Activity";
			if (logger != null) {
				logger.warn(errroMsg);
			}
			else {
				System.err.println(errroMsg);
			}
		}
	}
	
	public int getDIP(float pixels) {
		return getDIP(Math.round(pixels));
	}
	
	public int getDIP(int pixels) {
		if (pixels != 0) {
			return (int) ((float) pixels * density);
		}
		return pixels;
	}
	
	public boolean isLowRes() {
		return density <= 1.0f;
	}
	
	public boolean isLDPI() {
		return density <= 0.75f;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public int getDisplayWidth() {
		return displayWidth;
	}

	public int getOrientation() {
		return orientation;
	}
	
	public boolean isLandscape() {
		return orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
}
