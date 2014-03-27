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
import android.content.Context;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.CommentUtilsProxy;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentDeleteListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.comment.OnCommentViewActionListener;

import java.lang.reflect.Proxy;

/**
 * @author Jason Polites
 *
 */
public class CommentUtils {
	
	static CommentUtilsProxy proxy;
	
	static {
		proxy = (CommentUtilsProxy) Proxy.newProxyInstance(
				CommentUtilsProxy.class.getClassLoader(),
				new Class[]{CommentUtilsProxy.class},
				new SocializeActionProxy("commentUtils"));	// Bean name
	}
	
	/**
	 * Returns the default sharing options for the user.
	 * @param context The current context.
	 * @return The default sharing options for the user.
	 */
	public static CommentOptions getUserCommentOptions(Context context) {
		return proxy.getUserCommentOptions(context);
	}
	
	/**
	 * Adds a comment to the given entity.  This method will also prompt the user to share their comment.
	 * @param context The current context.
	 * @param entity The entity on which the comment will be associated.
	 * @param text The text of the comment.
	 * @param listener A listener to handle the result.
	 */
	public static void addComment (Activity context, Entity entity, String text, CommentAddListener listener) {
		proxy.addComment(context, entity, text, listener);
	}
	
	/**
	 * Deletes a comment.  Only the person that created the comment can delete it.
	 * @param context The current context.
	 * @param id The ID of the comment to be deleted.
	 * @param listener A listener to handle the result.
	 */
	public static void deleteComment (Activity context, long id, CommentDeleteListener listener) {
		proxy.deleteComment(context, id, listener);
	}	
	
	/**
	 * Adds a comment to the given entity.  This method will NOT prompt the user to share their comment as the desired networks are passed as a parameter.
	 * @param context The current context.
	 * @param entity The entity on which the comment will be associated.
	 * @param text The text of the comment.
	 * @param commentOptions Optional parameters for the comment.
	 * @param listener A listener to handle the result.
	 * @param networks 0 or more networks on which to share the comment.  It is assumed that the user is already linked to the given networks.  If not an error will be reported in the listener for each propagation failure. 
	 */
	public static void addComment (Activity context, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork...networks) {
		proxy.addComment(context, entity, text, commentOptions, listener, networks);
	}
	
	/**
	 * Retrieves a single comment based on ID.
	 * @param context The current context.
	 * @param id
	 * @param listener A listener to handle the result.
	 */
	public static void getComment (Activity context, CommentGetListener listener, long id) {
		proxy.getComment(context, id, listener);
	}

	/**
	 * Retrieves comments based on a set of IDs.
	 * @param context The current context.
	 * @param listener A listener to handle the result.
	 * @param ids An array of comment IDs
	 */
	public static void getComments (Activity context, CommentListListener listener, long...ids) {
		proxy.getComments(context, listener, ids);
	}
	
	/**
	 * Retrieves all comments made by the given user.
	 * @param context The current context.
	 * @param user The user.
	 * @param start The start index for pagination (from 0).
	 * @param end The end index for pagination.
	 * @param listener A listener to handle the result.
	 */
	public static void getCommentsByUser (Activity context, User user, int start, int end, CommentListListener listener) {
		proxy.getCommentsByUser(context, user, start, end, listener);
	}
	
	/**
	 * Retrieves all comments associated with the given entity.
	 * @param context The current context.
	 * @param entityKey The entity on which the comment were associated.
	 * @param start The start index for pagination (from 0).
	 * @param end The end index for pagination.
	 * @param listener A listener to handle the result.
	 */
	public static void getCommentsByEntity (Activity context, String entityKey, int start, int end, CommentListListener listener) {
		proxy.getCommentsByEntity(context, entityKey, start, end, listener);
	}
	
	/**
	 * Retrieves all comments across all entities.
	 * @param context The current context.
	 * @param start The start index for pagination (from 0).
	 * @param end The end index for pagination.
	 * @param listener A listener to handle the result.
	 */
	public static void getCommentsByApplication (Activity context, int start, int end, CommentListListener listener) {
		proxy.getCommentsByApplication(context, start, end, listener);
	}
	
	/**
	 * Shows the comments for an entity.
	 * @param listener A listener to handle the result.
	 */
	public static void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener) {
		proxy.showCommentView(context, entity, listener);
	}

	/**
	 * Shows the comments for an entity.
	 */
	public static void showCommentView(Activity context, Entity entity) {
		proxy.showCommentView(context, entity);
	}
}
