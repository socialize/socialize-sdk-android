package com.socialize.ui.sample;

import android.os.Bundle;

import com.socialize.ui.actionbar.ActionBarView;

public class ActionBarManualActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState, String entityKey, String entityName, boolean isEntityKeyUrl) {
		setContentView(R.layout.action_bar_manual);
		ActionBarView socializeActionBarView = (ActionBarView) findViewById(R.id.socializeActionBar);
		socializeActionBarView.setEntityKey(entityKey);
		socializeActionBarView.setEntityName(entityName);
		socializeActionBarView.setEntityKeyIsUrl(isEntityKeyUrl);
		socializeActionBarView.reload(); // Optional.. only if changing entity key.
	}
}
