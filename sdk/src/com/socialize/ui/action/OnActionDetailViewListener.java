/**
 * 
 */
package com.socialize.ui.action;

import com.socialize.entity.Comment;
import com.socialize.listener.SocializeListener;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.comment.CommentListView;

import java.util.List;

/**
 * Provides a callback for action detail UI view events.
 * @author jasonpolites
 */
public interface OnActionDetailViewListener extends SocializeListener {

	public void onCreate(ActionDetailLayoutView view);
	
	public void onRender(ActionDetailLayoutView view);
	
}
