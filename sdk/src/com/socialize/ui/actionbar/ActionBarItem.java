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
package com.socialize.ui.actionbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.socialize.util.DeviceUtils;

/**
 * An aciton bar item item is a single item on an action bar button or ticker.
 * Composed of an image and text.
 * @author Jason Polites
 */
public class ActionBarItem extends LinearLayout {

	private Drawable icon;
	private String text;
	
	private ImageView imageView;
	private TextView textView;
	private DeviceUtils deviceUtils;
	
	private ViewFlipper iconFlipper;
	
	public ActionBarItem(Context context) {
		super(context);
	}
	
	public void init() {
		imageView = new ImageView(getContext());
		textView = new TextView(getContext());
		
		int leftPadding = deviceUtils.getDIP(3);
		int rightPadding = deviceUtils.getDIP(1);
		
		int leftFlipperPadding = deviceUtils.getDIP(9);
		int rightFlipperPadding = deviceUtils.getDIP(1);
		int topFlipperPadding = deviceUtils.getDIP(6);
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				
		setLayoutParams(masterParams);
		
		LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		if(icon != null) {
			imageView.setImageDrawable(icon);
		}
	
		imageView.setLayoutParams(iconParams);
		imageView.setPadding(leftPadding, 0, rightPadding, 0);
		
		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		LayoutParams progressLayoutParams = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progress.setLayoutParams(progressLayoutParams);
		progress.setPadding(leftFlipperPadding, topFlipperPadding, rightFlipperPadding, 0);
		
		LayoutParams iconFlipperParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconFlipperParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		iconFlipper = new ViewFlipper(getContext());
		iconFlipper.setLayoutParams(iconFlipperParams);
		iconFlipper.addView(imageView, 0);
		iconFlipper.addView(progress, 1);
		iconFlipper.setDisplayedChild(0);	
		
		textView.setLayoutParams(textParams);
		textView.setPadding(0, 0, 0, 0);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		textView.setTypeface(Typeface.DEFAULT_BOLD);
		textView.setTextColor(Color.WHITE);

		if(this.text != null) {
			textView.setText(text);
		}
				
		addView(iconFlipper);
		addView(textView);
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
		
		if(this.imageView != null) {
			imageView.setImageDrawable(icon);
		}
	}
	
	public void setText(String text) {
		this.text = text;
		
		if(this.textView != null) {
			this.textView.setText(text);
		}
	}

	public void showLoading() {
		if(iconFlipper != null) {
			iconFlipper.setDisplayedChild(1);
		}
	}
	
	public void hideLoading() {
		if(iconFlipper != null) {
			iconFlipper.setDisplayedChild(0);
		}
	}
	
}
