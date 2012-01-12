package com.socialize.snippets;

import android.app.Activity;

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
	
}
