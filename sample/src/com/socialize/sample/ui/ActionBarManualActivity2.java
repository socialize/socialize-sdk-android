package com.socialize.sample.ui;

import android.os.Bundle;

import com.socialize.entity.Entity;
import com.socialize.sample.R;
import com.socialize.ui.actionbar.ActionBarView;

public class ActionBarManualActivity2 extends ActionBarActivity2 {
	@Override
	protected void onCreate(Bundle savedInstanceState, Entity entity) {
		setContentView(R.layout.action_bar_manual);
		ActionBarView socializeActionBarView = (ActionBarView) findViewById(R.id.socializeActionBar);
		socializeActionBarView.setEntity(entity);
		socializeActionBarView.refresh(); // Optional.. only if changing entity key.
	}
}
