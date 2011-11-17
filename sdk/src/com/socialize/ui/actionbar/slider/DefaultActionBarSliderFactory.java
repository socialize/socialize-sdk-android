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
package com.socialize.ui.actionbar.slider;

import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.socialize.android.ioc.IBeanFactory;

/**
 * @author Jason Polites
 *
 */
public class DefaultActionBarSliderFactory implements ActionBarSliderFactory<ActionBarSliderView> {

	private IBeanFactory<ActionBarSliderView> actionBarSliderViewFactory;
	
	@Override
	public ActionBarSliderView wrap(View view) {
		
		ActionBarSliderView actionBarSlider = null;
		ViewParent parent = view.getParent();
		
		if(parent instanceof RelativeLayout) {
			
			RelativeLayout frame = (RelativeLayout) parent;
			
			int[] location = new int[]{view.getLeft(), view.getTop()};
			
			actionBarSlider = actionBarSliderViewFactory.getBean(location);

			RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			barParams.addRule(RelativeLayout.ABOVE, view.getId());
			
			actionBarSlider.setLayoutParams(barParams);
			actionBarSlider.setVisibility(View.GONE);
			
			// Position in front of original, but behind actionbar
			int childCount = frame.getChildCount();
			
			for (int i = 0; i < childCount; i++) {
				View child = frame.getChildAt(i);
				
				if(child == view) {
					frame.addView(actionBarSlider, i);
					break;
				}
			}
		}	
		
		return actionBarSlider;
	}

	public void setActionBarSliderViewFactory(IBeanFactory<ActionBarSliderView> actionBarSliderViewFactory) {
		this.actionBarSliderViewFactory = actionBarSliderViewFactory;
	}
}
