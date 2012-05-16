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
package com.socialize.api.action.like;

import android.app.Activity;
import com.socialize.entity.User;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.networks.ShareOptions;


/**
 * @author Jason Polites
 *
 */
public interface LikeUtilsProxy {

	/**
	 * Records a like against the given entity for the current user.
	 * @param context The current context.
	 * @param entityKey The entity to be liked.
	 * @param listener A listener to handle the result.
	 */
	public void like (Activity context, String entityKey, LikeAddListener listener);
	
	/**
	 * Removes a like previously created for the current user.
	 * @param context The current context.
	 * @param entityKey The entity that was liked.
	 * @param listener A listener to handle the result.
	 */
	public void unlike (Activity context, String entityKey, LikeDeleteListener listener);
	
	/**
	 * Retrieves a like for an entity.
	 * @param context The current context.
	 * @param entityKey The entity that was liked.
	 * @param listener A listener to handle the result.
	 */
	public void getLike (Activity context, String entityKey, LikeGetListener listener);
	
	/**
	 * Lists all likes for the given user.
	 * @param context The current context.
	 * @param user The user for whom likes will be queried.
	 * @param start The first index (for pagination), starting at 0
	 * @param end The last index (for pagination)
	 * @param listener A listener to handle the result.
	 */
	public void getLikesByUser (Activity context, User user, int start, int end, LikeListListener listener);
	
	
	/**
	 * Lists all likes for the given entity.
	 * @param context The current context.
	 * @param entityKey The entity for which likes will be queried.
	 * @param start The first index (for pagination), starting at 0
	 * @param end The last index (for pagination)
	 * @param listener A listener to handle the result.
	 */
	public void getLikesByEntity (Activity context, String entityKey, int start, int end, LikeListListener listener);

	/**
	 * @param context
	 * @param entityKey
	 * @param shareOptions
	 * @param listener
	 */
	public void like(Activity context, String entityKey, ShareOptions shareOptions, LikeAddListener listener);
}
