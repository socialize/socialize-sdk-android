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
package com.socialize.ui.actionbar.slider.view;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.slider.AbstractSliderItem;
import com.socialize.ui.actionbar.slider.ActionBarSliderView.DisplayState;

/**
 * @author Jason Polites
 */
public class ViewSliderItem extends AbstractSliderItem {

	@Override
	public String getId() {
		return "view";
	}
	
	public ViewSliderItem(Activity context, ActionBarView actionBarView, OnActionBarEventListener onActionBarEventListener) {
		super(context, actionBarView, onActionBarEventListener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.slider.ActionBarSliderItem#getView()
	 */
	@Override
	public View getView() {
		
		LinearLayout layout = new LinearLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.setLayoutParams(params);
		
		TextView text = new TextView(context);
		text.setText("Test");
		
		layout.addView(text);
		
		return layout;
	}

	@Override
	public int getSliderContentHeight() {
		return 75;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.slider.ActionBarSliderItem#getStartPosition()
	 */
	@Override
	public DisplayState getStartPosition() {
		return DisplayState.MAXIMIZE;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.slider.ActionBarSliderItem#isRestore()
	 */
	@Override
	public boolean isPeekOnClose() {
		return true;
	}

	@Override
	public String getIconImage() {
		return "icon_view.png";
	}

	@Override
	public String getTitle() {
		return "More Info";
	}
}
