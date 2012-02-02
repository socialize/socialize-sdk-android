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
import android.util.AttributeSet;
import android.view.View;

import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.ui.view.AuthenticatedView;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeActionButton<A extends SocializeAction> extends AuthenticatedView {
	
	private ActionButtonConfig config;
	private ActionButtonLayoutView<A> view;
	private OnActionButtonEventListener<A> onActionButtonEventListener;

	public SocializeActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Must be done in constructor
		config = newActionButtonConfig();
		config.build(context, attrs);
	}

	public SocializeActionButton(Context context, ActionButtonConfig config) {
		super(context);
		this.config = config;
	}
	
	@Override
	public View getView() {

		view = container.getBean("actionButtonView", config, this); 
		
		if(!StringUtils.isEmpty(config.getEntityKey())) {
			Entity entity = Entity.newInstance(config.getEntityKey(), config.getEntityName());
			view.setEntity(entity);
		}
		
		return view;
	}
	

	@Override
	public void setOnClickListener(OnClickListener l) {
		throw new UnsupportedOperationException();
	}

	@Override
	public View getLoadingView() {
		return null;
	}
	
	protected ActionButtonConfig newActionButtonConfig() {
		return new ActionButtonConfig();
	}
	
	public Entity getEntity() {
		return (view == null) ? null : view.getEntity();
	}

	public void setEntity(Entity entity) {
		if(view != null) {
			view.setEntity(entity);
		}
	}
	
	public void refresh() {
		if(view != null) {
			view.refresh();
		}
	}
	
	protected void setConfig(ActionButtonConfig config) {
		this.config = config;
	}

	public ActionButtonConfig getConfig() {
		return config;
	}

	public ActionButtonState getState() {
		return (view == null) ? null : view.getState();
	}

	public OnActionButtonEventListener<A> getOnActionButtonEventListener() {
		return onActionButtonEventListener;
	}

	public void setOnActionButtonEventListener(OnActionButtonEventListener<A> onActionButtonEventListener) {
		this.onActionButtonEventListener = onActionButtonEventListener;
	}
}
