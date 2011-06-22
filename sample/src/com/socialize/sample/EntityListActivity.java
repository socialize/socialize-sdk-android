package com.socialize.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.sample.util.ErrorHandler;

public class EntityListActivity<T extends SocializeObject>  extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Socialize.init(this);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			String key = getIntent().getExtras().getString("key");
			
			Socialize.getSocialize().listEntitiesByKey(new EntityListListener() {
				
				@Override
				public void onError(SocializeException error) {
					new AlertDialog.Builder(EntityListActivity.this).setMessage("Error " + ErrorHandler.handleApiError(EntityListActivity.this, error)).create().show();
				}
				
				@Override
				public void onList(List<Entity> entities) {
					
					ArrayList<ListItem> items = new ArrayList<ListItem>(entities.size());
					
					for (final Entity entity : entities) {
						items.add(new ListItem() {
							
							@Override
							public String getName() {
								return entity.getName();
							}
						});
					}
					
					ListAdapter adapter = new ListAdapter(EntityListActivity.this, R.layout.list_row, items);
					setListAdapter(adapter);
				}
			}, key);
		}
	}
	
	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
	
}
