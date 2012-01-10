package com.socialize.sample.ui;

import android.app.Activity;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Entity;

public abstract class ActionBarActivity2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Entity entity = null;
		
		Bundle extras = this.getIntent().getExtras();
		
		if(extras != null) {
			entity = (Entity)extras.get(Socialize.ENTITY_OBJECT);
		}
		
		onCreate(savedInstanceState, entity);
	}

	protected abstract void onCreate(Bundle savedInstanceState, Entity entity);
	
}
