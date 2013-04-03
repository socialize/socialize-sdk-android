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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.*;
import com.socialize.ui.view.SafeViewFlipper;
import com.socialize.util.DisplayUtils;

/**
 * An action bar item item is a single item on an action bar button or ticker.
 * Composed of an image and text.
 * @author Jason Polites
 */
public class ActionBarItem extends LinearLayout {
	
	public static final int DEFAULT_TEXT_COLOR = Color.WHITE;

	private Drawable icon;
	private String text;
	
	private float textSize = -1;
	private Integer textColor;
	
	private ImageView imageView;
	private TextView textView;
	private DisplayUtils displayUtils;
	
	private boolean invertProgressStyle = false;
	
	private ViewFlipper iconFlipper;
	
	public ActionBarItem(Context context) {
		super(context);
	}
	
	public ActionBarItem(Context context, int textColor) {
		super(context);
		this.textColor = textColor;
	}
	
	public void init() {
		
		int leftMargin = displayUtils.getDIP(6);
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				
		setLayoutParams(masterParams);
		
/******************************************
 * Progress Bar
 ******************************************/
		
		int style = android.R.attr.progressBarStyleSmall;
		
		if(invertProgressStyle) {
			style = android.R.attr.progressBarStyleSmallInverse;
		}
		
		ProgressBar progress = new ProgressBar(getContext(), null, style);
		
		RelativeLayout progressLayout = new RelativeLayout(getContext());
		
		int minWidth = displayUtils.getDIP(24);
		int minHeight = displayUtils.getDIP(24);
		
		if(icon != null) {
			int intrinsicWidth = icon.getMinimumWidth();
			int intrinsicHeight = icon.getMinimumHeight();
			if(intrinsicWidth > minWidth) {
				minWidth = intrinsicWidth;
			}
			if(intrinsicHeight > minHeight) {
				minHeight = intrinsicHeight;
			}
			
		}
		
		minWidth+=leftMargin;
		
		progressLayout.setMinimumWidth(minWidth);
		progressLayout.setMinimumHeight(minHeight);
		
		LayoutParams progressLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progressLayoutParams.setMargins(leftMargin, 0, 0, 0);
		
		RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		progressParams.addRule(RelativeLayout.CENTER_VERTICAL);
		
		progressLayout.setLayoutParams(progressLayoutParams);
		progress.setLayoutParams(progressParams);
		
		progressLayout.addView(progress);
		
/******************************************
 * Icon
 ******************************************/	
		
		RelativeLayout imageLayout = new RelativeLayout(getContext());
		
		imageLayout.setMinimumWidth(minWidth);
		imageLayout.setMinimumHeight(minHeight);
		
		LayoutParams imageLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		imageLayoutParams.setMargins(leftMargin, 0, 0, 0);
		
		imageLayout.setLayoutParams(imageLayoutParams);
		
		imageView = new ImageView(getContext());
		
		RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		if(this.icon != null) {
			imageView.setImageDrawable(icon);
		}
		
		imageView.setLayoutParams(iconParams);
		
		imageLayout.addView(imageView);
		
/******************************************
 * Text
 ******************************************/	
		
		LinearLayout textLayout = new LinearLayout(getContext());
		
		LayoutParams textLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		textLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		textLayoutParams.setMargins(0, 0, 0, 0);
		textLayout.setLayoutParams(textLayoutParams);

		textView = new TextView(getContext());
		textView.setSingleLine(true);
		
		LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		if(this.text != null) {
			textView.setText(text);
		}
	
		if(textSize > 0) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		}
		else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		}
		
		textView.setTypeface(Typeface.DEFAULT_BOLD);
		textView.setTextColor((textColor == null) ? DEFAULT_TEXT_COLOR : textColor);
		textView.setLayoutParams(textParams);
		
		textLayout.addView(textView);
		
/******************************************
 * Flipper
 ******************************************/		
		
		// Flipper layout
		LayoutParams flipperParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		flipperParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		iconFlipper = new SafeViewFlipper(getContext());
		iconFlipper.setLayoutParams(flipperParams);
		iconFlipper.addView(imageLayout, 0);
		iconFlipper.addView(progressLayout, 1);
		iconFlipper.setDisplayedChild(0);	

		
/******************************************
 * Assembly
 ******************************************/		
		addView(iconFlipper);
		addView(textLayout);
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
		
		if(this.imageView != null) {
			imageView.setImageDrawable(icon);
		}
	}
	
	public void setTextSize(float size) {
		this.textSize = size;
		if(this.textView != null) {
			this.textView.setTextSize(size);
		}
	}
	
	public void setTextColor(int color) {
		this.textColor = color;
		if(this.textView != null) {
			this.textView.setTextColor(color);
		}
	}
	
	public void setText(String text) {
		this.text = text;
		
		if(this.textView != null) {
			this.textView.setText(text);
		}
	}
	
	public Drawable getIcon() {
		return icon;
	}

	public String getText() {
		return text;
	}

	public void setInvertProgressStyle(boolean invertProgressStyle) {
		this.invertProgressStyle = invertProgressStyle;
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

	public ImageView getImageView() {
		return imageView;
	}

	public TextView getTextView() {
		return textView;
	}
	
}
