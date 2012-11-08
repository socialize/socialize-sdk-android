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
package com.socialize.ui.comment;

import android.view.View;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.ui.slider.ActionBarSliderItem;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.slider.ActionBarSliderView.DisplayState;

/**
 * @author Jason Polites
 *
 */
public class CommentEntrySliderItem implements ActionBarSliderItem {
	
	private IBeanFactory<CommentEntryView> viewFactory;
	
	private CommentEntryView commentEntryView;
	
	private CommentAddButtonListener listener;
	
	private LocalizationService localizationService;

	public CommentEntrySliderItem(CommentAddButtonListener listener) {
		super();
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#getId()
	 */
	@Override
	public String getId() {
		return "comment";
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#getView()
	 */
	@Override
	public View getView() {
		commentEntryView = viewFactory.getBean(listener);
		return commentEntryView;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#getSliderContentHeight()
	 */
	@Override
	public int getSliderContentHeight() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#getStartPosition()
	 */
	@Override
	public DisplayState getStartPosition() {
		return DisplayState.MAXIMIZE;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#isPeekOnClose()
	 */
	@Override
	public boolean isPeekOnClose() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#getIconImage()
	 */
	@Override
	public String getIconImage() {
		return "icon_comment.png";
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.slider.ActionBarSliderItem#getTitle()
	 */
	@Override
	public String getTitle() {
		return localizationService.getString(I18NConstants.COMMENT_SLIDER_TITLE);
	}

	public void setViewFactory(IBeanFactory<CommentEntryView> viewFactory) {
		this.viewFactory = viewFactory;
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	@Override
	public void onClear(ActionBarSliderView slider) {
		if(commentEntryView != null) {
			commentEntryView.reset();
		}
	}

	@Override
	public void onUpdate(ActionBarSliderView slider) {
		if(commentEntryView != null) {
			commentEntryView.update();
		}		
	}

	@Override
	public void onClose(ActionBarSliderView slider) {
		if(commentEntryView != null) {
			commentEntryView.reset();
		}
	}

	protected CommentEntryView getCommentEntryView() {
		return commentEntryView;
	}

	@Override
	public void onCreate(ActionBarSliderView slider) {}

	@Override
	public void onOpen(ActionBarSliderView slider) {
		if(commentEntryView != null) {
			commentEntryView.updateUI();
		}		
	}
}
