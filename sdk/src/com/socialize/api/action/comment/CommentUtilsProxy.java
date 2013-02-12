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
import android.content.Context;
import com.socialize.android.ioc.ContainerAware;
import com.socialize.annotations.Synchronous;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentDeleteListener;
import com.socialize.listener.comment.CommentGetListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.comment.OnCommentViewActionListener;


/**
 * @author Jason Polites
 *
 */
public interface CommentUtilsProxy extends ContainerAware {
	
	@Synchronous
	public CommentOptions getUserCommentOptions(Context context);
	
	public void deleteComment (Activity context, long id, CommentDeleteListener listener);

	public void addComment (Activity context, Entity entity, String text, CommentAddListener listener);
	
	public void addComment (Activity context, Entity entity, String text, CommentOptions commentOptions, CommentAddListener listener, SocialNetwork...networks);
	
	public void getComment (Activity context, long id, CommentGetListener listener);
	
	public void getComments (Activity context, CommentListListener listener, long...id);
	
	public void getCommentsByUser (Activity context, User user, int start, int end, CommentListListener listener);
	
	public void getCommentsByEntity (Activity context, String entityKey, int start, int end, CommentListListener listener);
	
	public void getCommentsByApplication (Activity context, int start, int end, CommentListListener listener);
	
	public void preloadCommentView (Activity context);
	
	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener);
	
	public void showCommentView(Activity context, Entity entity);
}
