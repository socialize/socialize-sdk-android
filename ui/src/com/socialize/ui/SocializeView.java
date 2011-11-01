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
package com.socialize.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public abstract class SocializeView extends BaseView {

	protected IOCContainer container;
	
	public SocializeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SocializeView(Context context) {
		super(context);
	}
	
	@Override
	protected final void onViewLoad() {
		super.onViewLoad();
		View loading = getLoadingView();
		if(loading != null) {
			addView(loading);
		}
		doSocializeInit(getInitLoadListener());
	}
	
	@Override
	protected final void onViewUpdate() {
		super.onViewUpdate();
		doSocializeInit(getInitUpdateListener());
	}
	
	protected SocializeInitListener getInitLoadListener() {
		return new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				Log.e("Socialize", "Error initializing Socialize", error);
			}
			
			@Override
			public void onInit(Context context, IOCContainer c) {
				container = c;
				onViewLoad(container);
			}
		};
	}
	
	protected SocializeInitListener getInitUpdateListener() {
		return new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				Log.e("Socialize", "Error initializing Socialize", error);
			}
			
			@Override
			public void onInit(Context context, IOCContainer c) {
				container = c;
				onViewUpdate(container);
			}
		};
	}	
	
	// Subclasses override
	public void onViewLoad(IOCContainer container) {};
	
	// Subclasses override
	public void onViewUpdate(IOCContainer container) {};

	protected void doSocializeInit(SocializeInitListener listener) {
		if(!isInEditMode()) {
			onBeforeSocializeInit();
			initSocialize(listener);
		}
	}
	
	protected <E extends Object> E getBean(String name) {
		return container.getBean(name);
	}

	public void setContainer(IOCContainer container) {
		this.container = container;
	}

	protected void initSocialize(SocializeInitListener listener) {
		getSocializeUI().initSocializeAsync(this.getContext(), listener);
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}
	
	public abstract View getLoadingView();
	
	// Subclasses override
	protected void onBeforeSocializeInit() {}
}
