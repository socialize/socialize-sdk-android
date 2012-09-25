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
package com.socialize.ui.actionbar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import com.socialize.ui.actionbar.ActionBarOptions.ColorLayout;


/**
 * @author Jason Polites
 *
 */
public class ActionBarButtonBackground extends LayerDrawable {
	
	public static final int DEFAULT_ACCENT_COLOR = Color.parseColor("#03a6dc");
	public static final int DEFAULT_STROKE_COLOR = Color.parseColor("#222222");
	public static final int DEFAULT_HIGHLIGHT_COLOR = Color.parseColor("#666666");
	public static final int DEFAULT_FILL_COLOR = Color.parseColor("#454545");
	
	public ActionBarButtonBackground(int accentHeight, int strokeWidth) {
		this(accentHeight, strokeWidth, DEFAULT_STROKE_COLOR, DEFAULT_ACCENT_COLOR, DEFAULT_FILL_COLOR, DEFAULT_HIGHLIGHT_COLOR, ColorLayout.BOTTOM);
	}
	
	public ActionBarButtonBackground(int accentHeight, int strokeWidth, Integer strokeColor, Integer accentColor, Integer fillColor, Integer highlightColor, ActionBarOptions.ColorLayout colorLayout) {
		super(new Drawable[] { 
				new PaintDrawable((strokeColor == null) ? DEFAULT_STROKE_COLOR : strokeColor),  
				new PaintDrawable((accentColor == null) ? DEFAULT_ACCENT_COLOR : accentColor), 
				new PaintDrawable((highlightColor == null) ? DEFAULT_HIGHLIGHT_COLOR : highlightColor),
				new PaintDrawable((fillColor == null) ? DEFAULT_FILL_COLOR : fillColor)});
		
		
		if(colorLayout.equals(ColorLayout.TOP)) {
			setLayerInset(1, strokeWidth, strokeWidth, 0, 0);
			setLayerInset(2, strokeWidth, accentHeight+strokeWidth, 0, 0);
			setLayerInset(3, strokeWidth, accentHeight+strokeWidth, 0, strokeWidth);
		}
		else {
			setLayerInset(1, strokeWidth, 0, 0, strokeWidth);
			setLayerInset(2, strokeWidth, 0, 0, accentHeight+strokeWidth);
			setLayerInset(3, strokeWidth, strokeWidth, 0, accentHeight+strokeWidth);
		}
	}

	public ActionBarButtonBackground(Drawable[] layers) {
		super(layers);
	}

}
