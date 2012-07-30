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
package com.socialize.ui.actionbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 * 
 */
public class ActionBarButton extends LinearLayout {

	private Drawable icon;
	private String text;

	private ActionBarButtonListener listener;
	private DisplayUtils displayUtils;
	private ActionBarItem actionBarItem;

	private IBeanFactory<ActionBarItem> actionBarItemFactory;

	public ActionBarButton(Context context) {
		super(context);
	}

	public void init(int width, float weight, Integer textColor) {

		if (width > 0) {
			width = displayUtils.getDIP(width);
		}
		
		int height = displayUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT);

		LayoutParams masterParams = new LayoutParams(width, height);
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		masterParams.weight = weight;

		setLayoutParams(masterParams);

		actionBarItem = actionBarItemFactory.getBean(textColor);
		actionBarItem.setIcon(icon);
		actionBarItem.setText(text);
		actionBarItem.init();
		
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

	public void setText(String text) {
		this.text = text;

		if (this.actionBarItem != null) {
			this.actionBarItem.setText(text);
		}
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}
 
	public ActionBarItem getActionBarItem() {
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
