package com.socialize.snippets;

import android.app.Activity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;

@Deprecated
public class ActionBarReloadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String entityKey = "your_key";

		// Setup a listener to retain a reference
		MyActionBarListener listener = new MyActionBarListener();

		// Use the listener when you show the action bar
		SocializeUI.getInstance().showActionBar(this, R.layout.your_layout, entityKey, listener);

		// Later (After the action bar has loaded!), you can reference the view to refresh
		final ActionBarView view = listener.getActionBarView();
		
		if(view != null) {
			Socialize.getSocialize().addEntity("new_entity", "new_entity_name", new EntityAddListener() {
				
				@Override
				public void onError(SocializeException error) {
					// Handle error
				}
				
				@Override
				public void onCreate(Entity entity) {
					view.setEntityKey(entity.getKey());
					view.refresh();
				}
			});
		}
	}
	
	protected class MyActionBarListener implements ActionBarListener {
		private ActionBarView actionBarView;
		
		@Override
		public void onCreate(ActionBarView actionBar) {
			// Store a reference to the actionBar view
			this.actionBarView = actionBar;
		}

		public ActionBarView getActionBarView() {
			return actionBarView;
		}	
	}
}
