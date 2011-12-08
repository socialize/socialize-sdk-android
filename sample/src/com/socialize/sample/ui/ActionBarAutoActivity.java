package com.socialize.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.socialize.entity.Entity;
import com.socialize.sample.R;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.actionbar.ActionBarOptions;

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
		View actionBarWrapped = SocializeUI.getInstance().showActionBar(this, R.layout.action_bar_auto, entityKey, options, null);
		setContentView(actionBarWrapped);
	}
}
