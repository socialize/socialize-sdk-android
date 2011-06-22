package com.socialize.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
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
				public void onList(List<Comment> entities) {
					
					ArrayList<ListItem> items = new ArrayList<ListItem>(entities.size());
					
					for (final Comment entity : entities) {
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
