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

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Used exclusively while the action bar is loading.
 * CANNOT use the IOC container because by definition we haven't loaded yet :/
 * @author Jason Polites
 *
 */
public class ActionBarLoadingView extends LinearLayout {

	public ActionBarLoadingView(Context context) {
		super(context);
	}
	
	public void init(Activity activity) {
		
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = activity.getWindowManager().getDefaultDisplay();
		display.getMetrics(metrics);
		
		int padding = getDIP(4, metrics.density);
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, getDIP(ActionBarView.ACTION_BAR_HEIGHT, metrics.density));
		
		setBackgroundColor(Color.BLACK);
		
		setLayoutParams(masterParams);
		setOrientation(HORIZONTAL);
		
		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		progress.setPadding(padding, padding, padding, padding);
		
		TextView text = new TextView(getContext());
		text.setTextColor(Color.GRAY);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		text.setText("Loading...");
		text.setPadding(padding, padding, padding, padding);
		
		LayoutParams progressLayoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progressLayoutParams.weight = 0.0f;
		
		LayoutParams textParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		textParams.weight = 1.0f;
		textParams.setMargins(0, 0, 0, 0);
		
		setLayoutParams(masterParams);
		progress.setLayoutParams(progressLayoutParams);
		text.setLayoutParams(textParams);
		text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		addView(progress);
		addView(text);
		
	}

	protected int getDIP(int pixels, float density) {
		if (pixels != 0) {
			return (int) ((float) pixels * density);
		}
		return pixels;
	}
	
}
