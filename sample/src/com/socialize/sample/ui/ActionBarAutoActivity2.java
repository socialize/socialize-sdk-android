package com.socialize.sample.ui;

import java.util.concurrent.CountDownLatch;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.sample.R;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;

public class ActionBarAutoActivity2 extends ActionBarActivity2 {
	
	public final CountDownLatch launchLock = new CountDownLatch(1);

	@Override
	protected void onCreate(Bundle savedInstanceState, Entity entity, boolean manual) {
		
		if(manual) {
			setContentView(R.layout.action_bar_manual);
			ActionBarView socializeActionBarView = (ActionBarView) findViewById(R.id.socializeActionBar);
			socializeActionBarView.setEntity(entity);
			socializeActionBarView.refresh();
			
			launchLock.countDown();
		}
		else {
			ActionBarOptions options = new ActionBarOptions();
			options.setAddScrollView(true);
			
			Socialize.getSocialize().setEntityLoader(new EntityLoader());
			
			View actionBarWrapped = ActionBarUtils.showActionBar(this, R.layout.action_bar_auto, entity, options);
			
			setContentView(actionBarWrapped);
			
			launchLock.countDown();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		Socialize.onDestroy(this);
		super.onDestroy();
	}
}
