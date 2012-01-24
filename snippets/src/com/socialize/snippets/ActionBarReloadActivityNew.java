package com.socialize.snippets;

import android.app.Activity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;

public class ActionBarReloadActivityNew extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup a listener to retain a reference
		MyActionBarListener listener = new MyActionBarListener();
		
		Entity entity = Entity.newInstance("your_key", "My Entity");

		// Use the listener when you show the action bar
		Socialize.getSocializeUI().showActionBar(this, R.layout.your_layout, entity, listener);

		// Later (After the action bar has loaded!), you can reference the view to refresh
		final ActionBarView view = listener.getActionBarView();

		if(view != null) {
			
			Entity newEntity = Entity.newInstance("new_entity", "My New Entity");
			
			Socialize.getSocialize().addEntity(this, newEntity, new EntityAddListener() {
				
				@Override
				public void onError(SocializeException error) {
					// Handle error
				}
				
				@Override
				public void onCreate(Entity entity) {
					view.setEntity(entity);
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
