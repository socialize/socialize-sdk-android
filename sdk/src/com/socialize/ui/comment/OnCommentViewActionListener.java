/**
 * 
 */
package com.socialize.ui.comment;

import java.util.List;
import com.socialize.entity.Comment;
import com.socialize.listener.SocializeListener;

/**
 * Provides a callback for comment view events.
 * @author jasonpolites
 */
public interface OnCommentViewActionListener extends SocializeListener {

	public void onCreate(CommentListView view);
	
	public void onRender(CommentListView view);
	
	public void onCommentList(CommentListView view, List<Comment> comments, int start, int end);
	
	public void onReload(CommentListView view);
	
	public void onPostComment(Comment comment);
}
