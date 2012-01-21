package com.socialize.snippets;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

/**
 * Sample snippets from documentation, just here for reference.
 * 
 * @author jasonpolites
 */
public class SampleActionBarActivity extends Activity {
	Entity entity = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Your entity key. May be passed as a Bundle parameter to your activity
		String entityKey = "http://www.getsocialize.com";

		// Create an entity object including a name
		// The Entity object is Serializable, so you could also store the whole
		// object in the Intent
		Entity entity = Entity.newInstance(entityKey, "Socialize");

		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = Socialize.getSocializeUI().showActionBar(this, R.layout.your_layout, entity);

		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);
	}

	void snippet0() {
		Socialize.getSocializeUI().showActionBar(this, R.layout.your_layout, entity, new ActionBarListener() {
			@Override
			public void onCreate(ActionBarView actionBar) {
				// The ActionBar is really just a fancy LinearLayout
			}
		});
	}

	void snippet1() {

		Socialize.getSocializeUI().showActionBar(this, R.layout.your_layout, entity, new ActionBarListener() {

			@Override
			public void onCreate(ActionBarView actionBar) {

				actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {

					@Override
					public void onUpdate(ActionBarView actionBar) {
						// Called when the action bar has its data updated
					}

					@Override
					public void onPostUnlike(ActionBarView actionBar) {
						// Called AFTER a user has removed a like
					}

					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
						// Called AFTER a user has posted a share
					}

					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
						// Called AFTER a user has posted a like
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
						// Called when the action bar is loaded
					}

					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
						// Called when the action bar retrieves the like for the
						// current user
					}

					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
						// Called when the action bar retrieves the entity data
					}

					@Override
					public void onClick(ActionBarView actionBar, ActionBarEvent evt) {
						// Called when the user clicks on the action bar
					}
				});
			}
		});
	}

	void snippet2() {
		// Setup a listener to retain a reference
		MyActionBarListener listener = new MyActionBarListener();

		// Use the listener when you show the action bar
		Socialize.getSocializeUI().showActionBar(this, R.layout.your_layout, entity, listener);

		// Later (After the action bar has loaded!), you can reference the view to refresh
		ActionBarView view = listener.getActionBarView();

		if (view != null) {
			Entity newEntity = new Entity(); // This would be your new entity
			view.setEntity(newEntity);
			view.refresh();
		}
	}
	
	// Create a class to capture the action bar reference
	public class MyActionBarListener implements ActionBarListener {
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