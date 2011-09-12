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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class FacebookButton extends LinearLayout {
	
	private Drawables drawables;
	private Colors colors;
	private DeviceUtils deviceUtils;
	private ImageView imageView = null;
	private TextView textView = null;
	private int height = 40;
	private int textSize = 12;
	private String text = "Sign in with Facebook";

	public FacebookButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FacebookButton(Context context) {
		super(context);
	}

	public void init() {
		int pHeight = deviceUtils.getDIP(height);
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, pHeight);

		fill.setMargins(0,0,0,0);
		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(fill);
		setBackgroundColor(colors.getColor(Colors.FACEBOOK_BG));
		setPadding(0, 0, 0, 0);
		
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
		
		addView(imageView);
		addView(textView);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#29447e"));
        paint.setStrokeWidth(3);
        getLocalVisibleRect(rect);
        canvas.drawRect(rect, paint);       
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
	
	
}
