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
package com.socialize.ui.button;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeButton extends LinearLayout {
	
	public static enum TEXT_ALIGN {LEFT, CENTER, RIGHT};
	
	private Drawables drawables;
	private Colors colors;
	private DeviceUtils deviceUtils;
	private ImageView imageView = null;
	private TextView textView = null;
	
	private Integer height = 32;
	private Integer width = null;
	private int textSize = 12;
	
	private String text = "";
	private String imageName;
	
	private boolean bold = false;
	private boolean italic = false;
	
	private String bottomColor = Colors.BUTTON_BOTTOM;
	private String topColor = Colors.BUTTON_TOP;
	private String strokeColor = Colors.BUTTON_STROKE;
	
	private String textAlign = "left";

	public SocializeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SocializeButton(Context context) {
		super(context);
	}

	public void init() {
		int padding = deviceUtils.getDIP(8);
		int radius = deviceUtils.getDIP(4);
		int textPadding = deviceUtils.getDIP(4);
		int bottom = colors.getColor(bottomColor);
		int top = colors.getColor(topColor);
		int stroke = colors.getColor(strokeColor);
		
		GradientDrawable background = makeGradient(bottom, top);
		
		background.setCornerRadius(radius);
		background.setStroke(1, stroke);
		
		int pWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
		int pHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
		
		if(width == null) {
			pWidth = LinearLayout.LayoutParams.FILL_PARENT;
		}
		else if(width > 0) {
			pWidth = deviceUtils.getDIP(width);
		}
		
		if(height == null) {
			pHeight = LinearLayout.LayoutParams.FILL_PARENT;
		}
		else if(height > 0) {
			pHeight = deviceUtils.getDIP(height);
		}
			
		LayoutParams fill = makeLayoutParams(pWidth, pHeight);

		fill.setMargins(padding, padding, padding, padding);
		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(fill);
		setBackgroundDrawable(background);
		setPadding(padding, padding, padding, padding);
		setClickable(true);
		
		TEXT_ALIGN align = TEXT_ALIGN.LEFT;
		
		if(!StringUtils.isEmpty(textAlign)) {
			textAlign = textAlign.trim().toUpperCase();
			try {
				align = TEXT_ALIGN.valueOf(textAlign);
			}
			catch (Exception ignore) {
				ignore.printStackTrace();
			}
		}
		
		switch (align) {
			case LEFT:
				setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				break;
	
			case CENTER:
				setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
				break;
	
			case RIGHT:
				setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
				break;
	
			default:
				break;
		}
		
		LayoutParams imageLayout = makeLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams textLayout = makeLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		textView = makeTextView();
		textView.setTextColor(Color.WHITE);
		
		if(bold) {
			if(italic) {
				textView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD_ITALIC);
			}
			else {
				textView.setTypeface(Typeface.DEFAULT_BOLD);
			}
		}
		else if(italic) {
			textView.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		}
		
		setTextSize(textSize);
		
		textView.setText(text);
		textView.setLayoutParams(textLayout);
		
		if(!StringUtils.isEmpty(imageName)) {
			imageView = makeImageView();
			imageView.setImageDrawable(drawables.getDrawable(imageName));
			imageView.setLayoutParams(imageLayout);
			
			addView(imageView);
			
			textView.setPadding(textPadding, 0, 0, 0);
		}
		else {
			textView.setPadding(0, 0, 0, 0);
		}
		
		addView(textView);
	}
	// use a method so we can mock
	protected ImageView makeImageView() {
		return new ImageView(getContext());
	}
	// use a method so we can mock
	protected TextView makeTextView() {
		return new TextView(getContext());
	}
	// use a method so we can mock
	protected LayoutParams makeLayoutParams(int width, int height) {
		return new LinearLayout.LayoutParams(width, height);
	}

	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
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

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setTextSize(int textSize) {
		if(textView != null) {
			textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, textSize);
		}
		
		this.textSize = textSize;
	}
	
	public void setText(String text) {
		if(textView != null) {
			textView.setText(text);
		}
		this.text = text;
	}

	public void setBottomColor(String bottomColor) {
		this.bottomColor = bottomColor;
	}

	public void setTopColor(String topColor) {
		this.topColor = topColor;
	}

	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}
}
