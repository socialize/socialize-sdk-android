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
package com.socialize.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class SocializeActivityEntityView extends BaseView {

	private TextView textView;
	private ImageView imageView;
	private TextView countView;
	
	private int bgColor = Color.DKGRAY;
	private int strokeBottomColor = Color.BLACK;
	private int strokeTopColor = Color.GRAY;
	private int textColor = Color.WHITE;
	
	private String text = "";
	private int count = 0;
	private Drawable icon;
	
	private Drawables drawables;
	
	public SocializeActivityEntityView(Context context) {
		super(context);
	}
	
	public void init() {
		setOrientation(HORIZONTAL);
		
		GradientDrawable strokeBottom = makeGradient(strokeBottomColor, strokeBottomColor);
		GradientDrawable strokeTop = makeGradient(strokeTopColor, strokeTopColor);
		GradientDrawable base = makeGradient(bgColor, bgColor);
		
		LayerDrawable layers = new LayerDrawable(new Drawable[] {strokeBottom, strokeTop, base});
		layers.setLayerInset(0, 0, 1, 0, 0);
		layers.setLayerInset(1, 0, 0, 0, 1);
		layers.setLayerInset(2, 0, 1, 0, 1);
		
		setBackgroundDrawable(layers);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 66); // DIP
		params.gravity = (Gravity.CENTER_VERTICAL | Gravity.LEFT);
		
		setLayoutParams(params);
		
		LayoutParams textParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT); 
		textParams.gravity = (Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textParams.weight = 1.0f;
		
		textView = new TextView(getContext());
		textView.setTextColor(textColor);
		textView.setLayoutParams(textParams);
		textView.setGravity((Gravity.CENTER_VERTICAL | Gravity.LEFT));
		
		setText(text);
		
		LayoutParams imageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		imageParams.gravity = (Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		imageParams.weight = 0.0f;
		
		if(icon == null) {
			icon = drawables.getDrawable("icon_like.png");
		}
		
		imageView = new ImageView(getContext());
		imageView.setImageDrawable(icon);
		imageView.setLayoutParams(imageParams);
		
		LayoutParams countParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		countParams.gravity = (Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		countParams.weight = 0.0f;
		
		countView = new TextView(getContext());
		countView.setText(String.valueOf(count));
		countView.setTextColor(textColor);
		countView.setLayoutParams(countParams);
		countView.setGravity((Gravity.CENTER_VERTICAL | Gravity.LEFT));
		countView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		
		addView(textView);
		addView(countView);
		addView(imageView);
	}
	
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}

	public void setText(String text) {
		
		if(text != null && text.length() > 30) {
			text = text.substring(0, 30) + "...";
		}
		
		this.text = text;
		if(textView != null) {
			textView.setText(text);
		}
	}

	public void setCount(int count) {
		this.count = count;
		if(countView != null) {
			countView.setText(String.valueOf(count));
		}
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
		if(imageView != null) {
			imageView.setImageDrawable(icon);
		}
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public void setStrokeBottomColor(int strokeBottomColor) {
		this.strokeBottomColor = strokeBottomColor;
	}

	public void setStrokeTopColor(int strokeTopColor) {
		this.strokeTopColor = strokeTopColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public Drawables getDrawables() {
		return drawables;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	
}
