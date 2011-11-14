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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 * 
 */
public class ActionBarButton extends LinearLayout {

	private Drawable icon;
	private String text;

	private ActionBarButtonListener listener;
	private Drawable background;
	private DeviceUtils deviceUtils;
	private ActionBarItem actionBarItem;

	private IBeanFactory<ActionBarItem> actionBarItemFactory;

	public ActionBarButton(Context context) {
		super(context);
	}

	public void init(int width, float weight) {

		if (width > 0) {
			width = deviceUtils.getDIP(width);
		}

		LayoutParams masterParams = new LayoutParams(width, deviceUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		masterParams.weight = weight;

		setLayoutParams(masterParams);

		actionBarItem = actionBarItemFactory.getBean();
		actionBarItem.setIcon(icon);
		actionBarItem.setText(text);
		actionBarItem.init();

		GradientDrawable bottomLeft = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.BLACK, Color.BLACK });

		LayerDrawable bg = new LayerDrawable(new Drawable[] { bottomLeft, background });

		bg.setLayerInset(1, 1, 0, 0, 1);

		setBackgroundDrawable(bg);

		addView(actionBarItem);

		if (listener != null) {
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

		if (actionBarItem != null) {
			actionBarItem.setIcon(icon);
		}
	}

	public void setListener(ActionBarButtonListener listener) {
		this.listener = listener;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	public void setText(String text) {
		this.text = text;

		if (this.actionBarItem != null) {
			this.actionBarItem.setText(text);
		}
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public View getActionBarItem() {
		return actionBarItem;
	}

	public void setActionBarItemFactory(IBeanFactory<ActionBarItem> itemFactory) {
		this.actionBarItemFactory = itemFactory;
	}
	
	
	public void showLoading() {
		if(actionBarItem != null) {
			actionBarItem.showLoading();
		}
	}
	
	public void hideLoading() {
		if(actionBarItem != null) {
			actionBarItem.hideLoading();
		}
	}	
}
