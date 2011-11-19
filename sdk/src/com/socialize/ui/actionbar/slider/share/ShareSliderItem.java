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
package com.socialize.ui.actionbar.slider.share;

import android.app.Activity;
import android.view.View;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.share.ShareDialogView;
import com.socialize.ui.slider.AbstractSliderItem;
import com.socialize.ui.slider.ActionBarSliderView.DisplayState;

/**
 * @author Jason Polites
 */
public class ShareSliderItem extends AbstractSliderItem {

	private IBeanFactory<ShareDialogView> viewFactory;
	
	@Override
	public String getId() {
		return "share";
	}

	public ShareSliderItem(Activity context, ActionBarView actionBarView, OnActionBarEventListener onActionBarEventListener) {
		super(context, actionBarView, onActionBarEventListener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.slider.ActionBarSliderItem#getView()
	 */
	@Override
	public View getView() {
		return viewFactory.getBean(actionBarView, onActionBarEventListener);
	}

	@Override
	public int getSliderContentHeight() {
		return -1;
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
		return false;
	}

	public void setViewFactory(IBeanFactory<ShareDialogView> viewFactory) {
		this.viewFactory = viewFactory;
	}

	@Override
	public String getIconImage() {
		return "icon_share.png";
	}

	@Override
	public String getTitle() {
		return "Share";
	}
}
