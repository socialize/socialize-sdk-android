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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class ActionBarButton extends LinearLayout {

	private Drawable icon;
	private ActionBarButtonListener listener;
	private Drawable background;
	private TextView textView;
	private ImageView imageView;
	private DeviceUtils deviceUtils;
	
	public ActionBarButton(Context context) {
		super(context);
	}
	
	public void init(Context context, int width, float weight) {
		
		textView = new TextView(context);
		imageView = new ImageView(context);
		
		if(width > 0) {
			width = deviceUtils.getDIP(width);
		}
		
		int leftPadding = deviceUtils.getDIP(5);
		int rightPadding = deviceUtils.getDIP(2);
		
		LayoutParams masterParams = new LayoutParams(width,deviceUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		masterParams.weight = weight;
		
		setLayoutParams(masterParams);
		
		LinearLayout content = new LinearLayout(context);
		
		LayoutParams contentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		contentParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		content.setLayoutParams(contentParams);
		
		LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		
		imageView.setImageDrawable(icon);
		imageView.setLayoutParams(iconParams);
		imageView.setPadding(leftPadding, 0, rightPadding, 0);
		
		textView.setLayoutParams(textParams);
		textView.setPadding(0, 0, 0, 0);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		textView.setTypeface(Typeface.DEFAULT_BOLD);
		textView.setTextColor(Color.WHITE);
		
		content.addView(imageView);
		content.addView(textView);
		
		
		GradientDrawable bottomLeft = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{Color.BLACK, Color.BLACK});
		
		LayerDrawable bg = new LayerDrawable(new Drawable[] {bottomLeft, background});
		
		bg.setLayerInset(1, 1, 0, 0, 1);
		
		setBackgroundDrawable(bg);
		
		addView(content);
		
		if(listener != null) {
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(ActionBarButton.this);
				}
			});
		}
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
		
		if(imageView != null) {
			imageView.setImageDrawable(icon);
		}
	}

	public void setListener(ActionBarButtonListener listener) {
		this.listener = listener;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}
	
	public void setText(String text) {
		if(this.textView != null) {
			this.textView.setText(text);
		}
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
	
	
}
