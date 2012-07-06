package com.socialize.sample.ui;

import android.os.Bundle;
import android.view.View;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.sample.R;

public class ActionBarListenerActivity extends ActionBarActivity2 {
	@Override
	protected void onCreate(Bundle savedInstanceState, Entity entity, boolean manual) {
		View actionBarWrapped = Socialize.getSocialize().showActionBar(this, R.layout.action_bar_auto, entity, ActionBarListenerHolder.listener);
		setContentView(actionBarWrapped);
	}
}
