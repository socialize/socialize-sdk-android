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
package com.socialize;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.comment.OnCommentViewActionListener;

/**
 * @author Jason Polites
 *
 */
public interface SocializeUI {

	public View showActionBar(Activity parent, View original, Entity entity);
	
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options);
	
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener);
	
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener);

	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener);

	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options);

	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener);

	public View showActionBar(Activity parent, int resId, Entity entity);

	public void showUserProfileViewForResult(Activity context, Long userId, int requestCode);

	public void showUserProfileView(Activity context, Long userId);

	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener);

	public void showCommentView(Activity context, Entity entity);

	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode);

	public Drawable getDrawable(String name);
	
	public Drawable getDrawable(String name, boolean eternal);
}
