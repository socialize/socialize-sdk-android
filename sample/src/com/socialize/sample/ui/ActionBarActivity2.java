package com.socialize.sample.ui;

import android.app.Activity;
import android.os.Bundle;

public abstract class ActionBarActivity2 extends Activity {

	public static final String ENTITY_KEY = "socialize.entity.key";
	public static final String ENTITY_NAME = "socialize.entity.name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String entityKey = null;
		String entityName = null;
		
		Bundle extras = this.getIntent().getExtras();
		
		if(extras != null) {
			entityKey = extras.getString(ENTITY_KEY);
			entityName = extras.getString(ENTITY_NAME);
		}
		
		onCreate(savedInstanceState, entityKey, entityName);
	}

	protected abstract void onCreate(Bundle savedInstanceState, String entityKey, String entityName);
	
}
