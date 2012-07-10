package com.socialize.sample;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.socialize.ActionUtils;
import com.socialize.Socialize;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;
import com.socialize.sample.util.ErrorHandler;
import com.socialize.ui.dialog.SafeProgressDialog;

public class UserActivityListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_activity_list);

		Socialize.init(this);

		if(Socialize.getSocialize().isAuthenticated()) {
			
			if(getIntent().getExtras() != null) {
				Long key = getIntent().getExtras().getLong("user_id");
				
				final ProgressDialog progress = SafeProgressDialog.show(this, "Retrieving", "Please wait...");
				
				ActionUtils.getActionsByUser(this, key, 0, 10, new ActionListListener() {
					
					@Override
					public void onList(ListResult<SocializeAction> result) {

						ArrayList<ListItem> items = new ArrayList<ListItem>(result.getItems().size());

						for (final SocializeAction entity : result.getItems()) {
							items.add(new ListItem() {

								@Override
								public String getName() {
									
									switch(entity.getActionType()) {
									
									case COMMENT:
										return "COMMENT: " + entity.getDisplayText();
										
									case LIKE: 
										
										return "LIKE: " + entity.getDisplayText();
										
									case VIEW: 
										
										return "VIEW: " + entity.getDisplayText();
										
									case SHARE: 
										
										return "SHARE: " + entity.getDisplayText();
									
									default:
										return "UNKNOWN!";
										
									}
								}

								@Override
								public long getId() {
									return entity.getId();
								}
							});
						}

						ListAdapter adapter = new ListAdapter(UserActivityListActivity.this, R.layout.list_row, items);
						setListAdapter(adapter);
						
						progress.dismiss();
					}
					
					@Override
					public void onError(SocializeException error) {
						progress.dismiss();
						new AlertDialog.Builder(UserActivityListActivity.this).setMessage("Error " + ErrorHandler.handleApiError(UserActivityListActivity.this, error)).create().show();
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
}
