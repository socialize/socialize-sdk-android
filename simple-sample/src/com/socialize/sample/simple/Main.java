package com.socialize.sample.simple;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class Main extends Activity {
	static final String LOG_KEY = "Socialize";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectNetwork()
        .detectDiskWrites()
        .permitDiskReads()
        .penaltyLog()
        .build());
        
		// Your entity key.  May be passed as a Bundle parameter to your activity
		final String entityKey = "http://getsocialize.com";
		
		// Create an entity object, including a name.
		final Entity entity = Entity.newInstance(entityKey, "Socialize");
		
		// Set an entity loader to allow Socialize to load content within your app
		Socialize.getSocialize().setEntityLoader(new SampleEntityLoader());

		// Wrap your existing view with the action bar.
		// your_layout refers to the resource ID of your current layout.
		View actionBarWrapped = Socialize.getSocializeUI().showActionBar(this, R.layout.main, entity, new ActionBarListener() {

			@Override
			public void onCreate(ActionBarView actionBar) {

				actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {

					@Override
					public void onUpdate(ActionBarView actionBar) {
						// Called when the action bar has its data updated
						Log.i(LOG_KEY, "onUpdate called");
					}

					@Override
					public void onPostUnlike(ActionBarView actionBar) {
						// Called AFTER a user has removed a like
						Log.i(LOG_KEY, "onPostUnlike called");
					}

					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
						// Called AFTER a user has posted a share
						Log.i(LOG_KEY, "onPostShare called");
					}

					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
						// Called AFTER a user has posted a like
						Log.i(LOG_KEY, "onPostLike called");
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
						// Called when the action bar is loaded
						Log.i(LOG_KEY, "onLoad called");
					}

					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
						// Called when the action bar retrieves the like for the current user
						Log.i(LOG_KEY, "onGetLike called");
					}

					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
						// Called when the action bar retrieves the entity data
						Log.i(LOG_KEY, "onGetEntity called for " + entity.getKey());
					}

					@Override
					public void onClick(ActionBarView actionBar, ActionBarEvent evt) {
						// Called when the user clicks on the action bar 
						Log.i(LOG_KEY, "onClick called for " + evt);
					}
				});
			}
		});		

		// Now set the view for your activity to be the wrapped view.
		setContentView(actionBarWrapped);		 
    }
}