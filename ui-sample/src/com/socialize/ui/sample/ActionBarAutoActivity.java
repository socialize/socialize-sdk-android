package com.socialize.ui.sample;

import android.os.Bundle;
import android.view.View;

import com.socialize.ui.SocializeUI;

public class ActionBarAutoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState, String entityKey, String entityName, boolean isEntityKeyUrl) {
		View actionBarWrapped = SocializeUI.getInstance().showActionBar(this, R.layout.action_bar_auto, entityKey, entityName, isEntityKeyUrl);
		setContentView(actionBarWrapped);
	}
}
