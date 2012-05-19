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
package com.socialize.ui.slider;

import android.content.Context;
import android.view.MotionEvent;
import com.socialize.ui.util.Colors;

/**
 * @author Jason Polites
 *
 */
public class ActionBarSliderContent extends ActionBarSliderViewChild {
	
	private int height;
	private Colors colors;
	
	public ActionBarSliderContent(Context context, ActionBarSliderView parent, int height) {
		super(context, parent);
		this.height = height;
	}
	
	public void init() {
		setBackgroundColor(colors.getColor(Colors.ACTION_BAR_SLIDER_CONTENT));
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, height);
		params.setMargins(0,0,0,0);
		setLayoutParams(params);	
		setOrientation(VERTICAL);
	}
	
	@Override
	public boolean onTouchEvent (MotionEvent event) {
		return true;
	}
	
	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
