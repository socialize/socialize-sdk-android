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
package com.socialize;

import android.app.Activity;
import android.view.View;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarUtilsImpl;


/**
 * @author Jason Polites
 */
public class ActionBarUtils {
	
	static ActionBarUtilsImpl proxy = new ActionBarUtilsImpl();
	
	/**
	 * Attaches the Socialize action bar to an existing view.  The ActionBar will be pinned to the bottom of the view.
	 * @param parent The activity containing the view.
	 * @param original The original View to which the ActionBar will be pinned.
	 * @param entity The entity associated with the ActionBar.
	 * @return The final view including the ActionBar which can then be attached to your layout.
	 */
	public static View showActionBar(Activity parent, View original, Entity entity) {
		return proxy.showActionBar(parent, original, entity);
	}
	
	/**
	 * Attaches the Socialize action bar to an existing view.  The ActionBar will be pinned to the bottom of the view.
	 * @param parent The activity containing the view.
	 * @param resId The resource ID of the original View (layout) to which the ActionBar will be pinned.
	 * @param entity The entity associated with the ActionBar.
	 * @return The final view including the ActionBar which can then be attached to your layout.
	 */
	public static View showActionBar(Activity parent, int resId, Entity entity) {
		return proxy.showActionBar(parent, resId, entity);
	}
	
	
	/**
	 * Attaches the Socialize action bar to an existing view.  The ActionBar will be pinned to the bottom of the view.
	 * @param parent The activity containing the view.
	 * @param resId The resource ID of the original View (layout) to which the ActionBar will be pinned.
	 * @param entity The entity associated with the ActionBar.
	 * @param options Display option for the action bar (may be null)
	 * @param listener A listener to handle ActionBar events (may be null)
	 * @return The final view including the ActionBar which can then be attached to your layout.
	 */
	public static View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return proxy.showActionBar(parent, resId, entity, options, listener);
	}
	
	/**
	 * Attaches the Socialize action bar to an existing view.  The ActionBar will be pinned to the bottom of the view.
	 * @param parent The activity containing the view.
	 * @param original The original View to which the ActionBar will be pinned.
	 * @param entity The entity associated with the ActionBar.
	 * @param options Display option for the action bar (may be null)
	 * @return The final view including the ActionBar which can then be attached to your layout.
	 */
	public static View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options) {
		return proxy.showActionBar(parent, original, entity, options);
	}

	/**
	 * Attaches the Socialize action bar to an existing view.  The ActionBar will be pinned to the bottom of the view.
	 * @param parent The activity containing the view.
	 * @param resId The resource ID of the original View (layout) to which the ActionBar will be pinned.
	 * @param entity The entity associated with the ActionBar.
	 * @param options Display option for the action bar (may be null)
	 * @return The final view including the ActionBar which can then be attached to your layout.
	 */
	public static View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options) {
		return proxy.showActionBar(parent, resId, entity, options);
	}	
	
	/**
	 * Attaches the Socialize action bar to an existing view.  The ActionBar will be pinned to the bottom of the view.
	 * @param parent The activity containing the view.
	 * @param original The original View to which the ActionBar will be pinned.
	 * @param entity The entity associated with the ActionBar.
	 * @param options Display option for the action bar (may be null)
	 * @param listener A listener to handle ActionBar events (may be null)
	 * @return The final view including the ActionBar which can then be attached to your layout.
	 */
	public static View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return proxy.showActionBar(parent, original, entity, options, listener);
	}	
}
