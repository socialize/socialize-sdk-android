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

import android.app.Activity;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

/**
 * @author Jason Polites
 *
 */
public abstract class AbstractSliderItem implements ActionBarSliderItem {

	protected Activity context;
	protected ActionBarView actionBarView;
	protected OnActionBarEventListener onActionBarEventListener;
	
	@Override
	public String getId() {
		return "view";
	}
	
	public AbstractSliderItem(Activity context, ActionBarView actionBarView, OnActionBarEventListener onActionBarEventListener) {
		super();
		this.context = context;
		this.actionBarView = actionBarView;
		this.onActionBarEventListener = onActionBarEventListener;
	}
	
	@Override
	public void onUpdate(ActionBarSliderView slider) {}

	@Override
	public void onClear(ActionBarSliderView slider) {}

	@Override
	public void onClose(ActionBarSliderView slider) {}

	@Override
	public void onCreate(ActionBarSliderView slider) {}

	@Override
	public void onOpen(ActionBarSliderView slider) {}
}
