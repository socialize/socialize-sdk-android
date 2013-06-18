/**
 * 
 */
package com.socialize.ui.comment;

import android.view.MenuItem;
import com.socialize.entity.Comment;
import com.socialize.listener.SocializeListener;

import java.util.List;

/**
 * Provides a callback for comment view events.
 * @author jasonpolites
 */
public interface OnCommentViewActionListener extends SocializeListener {

	public void onCreate(CommentListView view);
	
	public void onRender(CommentListView view);
	
	public void onCommentList(CommentListView view, List<Comment> comments, int start, int end);
	
	public void onBeforeSetComment(Comment comment, CommentListItem item);
	
	public void onAfterSetComment(Comment comment, CommentListItem item);
	
	public void onReload(CommentListView view);
	
	public void onPostComment(Comment comment);

	public boolean onSettingsMenuItemClick(MenuItem item);

	public boolean onRefreshMenuItemClick(MenuItem item);

	public boolean onCommentItemClicked(CommentListItem item);

	public boolean onCommentIconClicked(CommentListItem item);
}
