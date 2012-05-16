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
package com.socialize.ui.actionbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.ui.view.AuthenticatedView;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
@Deprecated
public class SocializeActionButton<A extends SocializeAction> extends AuthenticatedView {
	
	private ActionButtonConfig config;
	private ActionButtonLayoutView<A> view;
	private OnActionButtonEventListener<A> onActionButtonEventListener;
	private Entity entity;

	public SocializeActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Must be done in constructor
		doConfigBuild(context, attrs);
	}

	public SocializeActionButton(Context context, ActionButtonConfig config) {
		super(context);
		this.config = config;
		onAfterBuild(config);
	}
	
	public SocializeActionButton(Context context) {
		super(context);
		this.config = getDefault();
		onAfterBuild(config);
	}

	@Override
	public View getView() {
		try {
			if(config != null) {
				view = container.getBean("actionButtonView", config, this); 
				String entityKey = config.getEntityKey();
				if(!StringUtils.isEmpty(entityKey)) {
					Entity entity = createEntity(entityKey, config.getEntityName());
					view.setEntity(entity);
				}
				else {
					view.setEntity(entity);
				}
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return view;
	}
	
	// So we can mock
	protected Entity createEntity(String key, String name) {
		return Entity.newInstance(key, name);
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
		this.entity = entity;
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
	
	protected void doConfigBuild(Context context, AttributeSet attrs) {
		config = newActionButtonConfig();
		config.build(context, attrs);
		onAfterBuild(config);
	}
	
	protected ActionButtonLayoutView<A> getLayoutView() {
		return view;
	}
	
	public ImageView getImageView() {
		return (view == null) ? null : view.getImageView();
	}

	public TextView getTextView() {
		return (view == null) ? null : view.getTextView();
	}
	
	/**
	 * Called after the initial build of the config.
	 * @param config
	 */
	protected void onAfterBuild(ActionButtonConfig config) {}
	
	// So we can mock
	protected ActionButtonConfig getDefault() {
		return ActionButtonConfig.getDefault();
	}
}
