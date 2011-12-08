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

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.slider.ActionBarSliderItem;

/**
 * @author Jason Polites
 *
 */
public class SliderActionBarListener implements OnActionBarEventListener {
	
	private ActionBarEvent lastEvent;

	private IBeanFactory<ActionBarSliderItem> shareSliderItemFactory;
	private IBeanFactory<ActionBarSliderItem> viewSliderItemFactory;
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.OnActionBarEventListener#onGetLike(com.socialize.ui.actionbar.ActionBarView, com.socialize.entity.Like)
	 */
	@Override
	public void onGetLike(ActionBarView actionBar, Like like) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.OnActionBarEventListener#onPostLike(com.socialize.ui.actionbar.ActionBarView, com.socialize.entity.Like)
	 */
	@Override
	public void onPostLike(ActionBarView actionBar, Like like) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.OnActionBarEventListener#onPostUnlike(com.socialize.ui.actionbar.ActionBarView)
	 */
	@Override
	public void onPostUnlike(ActionBarView actionBar) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.OnActionBarEventListener#onPostShare(com.socialize.ui.actionbar.ActionBarView, com.socialize.entity.Share)
	 */
	@Override
	public void onPostShare(ActionBarView actionBar, Share share) {
		actionBar.getSlider().close();
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.OnActionBarEventListener#onGetEntity(com.socialize.ui.actionbar.ActionBarView, com.socialize.entity.Entity)
	 */
	@Override
	public void onGetEntity(ActionBarView actionBar, Entity entity) {}
	

	@Override
	public void onUpdate(ActionBarView actionBar) {
		actionBar.getSlider().close();
	}

	@Override
	public void onLoad(ActionBarView actionBar) {
		actionBar.getSlider().close();
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbar.OnActionBarEventListener#onClick(com.socialize.ui.actionbar.ActionBarView, com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent)
	 */
	@Override
	public void onClick(ActionBarView actionBar, ActionBarEvent evt) {
		
		boolean doEvent = true;
		
		if(lastEvent != null && lastEvent.equals(evt)) {
			doEvent = !actionBar.getSlider().showLastItem();
		}
		
		if(doEvent) {
			ActionBarSliderItem item = null;
			switch(evt) {
				case SHARE:
					if(shareSliderItemFactory != null) {
						item = shareSliderItemFactory.getBean(actionBar, this);
					}
					break;
				case VIEW:
					if(viewSliderItemFactory != null) {
						item = viewSliderItemFactory.getBean(actionBar, this);
					}					
					break;
			}
			
			if(item != null) {
				actionBar.getSlider().showSliderItem(item);
				lastEvent = evt;
			}
		}
	}

	public void setShareSliderItemFactory(IBeanFactory<ActionBarSliderItem> shareSliderItemFactory) {
		this.shareSliderItemFactory = shareSliderItemFactory;
	}

	public void setViewSliderItemFactory(IBeanFactory<ActionBarSliderItem> viewSliderItemFactory) {
		this.viewSliderItemFactory = viewSliderItemFactory;
	}
}
