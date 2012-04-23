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
import com.socialize.ui.actionbutton.SocializeLikeButtonNew;
import com.socialize.ui.comment.OnCommentViewActionListener;

/**
 * @author Jason Polites
 *
 */
public interface SocializeUI {

	/**
	 * Appends the Socialize Action Bar to the view provided.  The action bar will appear at the bottom of the view.
	 * @param parent The current activity.
	 * @param original The view to be wrapped.
	 * @param entity The entity that will be bound to the action bar.
	 * @return The original view with the action bar appended to the bottom.
	 */
	public View showActionBar(Activity parent, View original, Entity entity);
	
	/**
	 * Appends the Socialize Action Bar to the view provided.  The action bar will appear at the bottom of the view.
	 * @param parent The current activity.
	 * @param original The view to be wrapped.
	 * @param entity The entity that will be bound to the action bar.
	 * @param options Display options for the action bar.
	 * @return The original view with the action bar appended to the bottom.
	 */
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options);
	
	/**
	 * 
	 * @param parent
	 * @param original
	 * @param entity
	 * @param listener
	 * @return
	 */
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener);
	
	/**
	 * 
	 * @param parent
	 * @param original
	 * @param entity
	 * @param options
	 * @param listener
	 * @return
	 */
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener);

	/**
	 * 
	 * @param parent
	 * @param resId
	 * @param entity
	 * @param options
	 * @param listener
	 * @return
	 */
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener);

	/**
	 * 
	 * @param parent
	 * @param resId
	 * @param entity
	 * @param options
	 * @return
	 */
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options);

	/**
	 * 
	 * @param parent
	 * @param resId
	 * @param entity
	 * @param listener
	 * @return
	 */
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener);

	/**
	 * 
	 * @param parent
	 * @param resId
	 * @param entity
	 * @return
	 */
	public View showActionBar(Activity parent, int resId, Entity entity);

	/**
	 * 
	 * @param context
	 * @param userId
	 * @param requestCode
	 */
	public void showUserProfileViewForResult(Activity context, Long userId, int requestCode);

	/**
	 * Shows the profile for a user.  If the user is the current logged in user this will allow the user to edit.
	 * @param context
	 * @param userId
	 */
	public void showUserProfileView(Activity context, Long userId);
	
	/**
	 * Shows the comments for an entity.
	 * @param context
	 * @param entity
	 * @param listener
	 */
	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener);

	/**
	 * Shows the comments for an entity.
	 * @param context
	 * @param entity
	 */
	public void showCommentView(Activity context, Entity entity);

	/**
	 * Shows the action detail view for a specific action.
	 * @param context
	 * @param user
	 * @param action
	 * @param requestCode
	 */
	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode);
	
	/**
	 * Creates a default Socialize Like Button.
	 * @param context
	 * @param entity
	 * @return
	 */
	public SocializeLikeButtonNew createLikeButton(Activity context, Entity entity);

	public Drawable getDrawable(String name);
	
	public Drawable getDrawable(String name, boolean eternal);
}
