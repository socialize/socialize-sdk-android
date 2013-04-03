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
package com.socialize.ui.slider;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 *
 */
public class DefaultActionBarSliderFactory implements ActionBarSliderFactory<ActionBarSliderView> {

	private IBeanFactory<ActionBarSliderView> actionBarSliderViewFactory;
	private SocializeLogger logger;
	
	@Override
	public ActionBarSliderView wrap(View parent, ZOrder order, int peekHeight) {
		return wrap(parent, order, peekHeight, 0);
	}

	@Override
	public ActionBarSliderView wrap(View parent, ZOrder order, int peekHeight, int top, int left) {
		return wrap(parent, order, peekHeight, left, top, 0);
	}
	
	@Override
	public ActionBarSliderView wrap(View parent, ZOrder order, int peekHeight, int zOffset) {
		return wrap(parent, order, peekHeight, parent.getLeft(), parent.getTop(), zOffset);
	}

	@Override
	public ActionBarSliderView wrap(View view, ZOrder order, int peekHeight, int left, int top, int zOffset) {
		
		ActionBarSliderView actionBarSlider = null;
		ViewParent parent = view.getParent();
		RelativeLayout frame = null;
		
		final String errorMsg = "Unable to wrap view with a slider.  The view being wrapped is not contained in a RelativeLayout and no ancestor layout could be shimmed";
		
		int[] location = new int[]{left, top};
		
		actionBarSlider = actionBarSliderViewFactory.getBean(location, peekHeight);

		RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		actionBarSlider.setLayoutParams(barParams);
		actionBarSlider.setVisibility(View.GONE);
		
		if(!(parent instanceof RelativeLayout)) {
			ViewParent ancestor = parent.getParent();
			
			if(ancestor != null) {
				if(ancestor instanceof ViewGroup) {
					ViewGroup ancestorGroup = (ViewGroup) ancestor;
					
					if(parent instanceof View) {
						View parentView = (View) parent;
						int index = 0;
					
						if(ancestorGroup.getChildCount() > 1) {
							for (int i = 0; i < ancestorGroup.getChildCount(); i++) {
								if(ancestorGroup.getChildAt(i) == parentView) {
									index = i;
									break;
								}
							}
						}
						
						ancestorGroup.removeView(parentView);
						
						frame = new RelativeLayout(view.getContext());
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
						frame.setLayoutParams(params);
						
						if(order.equals(ZOrder.BEHIND)) {
							frame.addView(actionBarSlider);
							frame.addView(parentView);
						}
						else {
							frame.addView(parentView);
							frame.addView(actionBarSlider);
						}						
		
						ancestorGroup.addView(frame, index);
					}
				}
				else {
					if(logger != null) {
						logger.warn(errorMsg);
					}
				}
			}
			else {
				// No ancestor
				if(logger != null) {
					logger.warn(errorMsg);
				}
			}
		}
		else {
			frame = (RelativeLayout) parent;
			
			int childCount = frame.getChildCount();
			
			for (int i = 0; i < childCount; i++) {
				View child = frame.getChildAt(i);
				if(child == view) {
					if(order.equals(ZOrder.BEHIND)) {
						// Position in front of original, but behind view
						frame.addView(actionBarSlider, i + zOffset);
					}
					else {
						// Position in front of original and view
						frame.addView(actionBarSlider, i + 1 + zOffset);
					}
					break;
				}
			}			
		}
		
		return actionBarSlider;
	}

	public void setActionBarSliderViewFactory(IBeanFactory<ActionBarSliderView> actionBarSliderViewFactory) {
		this.actionBarSliderViewFactory = actionBarSliderViewFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	
}
