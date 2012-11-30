package com.socialize.sample.ui;

import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.entity.Entity;

public abstract class ActionBarActivity2 extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Socialize.onCreate(this, savedInstanceState);
		
		Entity entity = null;
		boolean manual = false;
		Bundle extras = this.getIntent().getExtras();
		
		if(extras != null) {
			entity = (Entity)extras.get(Socialize.ENTITY_OBJECT);
			manual = extras.getBoolean("manual");
		}
		
		onCreate(savedInstanceState, entity, manual);
	}

	protected abstract void onCreate(Bundle savedInstanceState, Entity entity, boolean manual);
	
}
