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

import android.graphics.drawable.Drawable;
import android.view.View;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.actionbar.slider.ActionBarSliderItem;
import com.socialize.ui.actionbar.slider.ActionBarSliderView.DisplayState;
import com.socialize.ui.share.ShareDialogView;

/**
 * @author Jason Polites
 */
public class ShareSliderItem implements ActionBarSliderItem {

	private IBeanFactory<ShareDialogView> viewFactory;
	private ActionBarView actionBarView;
	private OnActionBarEventListener onActionBarEventListener;
	
	@Override
	public String getId() {
		return "share";
	}

	public ShareSliderItem(ActionBarView actionBarView, OnActionBarEventListener onActionBarEventListener) {
		super();
		this.actionBarView = actionBarView;
		this.onActionBarEventListener = onActionBarEventListener;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.slider.ActionBarSliderItem#getView()
	 */
	@Override
	public View getView() {
		return viewFactory.getBean(actionBarView, onActionBarEventListener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.slider.ActionBarSliderItem#getSliderTopPosition()
	 */
	@Override
	public float getSliderTopPosition() {
		return 1.0f;
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
