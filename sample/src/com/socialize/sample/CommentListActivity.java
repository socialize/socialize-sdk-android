/*
 * Copyright (c) 2011 Socialize Inc. 
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
package com.socialize.sample;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.sample.util.ErrorHandler;

public class CommentListActivity<T extends SocializeObject>  extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Socialize.init(this);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			String key = getIntent().getExtras().getString("key");
			
			Socialize.getSocialize().listCommentsByEntity(key, new CommentListListener() {
				
				@Override
				public void onError(SocializeException error) {
					new AlertDialog.Builder(CommentListActivity.this).setMessage("Error " + ErrorHandler.handleApiError(CommentListActivity.this, error)).create().show();
				}
				
				@Override
				public void onList(ListResult<Comment> result) {
					
					ArrayList<ListItem> items = new ArrayList<ListItem>(result.getResults().size());
					
					for (final Comment entity : result.getResults()) {
						items.add(new ListItem() {
							
							@Override
							public String getName() {
								return entity.getText();
							}
						});
					}
					
					ListAdapter adapter = new ListAdapter(CommentListActivity.this, R.layout.list_row, items);
					setListAdapter(adapter);
				}
			});
		}
	}
	
	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
	
}
