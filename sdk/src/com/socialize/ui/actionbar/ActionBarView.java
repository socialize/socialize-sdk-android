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

import com.socialize.android.ioc.IOCContainer;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.slider.ActionBarSliderFactory;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.slider.ActionBarSliderFactory.ZOrder;
import com.socialize.ui.view.EntityView;

/**
 * @author Jason Polites
 */
public class ActionBarView extends EntityView {
	
	public static final int ACTION_BAR_HEIGHT = 44;
	public static final int ACTION_BAR_BUTTON_WIDTH = 80;
	
	private ActionBarLayoutView actionBarLayoutView;
	private ActionBarSliderView slider;
	private ActionBarSliderFactory<ActionBarSliderView> sliderFactory;
	
	private boolean entityKeyIsUrl = true;
	private String entityKey;
	private String entityName;
	
	private OnActionBarEventListener onActionBarEventListener;
	private OnActionBarEventListener systemOnActionBarEventListener;
	
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
		
		if(systemOnActionBarEventListener == null) {
			systemOnActionBarEventListener = container.getBean("sliderActionBarListener");
		}
		
		setListeners();
		
		return actionBarLayoutView;
	}
	
	
	@Override
	public void onAfterAuthenticate(IOCContainer container) {
		super.onAfterAuthenticate(container);
		sliderFactory = container.getBean("actionBarSliderFactory");
		if(sliderFactory != null) {
			slider = sliderFactory.wrap(this, ZOrder.BEHIND, 0);
		}
	}
	
	protected void setListeners() {
		if(actionBarLayoutView != null) {
			if(onActionBarEventListener != null) {
				if(systemOnActionBarEventListener != null) {
					
					actionBarLayoutView.setOnActionBarEventListener(new OnActionBarEventListener() {
						
						@Override
						public void onPostUnlike(ActionBarView actionBar) {
							systemOnActionBarEventListener.onPostUnlike(actionBar);
							onActionBarEventListener.onPostUnlike(actionBar);
						}
						
						@Override
						public void onPostShare(ActionBarView actionBar, Share share) {
							systemOnActionBarEventListener.onPostShare(actionBar, share);
							onActionBarEventListener.onPostShare(actionBar, share);
						}
						
						@Override
						public void onPostLike(ActionBarView actionBar, Like like) {
							systemOnActionBarEventListener.onPostLike(actionBar, like);
							onActionBarEventListener.onPostLike(actionBar, like);
						}
						
						@Override
						public void onGetLike(ActionBarView actionBar, Like like) {
							systemOnActionBarEventListener.onGetLike(actionBar, like);
							onActionBarEventListener.onGetLike(actionBar, like);
						}
						
						@Override
						public void onGetEntity(ActionBarView actionBar, Entity entity) {
							systemOnActionBarEventListener.onGetEntity(actionBar, entity);
							onActionBarEventListener.onGetEntity(actionBar, entity);
						}
						
						@Override
						public void onClick(ActionBarView actionBar, ActionBarEvent evt) {
							systemOnActionBarEventListener.onClick(actionBar, evt);
							onActionBarEventListener.onClick(actionBar, evt);
						}

						@Override
						public void onUpdate(ActionBarView actionBar) {
							systemOnActionBarEventListener.onUpdate(actionBar);
							onActionBarEventListener.onUpdate(actionBar);
						}
						
						@Override
						public void onLoad(ActionBarView actionBar) {
							systemOnActionBarEventListener.onLoad(actionBar);
							onActionBarEventListener.onLoad(actionBar);
						}
					});
					
				}
				else {
					actionBarLayoutView.setOnActionBarEventListener(onActionBarEventListener);
				}
			}
			else if(systemOnActionBarEventListener != null) {
				actionBarLayoutView.setOnActionBarEventListener(systemOnActionBarEventListener);
			}
		}
	}
	
	
	public String getEntityKey() {
		return entityKey;
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	protected ActionBarSliderView getSlider() {
		return slider;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.EntityView#getEntityKeys()
	 */
	@Override
	protected String[] getEntityKeys() {
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

	public boolean isEntityKeyUrl() {
		return entityKeyIsUrl;
	}

	public void setEntityKeyIsUrl(boolean entityKeyIsUrl) {
		this.entityKeyIsUrl = entityKeyIsUrl;
	}
	
	public void setOnActionBarEventListener(OnActionBarEventListener onActionBarEventListener) {
		this.onActionBarEventListener = onActionBarEventListener;
		
		if(actionBarLayoutView != null) {
			actionBarLayoutView.setOnActionBarEventListener(onActionBarEventListener);
		}
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
}