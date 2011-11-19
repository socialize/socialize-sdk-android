package com.socialize.ui.sample;

import android.os.Bundle;
import android.view.View;

import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarOptions;

public class ActionBarAutoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState, String entityKey, String entityName, boolean isEntityKeyUrl) {
		ActionBarOptions options = new ActionBarOptions();
		options.setEntityName(entityName);
		options.setEntityKeyUrl(isEntityKeyUrl);
		View actionBarWrapped = SocializeUI.getInstance().showActionBar(this, R.layout.action_bar_auto, entityKey, options, null);
		setContentView(actionBarWrapped);
	}
}
