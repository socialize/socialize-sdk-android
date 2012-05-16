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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 *
 */
public class SliderAnimationSet {
	
	private DisplayUtils displayUtils;
	
	private TranslateAnimation maximizeFromClose;
	private TranslateAnimation maximizeFromPeek;
	private TranslateAnimation peekFromClose;
	private TranslateAnimation peekFromMaximize;
	private TranslateAnimation closeFromPeek;
	private TranslateAnimation closeFromMaximized;
	
	public TranslateAnimation getMaximizeFromClose() {
		return maximizeFromClose;
	}
	public void setMaximizeFromClose(TranslateAnimation maximizeFromClose) {
		this.maximizeFromClose = maximizeFromClose;
	}
	public TranslateAnimation getMaximizeFromPeek() {
		return maximizeFromPeek;
	}
	public void setMaximizeFromPeek(TranslateAnimation maximizeFromPeek) {
		this.maximizeFromPeek = maximizeFromPeek;
	}
	public TranslateAnimation getPeekFromClose() {
		return peekFromClose;
	}
	public void setPeekFromClose(TranslateAnimation peekFromClose) {
		this.peekFromClose = peekFromClose;
	}
	public TranslateAnimation getPeekFromMaximize() {
		return peekFromMaximize;
	}
	public void setPeekFromMaximize(TranslateAnimation peekFromMaximize) {
		this.peekFromMaximize = peekFromMaximize;
	}
	public TranslateAnimation getCloseFromPeek() {
		return closeFromPeek;
	}
	public void setCloseFromPeek(TranslateAnimation closeFromPeek) {
		this.closeFromPeek = closeFromPeek;
	}
	public TranslateAnimation getCloseFromMaximized() {
		return closeFromMaximized;
	}
	public void setCloseFromMaximized(TranslateAnimation closeFromMaximized) {
		this.closeFromMaximized = closeFromMaximized;
	}
	
	protected void notifyMove(int height, ActionBarSliderView slider) {
		int yOffset = 0;
		
		switch(slider.getDisplayState()) {
			case CLOSE:
				yOffset = slider.getActionBarTop();
				break;
			case PEEK:
				yOffset = slider.getActionBarTop() - slider.getHandleHeight();
				break;				
			case MAXIMIZE:
				yOffset = slider.getDeviceHeight() - height;
				break;
		}	
		
		slider.getHandle().notifyMove(yOffset);
		slider.getContent().notifyMove(yOffset);
	}
	
	public void init(ActionBarSliderItem item, final ActionBarSliderView slider) {
		int slideHeight = item.getSliderContentHeight();
		
		int h = slider.getDeviceHeight();
		
		if(slideHeight > 0) {
			h = displayUtils.getDIP(slideHeight) + (slider.getDeviceHeight() - slider.getActionBarTop()) + slider.getHandleHeight();
		}
		
		final int height = h;
		
		AnimationListener visibleAnimation = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				slider.setVisibility(View.VISIBLE);
				notifyMove(height, slider);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				slider.setMoving(false);
				slider.onOpen();
			}
		};
		
		AnimationListener hideAnimation = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				notifyMove(height, slider);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				slider.setVisibility(View.GONE);
				slider.setMoving(false);
				slider.clearAnimation();
				slider.onClose();
			}
		};
				
		
		float closed = slider.getActionBarTop();
		float maximized = slider.getDeviceHeight() - height;
		float peeked = slider.getActionBarTop() - slider.getHandleHeight();
		
		maximizeFromClose = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f, // 	x:from->to
			Animation.ABSOLUTE, closed, Animation.ABSOLUTE, maximized   // 	y:from->to	
		);

		maximizeFromPeek = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, peeked, Animation.ABSOLUTE, maximized
		);		

		peekFromClose = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, closed, Animation.ABSOLUTE, peeked
		);		
		
		peekFromMaximize = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, maximized, Animation.ABSOLUTE, peeked
		);
		
		closeFromPeek = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, peeked, Animation.ABSOLUTE, closed
		);
		
		closeFromMaximized = new TranslateAnimation(
			Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
			Animation.ABSOLUTE, maximized, Animation.ABSOLUTE, closed
		);		
		
		
		setDefaultAnimationConfig(maximizeFromClose, visibleAnimation, 500);
		setDefaultAnimationConfig(maximizeFromPeek, visibleAnimation, 500);
		
		setDefaultAnimationConfig(peekFromClose, visibleAnimation, 700);
		setDefaultAnimationConfig(peekFromMaximize, visibleAnimation, 500);

		setDefaultAnimationConfig(closeFromPeek, hideAnimation, 700);
		setDefaultAnimationConfig(closeFromMaximized, hideAnimation, 500);		
	}
	
	protected void setDefaultAnimationConfig(Animation anim, AnimationListener listener, long duration) {
		anim.setAnimationListener(listener);
		anim.setFillAfter(true);
		anim.setDuration(duration);
	}
	
	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}
}
