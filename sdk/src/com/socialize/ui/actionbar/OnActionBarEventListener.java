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

import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;

/**
 * @author Jason Polites
 *
 */
public interface OnActionBarEventListener {
	
	public enum ActionBarEvent {VIEW, LIKE, COMMENT, SHARE}; 

	public void onGetLike(ActionBarView actionBar, Like like);
	
	public void onPostLike(ActionBarView actionBar, Like like);
	
	public void onPostUnlike(ActionBarView actionBar);
	
	public void onPostShare(ActionBarView actionBar, Share share);
	
	public void onGetEntity(ActionBarView actionBar, Entity entity);
	
	/**
	 * Called when a click event occurs on the ActionBar.
	 * @param actionBar
	 * @param evt
	 * @return Return true if the event has been consumed and should NOT be propagated to the ActionBar
	 */
	public boolean onClick(ActionBarView actionBar, ActionBarEvent evt);
	
	public void onUpdate(ActionBarView actionBar);
	
	public void onLoad(ActionBarView actionBar);
	
	public void onLoadFail(Exception error);
}
