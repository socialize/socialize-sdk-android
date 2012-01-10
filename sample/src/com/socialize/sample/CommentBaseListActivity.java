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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.sample.util.ErrorHandler;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.util.StringUtils;

public abstract class CommentBaseListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comment_list);

		Socialize.init(this);

		if(Socialize.getSocialize().isAuthenticated()) {
			
			if(getIntent().getExtras() != null) {
				String key = getIntent().getExtras().getString("key");
				String start = getIntent().getExtras().getString("start");
				String end = getIntent().getExtras().getString("end");
				
				int iStart = 0;
				int iEnd = 100;
				
				if(!StringUtils.isEmpty(start)) {
					iStart = Integer.parseInt(start);
				}	
				if(!StringUtils.isEmpty(end)) {
					iEnd = Integer.parseInt(end);
				}
				
				final ProgressDialog progress = SafeProgressDialog.show(this, "Retrieving", "Please wait...");
				
				doList(key, iStart, iEnd, new CommentListListener() {

					@Override
					public void onError(SocializeException error) {
						progress.dismiss();
						new AlertDialog.Builder(CommentBaseListActivity.this).setMessage("Error " + ErrorHandler.handleApiError(CommentBaseListActivity.this, error)).create().show();
					}

					@Override
					public void onList(ListResult<Comment> result) {

						ArrayList<ListItem> items = new ArrayList<ListItem>(result.getItems().size());

						for (final Comment entity : result.getItems()) {
							items.add(new ListItem() {

								@Override
								public String getName() {
									return entity.getText();
								}

								@Override
								public long getId() {
									return entity.getId();
								}
							});
						}

						ListAdapter adapter = new ListAdapter(CommentBaseListActivity.this, R.layout.list_row, items);
						setListAdapter(adapter);

						final ListView lv = getListView();
						lv.setTextFilterEnabled(true);
						lv.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								ListItem item = (ListItem) lv.getItemAtPosition(position);
								String strId = String.valueOf(item.getId());
								Intent i = new Intent(CommentBaseListActivity.this, CommentGetActivity.class);
								i.putExtra("id", strId);
								startActivity(i);
							}
						});
						
						progress.dismiss();
					}
				});	
			}
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
	protected abstract void doList(String key, int iStart, int iEnd, CommentListListener listener);
}
