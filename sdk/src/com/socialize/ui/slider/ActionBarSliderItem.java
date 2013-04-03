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
import com.socialize.ui.slider.ActionBarSliderView.DisplayState;

/**
 * @author Jason Polites
 *
 */
public interface ActionBarSliderItem {
	
	public String getId();
	
	/**
	 * Returns the view to be displayed in the slider.
	 * @return
	 */
	public View getView();
	
	/**
	 * Returns height of the slider content in pixels, or -1 for whole screen.
	 * @return
	 */
	public int getSliderContentHeight();
	
	/**
	 * If true the slider can be maximized.
	 * @return
	 */
	public DisplayState getStartPosition();
	
	/**
	 * If false the slider closes when restored either from maximized position, or restored position.
	 * @return
	 */
	public boolean isPeekOnClose();
	
	public String getIconImage();
	
	public String getTitle();
	
	/**
	 * Informs the slider that an update may be required
	 * @param slider
	 */
	public void onUpdate(ActionBarSliderView slider);
	
	/**
	 * Clears the content of the item.
	 */
	public void onClear(ActionBarSliderView slider);
	
	public void onClose(ActionBarSliderView slider);
	
	public void onOpen(ActionBarSliderView slider);
	
	public void onCreate(ActionBarSliderView slider);
}
