package com.socialize.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.sample.R;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class ActionBarAutoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState, String entityKey, String entityName, boolean isEntityKeyUrl) {
		ActionBarOptions options = new ActionBarOptions();
		options.setEntityName(entityName);
		options.setEntityKeyUrl(isEntityKeyUrl);
		options.setAddScrollView(true);
		
		SocializeUI.getInstance().setEntityLoader(new SocializeEntityLoader() {
			@Override
			public void loadEntity(Activity activity, Entity entity) {
				Toast.makeText(activity, "Clicked on entity with key: " + entity.getKey(), Toast.LENGTH_SHORT).show();
			}
		});
		View actionBarWrapped = SocializeUI.getInstance().showActionBar(this, R.layout.action_bar_auto, entityKey, options, new ActionBarListener() {
			
			@Override
			public void onCreate(ActionBarView view) {
				view.setOnActionBarEventListener(new OnActionBarEventListener() {
					
					@Override
					public void onUpdate(ActionBarView actionBar) {
					}
					
					@Override
					public void onPostUnlike(ActionBarView actionBar) {
					}
					
					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
						Log.i("Socialize", "onPostShare called");
					}
					
					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
					}
					
					@Override
					public void onLoad(ActionBarView actionBar) {
					}
					
					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
					}
					
					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
					}
					
					@Override
					public void onClick(ActionBarView actionBar, ActionBarEvent evt) {
					}
				});
			}
		});
		setContentView(actionBarWrapped);
	}
}
