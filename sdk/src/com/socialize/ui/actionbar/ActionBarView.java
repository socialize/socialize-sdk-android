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
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import com.socialize.entity.Entity;
import com.socialize.ui.view.EntityView;

/**
 * @author Jason Polites
 */
public class ActionBarView extends EntityView {
	
	public static final int ACTION_BAR_HEIGHT = 44;
	public static final int ACTION_BAR_BUTTON_WIDTH = 80;
	
	private ActionBarLayoutView actionBarLayoutView;
//	private ActionBarSliderView slider;
//	private ActionBarSliderFactory<ActionBarSliderView> sliderFactory;
	private ActionBarListener actionBarListener;
	
	private Entity entity;
	
	private OnActionBarEventListener onActionBarEventListener;
//	private SliderActionBarListener sliderActionBarEventListener;
	
	public ActionBarView(Context context) {
		super(context);
	}
	
	public ActionBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public View getLoadingView() {
		ActionBarLoadingView loadingView = new ActionBarLoadingView(getContext());
		loadingView.init(getActivity());
		return loadingView;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getView(android.os.Bundle, java.lang.Object[])
	 */
	@Override
	protected View getView(Bundle bundle, Object... entityKeys) {
		if(actionBarLayoutView == null) {
			actionBarLayoutView = container.getBean("actionBarLayoutView", this);
		}
		
//		if(sliderActionBarEventListener == null) {
//			sliderActionBarEventListener = container.getBean("sliderActionBarListener");
//		}
		
		setListeners();
		
		if(actionBarListener != null) {
			actionBarListener.onCreate(this);
		}
		
		return actionBarLayoutView;
	}
	
	
//	@Override
//	public void onAfterAuthenticate(IOCContainer container) {
//		super.onAfterAuthenticate(container);
//		sliderFactory = container.getBean("actionBarSliderFactory");
//		if(sliderFactory != null) {
//			slider = sliderFactory.wrap(this, ZOrder.BEHIND, 0);
//		}
//	}
	
	protected void setListeners() {
		if(actionBarLayoutView != null) {
			if(onActionBarEventListener != null) {
				actionBarLayoutView.setOnActionBarEventListener(onActionBarEventListener);
				
//				if(sliderActionBarEventListener != null) {
//					sliderActionBarEventListener.setDelegate(onActionBarEventListener);
//					actionBarLayoutView.setOnActionBarEventListener(sliderActionBarEventListener);
//				}
//				else {
//					actionBarLayoutView.setOnActionBarEventListener(onActionBarEventListener);
//				}
			}
//			else if(sliderActionBarEventListener != null) {
//				actionBarLayoutView.setOnActionBarEventListener(sliderActionBarEventListener);
//			}
		}
	}
	
	@Override
	public void onViewRendered(int width, int height) {
		super.onViewRendered(width, height);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

//	protected ActionBarSliderView getSlider() {
//		return slider;
//	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getEntityKeys()
	 */
	@Override
	protected String[] getBundleKeys() {
		return null;
	}
	
	@Override
	public void showError(Context context, Exception e) {
		// Don't display popup
		e.printStackTrace();
	}

	@Override
	protected View getEditModeView() {
		return new ActionBarEditView(getContext());
	}

	public void setOnActionBarEventListener(OnActionBarEventListener onActionBarEventListener) {
		this.onActionBarEventListener = onActionBarEventListener;
		setListeners();
	}

	public void refresh() {
		if(actionBarLayoutView != null) {
			actionBarLayoutView.reload();
		}
	}
	
	public void stopTicker() {
		if(actionBarLayoutView != null) {
			actionBarLayoutView.stopTicker();
		}
	}
	
	public void startTicker() {
		if(actionBarLayoutView != null) {
			actionBarLayoutView.startTicker();
		}
	}

	public void setActionBarListener(ActionBarListener actionBarListener) {
		this.actionBarListener = actionBarListener;
	}
}