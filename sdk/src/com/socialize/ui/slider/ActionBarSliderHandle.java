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
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.ui.util.CompatUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class ActionBarSliderHandle extends ActionBarSliderViewChild {

	private int height;
	private TextView text;
	private LinearLayout closeButton;
	private ImageView icon;
	private Drawables drawables;
	private String title = "";
	
	public ActionBarSliderHandle(Context context, ActionBarSliderView parent, int height) {
		super(context, parent);
		this.height = height;
	}

	public void init() {
		CompatUtils.setBackgroundDrawable(this, drawables.getDrawable("toolbar_bg.png", true, false, true));

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, height);
		params.setMargins(0,0,0,0);
		setLayoutParams(params);	
		setPadding(0, 0, 0, 0);
		setOrientation(HORIZONTAL);
		
		text = new TextView(getContext());
		text.setText(title);
		text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		text.setPadding(4, 0, 4, 0);
		text.setTypeface(Typeface.DEFAULT_BOLD);
		text.setTextColor(Color.WHITE);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		
		Drawable drawable = drawables.getDrawable("toolbar_close.png");
		
		closeButton = new LinearLayout(getContext());
		closeButton.setPadding(0, 0, 0, 0);

		CompatUtils.setBackgroundDrawable(closeButton, drawable);

		icon = new ImageView(getContext());
		
		LayoutParams textParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		textParams.weight = 1.0f;
		
		LayoutParams closeImageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		closeImageParams.weight = 0.0f;
		closeImageParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		closeImageParams.setMargins(0, 0, 0, 0);
		
		LayoutParams iconParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		iconParams.weight = 0.0f;
		iconParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		iconParams.setMargins(0, 0, 0, 0);		
		
		text.setLayoutParams(textParams);
		closeButton.setLayoutParams(closeImageParams);
		icon.setLayoutParams(iconParams);
		
		addView(icon);
		addView(text);
		addView(closeButton);
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setTitle(String str) {
		if(text != null) {
			text.setText(str);
		}
		this.title = str;
	}
	
	public void setIconImage(String imageName) {
		
		if(StringUtils.isEmpty(imageName)) {
			icon.setVisibility(GONE);
		}
		else {
			icon.setImageDrawable(drawables.getDrawable(imageName));
			icon.setVisibility(VISIBLE);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Rect rect = new Rect();
		closeButton.getHitRect(rect);
		adjustHitRect(rect);
		if(rect.contains((int)FloatMath.ceil(ev.getX()), (int)FloatMath.ceil(ev.getY()))) {
			getSlider().close();
		}
		else {
			getSlider().slide();
		}
		return true;
	}
}
