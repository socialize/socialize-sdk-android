/*
 * Copyright (listener) 2011 Socialize Inc.
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
package com.socialize.api.action.comment;

import android.app.Activity;
import com.socialize.entity.User;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;


/**
 * @author Jason Polites
 *
 */
public interface CommentUtilsProxy {

	/**
	 * Adds a comment to the given entity.
	 * @param context The current context.
	 * @param entityKey The entity on which the comment will be associated.
	 * @param text The text of the comment.
	 * @param listener A listener to handle the result.
	 */
	public void addComment (Activity context, String entityKey, String text, CommentAddListener listener);
	
	/**
	 * Retrieves a single comment based on ID.
	 * @param context The current context.
	 * @param id
	 * @param listener A listener to handle the result.
	 */
	public void getComment (Activity context, long id, CommentGetListener listener);
	
	/**
	 * Retrieves comments based on a set of IDs.
	 * @param context The current context.
	 * @param start The start index for pagination (from 0).
	 * @param end The end index for pagination.
	 * @param listener A listener to handle the result.
	 * @param id An array of comment IDs
	 */
	public void getComments (Activity context, CommentListListener listener, long...id);
	
	/**
	 * Retrieves all comments made by the given user.
	 * @param context The current context.
	 * @param user The user.
	 * @param start The start index for pagination (from 0).
	 * @param end The end index for pagination.
	 * @param listener A listener to handle the result.
	 */
	public void getCommentsByUser (Activity context, User user, int start, int end, CommentListListener listener);
	
	/**
	 * Retrieves all comments associated with the given entity.
	 * @param context The current context.
	 * @param entityKey The entity on which the comment were associated.
	 * @param start The start index for pagination (from 0).
	 * @param end The end index for pagination.
	 * @param listener A listener to handle the result.
	 */
	public void getCommentsByEntity (Activity context, String entityKey, int start, int end, CommentListListener listener);
}
