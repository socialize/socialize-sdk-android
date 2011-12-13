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
package com.socialize.ui.profile.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 */
public class UserActivityListItem extends LinearLayout {
	
	private TextView text;
	private TextView date;
	private TextView title;
	private ImageView icon;
	private DeviceUtils deviceUtils;
	private Colors colors;	
	
	private LinearLayout doubleLineLayout;
	private LinearLayout singleLineLayout;
	
	private boolean singleLine = false;
	
	public UserActivityListItem(Context context) {
		super(context);
	}

	public UserActivityListItem(Context context, boolean singleLine) {
		super(context);
		this.singleLine = singleLine;
	}
	
	public void init() {
		
		final int padding = deviceUtils.getDIP(4);
		final int imagePadding = 0;
		final int margin = deviceUtils.getDIP(8);
		final int textColor = colors.getColor(Colors.BODY);
		final int titleColor = colors.getColor(Colors.TITLE);
		final int iconSize = deviceUtils.getDIP(32);
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, deviceUtils.getDIP(64));
		setDrawingCacheEnabled(true);
		setBackgroundColor(colors.getColor(Colors.LIST_ITEM_BG));
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(layout);
		setGravity(Gravity.TOP);
		setPadding(padding,padding,padding,padding);
		
		LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams dateLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		title = new TextView(getContext());
		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		title.setTextColor(textColor);
		title.setLayoutParams(titleLayoutParams);
		title.setMaxLines(1);
		title.setTypeface(Typeface.DEFAULT_BOLD);
		
		dateLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		dateLayoutParams.weight = 1.0f;
		
		date = new TextView(getContext());
		date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		date.setTextColor(textColor);
		date.setLayoutParams(textLayoutParams);
		date.setMaxLines(1);
		date.setLayoutParams(dateLayoutParams);
		date.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		date.setTextColor(titleColor);
		
		if(singleLine) {
			LinearLayout.LayoutParams singleLineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			singleLineLayoutParams.setMargins(margin, 0, 0, 0);
			
			singleLineLayout = new LinearLayout(getContext());
			singleLineLayout.setOrientation(LinearLayout.VERTICAL);
			singleLineLayout.setGravity(Gravity.LEFT);
			singleLineLayout.setPadding(0, padding, 0, 0);
			singleLineLayout.setLayoutParams(singleLineLayoutParams);
			singleLineLayout.setGravity(Gravity.TOP | Gravity.LEFT);
			singleLineLayout.addView(title);
			singleLineLayout.addView(date);	
		}
		else {
			LinearLayout.LayoutParams doubleLineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			doubleLineLayoutParams.setMargins(margin, 0, 0, 0);
			
			doubleLineLayout = new LinearLayout(getContext());
			doubleLineLayout.setOrientation(LinearLayout.VERTICAL);
			doubleLineLayout.setGravity(Gravity.TOP | Gravity.LEFT);
			doubleLineLayout.setPadding(0, padding, 0, 0);
			doubleLineLayout.setLayoutParams(doubleLineLayoutParams);
			
			text = new TextView(getContext());
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
			text.setTextColor(textColor);
			text.setLayoutParams(textLayoutParams);
			text.setMaxLines(1);
			
			doubleLineLayout.addView(title);
			doubleLineLayout.addView(text);	
			doubleLineLayout.addView(date);	
			
		}		

		LinearLayout iconLayout = new LinearLayout(getContext());
		
		LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(iconSize, iconSize);
		iconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		
		iconLayout.setLayoutParams(iconLayoutParams);
		iconLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		
		icon = new ImageView(getContext());
		icon.setLayoutParams(iconLayoutParams);
		icon.setPadding(imagePadding, 0, imagePadding, 0);	
		
		iconLayout.addView(icon);
		
		addView(iconLayout);
		if(singleLine) {
			addView(singleLineLayout);
		}
		else {
			addView(doubleLineLayout);	
		}
	}

	public void setText(String str) {
		if(this.text != null) {
			if(str.length() > 50) {
				str = str.substring(0,50) + "...";
			}
			this.text.setText(str);
		}
	}
	
	public void setTitle(String str) {
		if(this.title != null) {
			if(str.length() > 30) {
				str = str.substring(0,30) + "...";
			}
			this.title.setText(str);
		}
	}
	
	public void setDate(String dateString) {
		if(date != null) {
			date.setText(dateString);
		}
	}
	
	public void setIcon(Drawable image) {
		if(this.icon != null) {
			this.icon.setImageDrawable(image);
		}
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
