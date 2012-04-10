package com.socialize.sample.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.ui.dialog.DialogRegister;

public abstract class ActionBarActivity2 extends Activity implements DialogRegister {

	private Set<Dialog> dialogs = new HashSet<Dialog>();	
	
	@Override
	public void register(Dialog dialog) {
		dialogs.add(dialog);
	}

	@Override
	public Collection<Dialog> getDialogs() {
		return dialogs;
	}

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
