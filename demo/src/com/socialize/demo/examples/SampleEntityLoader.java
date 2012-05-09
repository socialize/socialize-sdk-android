package com.socialize.demo.examples;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.socialize.entity.Entity;
import com.socialize.ui.SocializeEntityLoader;

public class SampleEntityLoader implements SocializeEntityLoader {

	private Toast toast;

	@Override
	public void loadEntity(Activity activity, Entity entity) {
		
		// Demo only.. you would usually load your entity here.
		String msg = "Clicked on entity with key: " + entity.getKey();
		if(toast != null) {
			toast.cancel();
			toast.setText(msg);
		}
		else {
			toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
		}
		
		toast.show();
	}

	@Override
	public boolean canLoad(Context arg0, Entity arg1) {
		return true;
	}

}
