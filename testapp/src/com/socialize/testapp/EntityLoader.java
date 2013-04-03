package com.socialize.testapp;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.socialize.entity.Entity;
import com.socialize.ui.SocializeEntityLoader;

public class EntityLoader implements SocializeEntityLoader {

	@Override
	public boolean canLoad(Context context, Entity entity) {
		return true;
	}

	@Override
	public void loadEntity(Activity activity, Entity entity) {
		Toast.makeText(activity, "Clicked on entity with key: " + entity.getKey(), Toast.LENGTH_SHORT).show();
	}

}
