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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import com.socialize.ui.animation.RotatingFadeViewAnimator;
import com.socialize.ui.util.CompatUtils;
import com.socialize.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The ticker shows a rolling display of stats for an entity.
 * @author Jason Polites
 *
 */
public class ActionBarTicker extends LinearLayout {
	
	public static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#222222");

	private List<View> views;
	private RotatingFadeViewAnimator rotator;
	private DisplayUtils displayUtils;
	private boolean initialized = false;
	private Integer backgroundColor;
	
	public ActionBarTicker(Context context) {
		super(context);
	}
	
	public ActionBarTicker(Context context, Integer backgroundColor) {
		super(context);
		this.backgroundColor = backgroundColor;
	}
	
	public void init(int width, float weight) {
		if(!initialized) {
			
			if(width > 0) {
				width = displayUtils.getDIP(width);
			}
			
			LayoutParams masterParams = new LayoutParams(width,displayUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
			masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			masterParams.weight = weight;
			
			setLayoutParams(masterParams);

			ColorDrawable viewBg = new ColorDrawable((backgroundColor == null) ? DEFAULT_BACKGROUND_COLOR : backgroundColor);

			CompatUtils.setBackgroundDrawable(this, viewBg);

			if(views != null) {
				rotator = new RotatingFadeViewAnimator(views.size());
				int index = 0;
				for (View view : views) {
					rotator.addView(index, view);
					addView(view);
					index++;
				}
				
				rotator.setFadeInTime(1500);
				rotator.setFadeOutTime(1000);
				rotator.setStickTime(5000);
			}
			
			initialized = true;
		}
	}
	
	public void startTicker() {
		if(rotator != null) {
			rotator.start();
		}
	}
	
	public void skipToNext() {
		if(rotator != null) {
			rotator.skipToNext();
		}
		invalidate();
	}
	
	public void stopTicker() {
		if(rotator != null) {
			rotator.stop();
		}
	}
	
	public void resetTicker() {
		if(rotator != null) {
			rotator.reset();
		}
	}

	public void addTickerView(View view) {
		if(views == null) {
			views = new ArrayList<View>(10);
		}
		views.add(view);
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}
}
