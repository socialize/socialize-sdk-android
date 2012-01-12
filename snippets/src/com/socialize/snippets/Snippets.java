package com.socialize.snippets;

import android.app.Activity;
import android.content.Intent;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.ui.SocializeEntityLoader;

/**
 * Sample snippets from documentation, just here for reference.
 * 
 * @author jasonpolites
 */
public class Snippets {

	void snippet0() {
		Socialize.getSocialize().setEntityLoader(new SocializeEntityLoader() {
			public void loadEntity(Activity activity, Entity entity) {
				// Load the original view for this entity
			}
		});
	}
	
	void snippet1() {
		Socialize.getSocialize().setEntityLoader(new SocializeEntityLoader() {
			public void loadEntity(Activity activity, Entity entity) {
				// Launch an activity from here...
				Intent intent = new Intent(activity, MyContentActivity.class);
				intent.putExtra("some_key", entity.getMetaData());
				activity.startActivity(intent);
			}
		});
	}
	
	class MyContentActivity extends Activity {}
}
