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
package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public abstract class ClickableSectionCell extends LinearLayout {

	protected Drawables drawables;
	protected Colors colors;
	protected DeviceUtils deviceUtils;
	
	private String displayText;
	private TextView textView;
	private ImageView imageView;
	
	private Bitmap image;
	
	public ClickableSectionCell(Context context) {
		super(context);
	}
	
	public void init() {
		setBackground();
		
		LinearLayout master = new LinearLayout(getContext());
		
		LinearLayout.LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		textParams.weight = 1.0f;
		
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		iconParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		
		int margin = deviceUtils.getDIP(4);
		
		textParams.setMargins(margin*2, 0, margin, 0);
		iconParams.setMargins(margin, 0, margin, 0);
		
		textView = makeDisplayText();
		textView.setLayoutParams(textParams);
		
		imageView = makeImage();
		
		master.setLayoutParams(masterParams);
		
		ImageView arrowIcon = new ImageView(getContext());
		arrowIcon.setImageDrawable(drawables.getDrawable("arrow.png"));
		arrowIcon.setLayoutParams(iconParams);
		
		if(imageView != null) {
			master.addView(imageView);
		}
		
		master.addView(textView);
		master.addView(arrowIcon);
		
		addView(master);
	}
	
	protected TextView makeDisplayText() {
		int textColor = colors.getColor(Colors.BODY);
		TextView textView = new TextView(getContext());
		textView.setText(displayText);
		textView.setTextColor(textColor);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		return textView;
	}
	
	protected abstract ImageView makeImage();
	
	protected void setBackground() {
		int bgColor = colors.getColor(Colors.ACTIVITY_BG);
		GradientDrawable background = makeGradient(bgColor, bgColor);
		background.setCornerRadius(deviceUtils.getDIP(8));
		background.setAlpha(128);
		setBackgroundDrawable(background);
		
		int padding = deviceUtils.getDIP(4);
		
		setPadding(padding, padding, padding, padding);
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
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
		if(textView != null) {
			textView.setText(displayText);
		}
	}
	
	public void setImage(Bitmap image) {
		this.image = image;
		if(imageView != null) {
			imageView.setImageBitmap(image);
		}
	}	

	protected String getDisplayText() {
		return displayText;
	}

	protected TextView getTextView() {
		return textView;
	}

	protected ImageView getImageView() {
		return imageView;
	}

	public Bitmap getImage() {
		return image;
	}

	// So we can mock
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}	

}
