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
 *
 */
public class UserActivityListItem extends LinearLayout {
	
	private TextView text;
	private ImageView icon;
	private DeviceUtils deviceUtils;
	private Colors colors;	

	public UserActivityListItem(Context context) {
		super(context);
	}
	
	public void init() {
		
		final int padding = deviceUtils.getDIP(4);
		final int margin = deviceUtils.getDIP(4);
		final int textColor = colors.getColor(Colors.BODY);
		final int iconSize = deviceUtils.getDIP(32);
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.FILL_PARENT);
		setDrawingCacheEnabled(true);
		setBackgroundColor(colors.getColor(Colors.LIST_ITEM_BG));
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(layout);
		setGravity(Gravity.TOP);
		setPadding(padding,padding,padding,padding);
		
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setGravity(Gravity.LEFT);
		contentLayout.setPadding(0, 0, 0, 0);
		
		LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentLayoutParams.setMargins(margin, 0, 0, 0);
		contentLayout.setLayoutParams(contentLayoutParams);
		
		LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		text = new TextView(getContext());
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		text.setTextColor(textColor);
		text.setLayoutParams(textLayoutParams);
		text.setMaxLines(1);
		
		contentLayout.addView(text);
		
		LinearLayout iconLayout = new LinearLayout(getContext());
		
		LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(iconSize, iconSize);
		iconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		iconLayout.setLayoutParams(iconLayoutParams);
		iconLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		icon = new ImageView(getContext());
		icon.setLayoutParams(iconLayoutParams);
		icon.setPadding(padding, padding, padding, padding);	
		
		iconLayout.addView(icon);
		
		addView(iconLayout);
		addView(contentLayout);		
	}

	public TextView getText() {
		return text;
	}

	public ImageView getIcon() {
		return icon;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

}
