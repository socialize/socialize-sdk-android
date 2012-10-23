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

import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;


/**
 * @author Jason Polites
 */
public abstract class OnActionBarShareEventListener extends SocialNetworkDialogListener implements OnActionBarEventListener {

	@Override
	public void onGetLike(ActionBarView actionBar, Like like) {}

	@Override
	public void onPostLike(ActionBarView actionBar, Like like) {}

	@Override
	public void onPostUnlike(ActionBarView actionBar) {}

	@Override
	public void onPostShare(ActionBarView actionBar, Share share) {}

	@Override
	public void onGetEntity(ActionBarView actionBar, Entity entity) {}

	@Override
	public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
		return false;
	}

	@Override
	public void onUpdate(ActionBarView actionBar) {}

	@Override
	public void onLoad(ActionBarView actionBar) {}

}
