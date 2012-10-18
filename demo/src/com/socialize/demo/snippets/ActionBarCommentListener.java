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
package com.socialize.demo.snippets;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.demo.R;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.OnCommentViewActionListener;

//begin-snippet-0
public class ActionBarCommentListener extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityKey = "http://www.getsocialize.com";
		
		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole object in the Intent
		Entity entity = Entity.newInstance(entityKey, "Socialize");
		
		// Add more options
		ActionBarOptions options = new ActionBarOptions();
		
		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = ActionBarUtils.showActionBar(this, R.layout.actionbar, entity, options, new ActionBarListener() {
			
			@Override
			public void onCreate(ActionBarView actionBar) {
				
				actionBar.setOnCommentViewActionListener(new OnCommentViewActionListener() {

					@Override
					public void onError(SocializeException error) {
						// Handle error
					}
					
					@Override
					public void onRender(CommentListView view) {
						// Called when the list view is rendered
					}
					
					@Override
					public void onReload(CommentListView view) {
						// Called when a reload event is posted to the list view.
					}
					
					@Override
					public void onPostComment(Comment comment) {
						// Called after a comment is posted.
					}
					
					@Override
					public void onCreate(CommentListView view) {
						// Called when the list view component was created (but may not be shown)
					}
					
					@Override
					public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {
						// Called when a list of comments is retrieved.
					}

					@Override
					public void onBeforeSetComment(Comment comment, CommentListItem item) {
						// Called before a single comment is set on the comment list item view
					}

					@Override
					public void onAfterSetComment(Comment comment, CommentListItem item) {
						// Called after a single comment is set on the comment list item view
					}
				});
			}
		});
		
		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);
	}
}
//end-snippet-0


