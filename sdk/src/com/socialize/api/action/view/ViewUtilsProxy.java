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
package com.socialize.api.action.view;

import android.app.Activity;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.listener.view.ViewGetListener;
import com.socialize.listener.view.ViewListListener;

/**
 * @author Jason Polites
 */
public interface ViewUtilsProxy {

	/**
	 * Records a view against the given entity for the current user.
	 * @param context The current context.
	 * @param e The entity to be viewd.
	 * @param listener A listener to handle the result.
	 */
	public void view (Activity context, Entity e, ViewAddListener listener);
	
	/**
	 * Retrieves a view for an entity.
	 * @param context The current context.
	 * @param e The entity that was viewd.
	 * @param listener A listener to handle the result.
	 * @deprecated No longer supported.
	 */
	@Deprecated
	public void getView (Activity context, Entity e, ViewGetListener listener);
	
	public void getView (Activity context, long id, ViewGetListener listener);
	
	/**
	 * Lists all views for the given user.
	 * @param context The current context.
	 * @param user The user for whom views will be queried.
	 * @param start The first index (for pagination), starting at 0
	 * @param end The last index (for pagination)
	 * @param listener A listener to handle the result.
	 * @deprecated No longer supported.
	 */
	@Deprecated
	public void getViewsByUser (Activity context, User user, int start, int end, ViewListListener listener);
	
	/**
	 * Lists all views for the given entity.
	 * @param context The current context.
	 * @param entity The entity for which views will be queried.
	 * @param start The first index (for pagination), starting at 0
	 * @param end The last index (for pagination)
	 * @param listener A listener to handle the result.
	 * @deprecated No longer supported.
	 */
	@Deprecated
	public void getViewsByEntity (Activity context, Entity entity, int start, int end, ViewListListener listener);
}
