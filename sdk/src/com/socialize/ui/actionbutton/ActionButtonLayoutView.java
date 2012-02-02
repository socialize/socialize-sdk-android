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
package com.socialize.ui.actionbutton;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.ui.actionbar.ActionBarItem;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ActionButtonLayoutView<A extends SocializeAction> extends BaseView implements ActionButton<A> {
	
	private Entity entity;
	
	private ActionBarItem actionBarItem;
	private IBeanFactory<ActionBarItem> actionBarItemFactory;
	
	private ActionButtonHandlers actionButtonHandlers;
	private ActionButtonHandler<A> actionButtonHandler;
	
	private Drawables drawables;
	
	private Drawable imageOn;
	private Drawable imageOff;
	private Drawable imageDisabled;
	
	private ActionButtonConfig config;
	private SocializeActionButton<A> button;
	
	private ActionButtonState state;
	
	public ActionButtonLayoutView(Context context, ActionButtonConfig config, SocializeActionButton<A> button) {
		super(context);
		this.config = config;
		this.button = button;
	}

	public void init() {
		actionBarItem = actionBarItemFactory.getBean();
		actionButtonHandler = actionButtonHandlers.getHandler(config.getActionType());
		
		if(actionButtonHandler != null) {
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					actionButtonHandler.handleAction(getActivity(), ActionButtonLayoutView.this);
				}
			});			
		}
		
		if(config.getImageResIdOn() > 0) {
			try {
				imageOn = getResources().getDrawable(config.getImageResIdOn());
			} catch (Exception e) {
				// TODO: Log it baby!
				// TODO: Set default image
			}
		}
		
		if(config.getImageResIdOff() > 0) {
			try {
				imageOff = getResources().getDrawable(config.getImageResIdOff());
			} catch (Exception e) {
				// TODO: Log it baby!
				// TODO: Set default image
			}
		}
		
		if(config.getImageResIdOff() > 0) {
			try {
				imageDisabled = getResources().getDrawable(config.getImageResIdDisabled());
			} catch (Exception e) {
				// TODO: Log it baby!
				// TODO: Set default image
			}
		}		
		
		if(config.getBackgroundResId() > 0) {
			setBackgroundResource(config.getBackgroundResId());
		}
		else{
			setBackgroundColor(config.getBackgroundColor());
		}
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		setLayoutParams(params);
		
		if(imageDisabled != null) {
			actionBarItem.setIcon(imageDisabled);
			actionBarItem.setText("...");
		}
		
		actionBarItem.init();
		
		addView(actionBarItem);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public ActionButtonState getState() {
		return state;
	}

	public void setState(ActionButtonState state) {
		if(!state.equals(this.state)) {
			handleStateChange(state);
		}
		this.state = state;
	}

	protected void handleStateChange(ActionButtonState state) {
		switch (state) {
			case ACTIVE:
				actionBarItem.setText(config.getTextOn());
				actionBarItem.setIcon(imageOn);
				actionBarItem.hideLoading();
				break;
				
			case INACTIVE:
				actionBarItem.setText(config.getTextOff());
				actionBarItem.setIcon(imageOff);
				actionBarItem.hideLoading();
				break;
				
			case DISABLED:
				actionBarItem.setText(config.getTextOff());
				actionBarItem.setIcon(imageDisabled);
				actionBarItem.hideLoading();
				break;
				
			case LOADING:
				actionBarItem.setText("...");
				actionBarItem.showLoading();
				break;			
		}
	}

	
	@Override
	public void onViewRendered(int width, int height) {
		super.onViewRendered(width, height);
		refresh();
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.actionbutton.ActionButton#refresh()
	 */
	@Override
	public void refresh() {
		if(actionButtonHandler != null) {
			actionButtonHandler.handleLoad(getActivity(), this);
		}
	}
	
	public void setActionBarItemFactory(IBeanFactory<ActionBarItem> actionBarItemFactory) {
		this.actionBarItemFactory = actionBarItemFactory;
	}

	public void setImageDisabled(Drawable imageDisabled) {
		this.imageDisabled = imageDisabled;
	}

	public void setImageOn(Drawable imageOn) {
		this.imageOn = imageOn;
	}

	public void setImageOff(Drawable imageOff) {
		this.imageOff = imageOff;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	@Override
	public OnActionButtonEventListener<A> getOnActionButtonEventListener() {
		return button.getOnActionButtonEventListener();
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.actionbutton.ActionButton#getConfig()
	 */
	@Override
	public ActionButtonConfig getConfig() {
		return config;
	}

	public void setActionButtonHandlers(ActionButtonHandlers actionButtonHandlers) {
		this.actionButtonHandlers = actionButtonHandlers;
	}
	
	
}
