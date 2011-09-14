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
package com.socialize.ui.facebook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class FacebookButtonOld extends LinearLayout {
	
	private Drawables drawables;
	private Colors colors;
	private DeviceUtils deviceUtils;
	private ImageView imageView = null;
	private TextView textView = null;
	private int height = 32;
	private int textSize = 12;
	private String text = "Sign in with Facebook";
	private FacebookAuthClickListener clickListener;

	public FacebookButtonOld(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FacebookButtonOld(Context context) {
		super(context);
	}

	public void init() {
		
		int pHeight = deviceUtils.getDIP(height);
		int padding = deviceUtils.getDIP(8);
		int radius = deviceUtils.getDIP(4);
		int textPadding = deviceUtils.getDIP(4);
		int bottom = colors.getColor(Colors.FACEBOOK_BOTTOM);
		int top = colors.getColor(Colors.FACEBOOK_TOP);
		int stroke = colors.getColor(Colors.FACEBOOK_STROKE);
		
		GradientDrawable background = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
		
		background.setCornerRadius(radius);
		background.setStroke(1, stroke);

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, pHeight);

		fill.setMargins(0,0,0,0);
		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(fill);
		setBackgroundDrawable(background);
		setPadding(padding, padding, padding, padding);
		setClickable(true);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		imageView = new ImageView(getContext());
		imageView.setImageDrawable(drawables.getDrawable("fb_button.png"));
		imageView.setLayoutParams(imageLayout);
		
		textView = new TextView(getContext());
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, textSize);
		textView.setText(text);
		textView.setLayoutParams(textLayout);
		textView.setPadding(textPadding, 0, 0, 0);
		
		addView(imageView);
		addView(textView);
		setOnClickListener(clickListener);
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
	
	public void setText(String text) {
		if(textView != null) {
			textView.setText(text);
		}
		this.text = text;
	}
	
	public void setAuthListener(SocializeAuthListener listener) {
		clickListener.setListener(listener);
	}

	public void setClickListener(FacebookAuthClickListener clickListener) {
		this.clickListener = clickListener;
	}
}
