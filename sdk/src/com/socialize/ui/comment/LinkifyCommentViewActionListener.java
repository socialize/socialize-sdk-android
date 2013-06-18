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
package com.socialize.ui.comment;

import android.text.util.Linkify;
import android.view.MenuItem;
import com.socialize.entity.Comment;
import com.socialize.error.SocializeException;

import java.util.List;


/**
 * @author Jason Polites
 *
 */
public class LinkifyCommentViewActionListener implements OnCommentViewActionListener {
	
	int mask = Linkify.ALL;

	public LinkifyCommentViewActionListener() {
		super();
	}
	
	public LinkifyCommentViewActionListener(int mask) {
		super();
		this.mask = mask;
	}
	

	/* (non-Javadoc)
	 * @see com.socialize.listener.SocializeListener#onError(com.socialize.error.SocializeException)
	 */
	@Override
	public void onError(SocializeException error) {
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onCreate(com.socialize.ui.comment.CommentListView)
	 */
	@Override
	public void onCreate(CommentListView view) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onRender(com.socialize.ui.comment.CommentListView)
	 */
	@Override
	public void onRender(CommentListView view) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onCommentList(com.socialize.ui.comment.CommentListView, java.util.List, int, int)
	 */
	@Override
	public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onBeforeSetComment(com.socialize.entity.Comment, com.socialize.ui.comment.CommentListItem)
	 */
	@Override
	public void onBeforeSetComment(Comment comment, CommentListItem item) {}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onAfterSetComment(com.socialize.entity.Comment, com.socialize.ui.comment.CommentListItem)
	 */
	@Override
	public void onAfterSetComment(Comment comment, CommentListItem item) {
		Linkify.addLinks(item.getCommentText(), mask);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onReload(com.socialize.ui.comment.CommentListView)
	 */
	@Override
	public void onReload(CommentListView view) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.comment.OnCommentViewActionListener#onPostComment(com.socialize.entity.Comment)
	 */
	@Override
	public void onPostComment(Comment comment) {}

	@Override
	public boolean onRefreshMenuItemClick(MenuItem item) {
		return false;
	}

	@Override
	public boolean onSettingsMenuItemClick(MenuItem item) {
		return false;
	}

	@Override
	public boolean onCommentIconClicked(CommentListItem item) {
		return false;
	}

	@Override
	public boolean onCommentItemClicked(CommentListItem item) {
		return false;
	}
}
