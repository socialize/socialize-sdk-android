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
package com.socialize.demo.implementations.comment;

import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.socialize.CommentUtils;
import com.socialize.demo.R;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.ui.comment.CommentListItem;
import com.socialize.ui.comment.CommentListView;
import com.socialize.ui.comment.OnCommentViewActionListener;


/**
 * @author Jason Polites
 *
 */
public class CommentActivity extends ListActivity {
	final String[] values = new String[] { 
			"Show Comment List",
			"Show Comment List (No Header)", 
			"Show Comment List (Custom Header)", 
			"Add Comment", 
			"Add Comment Without Share", 
			"Get Comments By Entity", 
			"Get Comments By User", 
			"Get Comment By ID"};
	final Class<?>[] activities = new Class<?>[] { 
			CommentViewEmbeddedActivity.class, 
			CommentViewCustomHeaderActivity.class,
			AddCommentActivity.class, 
			AddCommentWithoutShareActivity.class, 
			GetCommentsByEntityActivity.class, 
			GetCommentsByUserActivity.class, 
			GetCommentsByIDActivity.class};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		if(position <= 1) {
			
			String entity = null;
			
			switch (position) {
			case 0:
				entity = "http://getsocialize.com";
				break;
			}
			
			CommentUtils.showCommentView(this, Entity.newInstance(entity, "Socialize"), new OnCommentViewActionListener() {
				
				@Override
				public void onError(SocializeException error) {
					error.printStackTrace();
				}
				
				@Override
				public void onRender(CommentListView view) {
				}
				
				@Override
				public void onReload(CommentListView view) {
				}
				
				@Override
				public void onPostComment(Comment comment) {
				}
				
				@Override
				public void onCreate(CommentListView view) {
				}
				
				@Override
				public void onCommentList(CommentListView view, List<Comment> comments, int start, int end) {
				}

				@Override
				public void onBeforeSetComment(Comment comment, CommentListItem item) {
				}

				@Override
				public void onAfterSetComment(Comment comment, CommentListItem item) {
				}
			});
		}
		else {
			Class<?> activityClass = activities[position-1];
			if(activityClass != null) {
				Intent intent = new Intent(this, activityClass);
				startActivity(intent);
			}
		}
	}
}
