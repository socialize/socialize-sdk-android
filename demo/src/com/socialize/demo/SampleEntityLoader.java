package com.socialize.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.socialize.entity.Entity;
import com.socialize.ui.SocializeEntityLoader;

public class SampleEntityLoader implements SocializeEntityLoader {

	@Override
	public void loadEntity(Activity activity, Entity entity) {
		// Demo only.. you would usually load your entity here.
		Intent intent = new Intent(activity, EntityLoaderActivity.class);
		intent.putExtra("entity", entity);
		activity.startActivity(intent);
	}

	@Override
	public boolean canLoad(Context arg0, Entity arg1) {
		return true;
	}
}
