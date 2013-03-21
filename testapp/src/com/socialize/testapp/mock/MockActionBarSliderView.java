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
package com.socialize.testapp.mock;

import android.content.Context;
import android.view.animation.Animation;
import com.socialize.ui.slider.ActionBarSliderContent;
import com.socialize.ui.slider.ActionBarSliderHandle;
import com.socialize.ui.slider.ActionBarSliderView;

/**
 * @author Jason Polites
 *
 */
public class MockActionBarSliderView extends ActionBarSliderView {

	public MockActionBarSliderView(Context context, int[] parentLocation, int peekHeight) {
		super(context, parentLocation, peekHeight);
	}

	public MockActionBarSliderView(Context context) {
		super(context);
	}

	@Override
	public void startAnimationForState(Animation animation, DisplayState state) {
		super.startAnimationForState(animation, state);
	}

	@Override
	public boolean isMoving() {
		return super.isMoving();
	}

	@Override
	public void setMoving(boolean moving) {
		super.setMoving(moving);
	}

	@Override
	public ActionBarSliderHandle getHandle() {
		return super.getHandle();
	}

	@Override
	public ActionBarSliderContent getContent() {
		return super.getContent();
	}

	@Override
	public DisplayState getDisplayState() {
		return super.getDisplayState();
	}

	@Override
	public int getHandleHeight() {
		return super.getHandleHeight();
	}

	@Override
	public int getActionBarTop() {
		return super.getActionBarTop();
	}

	@Override
	public int getActionBarHeight() {
		return super.getActionBarHeight();
	}

	@Override
	public int getDeviceHeight() {
		return super.getDeviceHeight();
	}

	
}
