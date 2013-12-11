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
package com.socialize.ui.actionbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import com.socialize.entity.Entity;


/**
 * @author Jason Polites
 */
public class ActionBarUtilsImpl {
	
	public View showActionBar(Activity parent, int resId, Entity entity) {
		return showActionBar(parent, resId, entity, null, null);
	}
	
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener) {
		return showActionBar(parent, resId, entity, null, listener);
	}

	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options) {
		return showActionBar(parent, resId, entity, options, null);
	}
	
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return showActionBar(parent, inflateView(parent, resId) , entity, options, listener);
	}

	public View showActionBar(Activity parent, View original, Entity entity) {
		return showActionBar(parent, original, entity, null, null);
	}
	
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options) {
		return showActionBar(parent, original, entity, options, null);
	}
	
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener) {
		return showActionBar(parent, original, entity, null, listener);
	}	
	
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		RelativeLayout parentRelLyout = newRelativeLayout(parent);
		RelativeLayout.LayoutParams barLayoutParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		parentRelLyout.setLayoutParams(barLayoutParams);
		
		ActionBarView socializeActionBar = newActionBarView(parent);
		socializeActionBar.setActionBarListener(listener);
		socializeActionBar.assignId(original);
		socializeActionBar.setEntity(entity);
		socializeActionBar.setActionBarOptions(options);
		
		LayoutParams socializeActionBarParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		socializeActionBarParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		socializeActionBar.setLayoutParams(socializeActionBarParams);
		
		boolean addScrollView = true;
		
		if(options != null) {
			addScrollView = options.isAddScrollView();
		}
		
		View contentView;
		
		if(addScrollView && !(original instanceof ScrollView) && !(original instanceof ListView) ) {
			RelativeLayout.LayoutParams scrollViewParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			
			scrollViewParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
			
			ScrollView scrollView = newScrollView(parent);
			scrollView.setFillViewport(true);
			scrollView.setBackgroundColor(Color.parseColor("#00000000"));
			scrollView.setScrollContainer(false);
			scrollView.setLayoutParams(scrollViewParams);
			scrollView.addView(original);
			
			contentView = scrollView;
		}
		else {
			ViewGroup.LayoutParams originalParams = original.getLayoutParams();
			LayoutParams updatedOriginalParams = null;
			
			if(originalParams != null) {
				updatedOriginalParams = newLayoutParams(originalParams);
			}
			else {
				updatedOriginalParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			}
			
			updatedOriginalParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
			
			original.setLayoutParams(updatedOriginalParams);
			
			contentView = original;
		}		

		parentRelLyout.addView(contentView);
		parentRelLyout.addView(socializeActionBar);
		
		return parentRelLyout;
	}
	
	protected LayoutParams newLayoutParams(int width, int height) {
		return new LayoutParams(width, height);
	}
	
	protected RelativeLayout.LayoutParams newLayoutParams(android.view.ViewGroup.LayoutParams source) {
		return new RelativeLayout.LayoutParams(source);
	}
	

	protected RelativeLayout newRelativeLayout(Activity parent) {
		return new RelativeLayout(parent);
	}

	protected ScrollView newScrollView(Activity parent) {
		return new ScrollView(parent);
	}

	protected ActionBarView newActionBarView(Activity parent) {
		return new ActionBarView(parent);
	}

	protected View inflateView(Activity parent, int resId) {
		LayoutInflater layoutInflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		return layoutInflater.inflate(resId, null);
	}
}
