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
package com.socialize.ui.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;


/**
 * Takes a collection of views and fades them in/out in a rotating sequence.
 * @author Jason Polites
 *
 */
public class RotatingFadeViewAnimator {

	private int size;
	private View[] views;
	private int currentView = 0;
	private long fadeInTime = 1000;
	private long fadeOutTime = 1000;
	private long stickTime = 2000;
	
	private AlphaAnimation fadeIn;
	private AlphaAnimation fadeOut;
	
	public RotatingFadeViewAnimator(int size) {
		super();
		this.views = new View[size];
		this.size = size;
	}

	public void addView(int order, View view) {
		views[order] = view;
		if(order > 0) {
			view.setVisibility(View.GONE);
		}
	}
	
	public void setFadeInTime(long fadeInTime) {
		this.fadeInTime = fadeInTime;
	}

	public void setFadeOutTime(long fadeOutTime) {
		this.fadeOutTime = fadeOutTime;
	}

	public void setStickTime(long stickTime) {
		this.stickTime = stickTime;
	}
	
	public void startAt(int index) {
		currentView = index;
		start();
	}
	
	public void start() {
		if(fadeIn == null || fadeOut == null) {
			initAnimations();
		}
		
		View view = getCurrentView();
		view.startAnimation(fadeIn);
	}
	
	public void stop() {
		
		if(fadeIn != null) fadeIn.reset();
		if(fadeOut != null) fadeOut.reset();
		
		for (View view : views) {
			view.clearAnimation();
			view.setVisibility(View.GONE);
		}
		
		currentView = 0;
	}
	
	public void reset() {
		restartAt(0);
	}
	
	public void skipToNext() {
		restartAt((currentView + 1) % size);
	}
	
	protected void restartAt(int index) {
		stop();
		startAt(index);
	}
	
	protected View getCurrentView() {
		return views[currentView];
	}
	
	protected View getNextView() {
		currentView = (currentView + 1) % size;
		return getCurrentView();
	}
	
	protected void initAnimations() {
		fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setDuration(fadeInTime);
		fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setStartOffset(stickTime);
		fadeOut.setDuration(fadeOutTime);
		
		fadeIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				View view = getCurrentView();
				view.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				View view = getCurrentView();
				view.startAnimation(fadeOut);
			}
		});
		
		fadeOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				View current = getCurrentView();
				View next = getNextView();
				
				current.setVisibility(View.GONE);
				next.setVisibility(View.VISIBLE);
				
				next.startAnimation(fadeIn);
			}
		});
	}
}
