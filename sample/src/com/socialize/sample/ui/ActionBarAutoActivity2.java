package com.socialize.sample.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.sample.R;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

public class ActionBarAutoActivity2 extends ActionBarActivity2 {

	@Override
	protected void onCreate(Bundle savedInstanceState, Entity entity) {
		ActionBarOptions options = new ActionBarOptions();
		options.setAddScrollView(true);
		
		Socialize.getSocialize().setEntityLoader(new EntityLoader());
		
		View actionBarWrapped = Socialize.getSocialize().showActionBar(this, R.layout.action_bar_auto, entity, options, new ActionBarListener() {
			
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
					public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
						return false;
					}
				});
			}
		});
		setContentView(actionBarWrapped);
	}
}
