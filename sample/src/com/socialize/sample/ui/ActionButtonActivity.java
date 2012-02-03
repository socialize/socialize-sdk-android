package com.socialize.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.sample.R;
import com.socialize.ui.actionbutton.SocializeActionButton;

/**
 * @author jasonpolites
 *
 */
public class ActionButtonActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			if(extras.getBoolean("manual")) {
				setContentView(R.layout.action_button);
			}
			else {
				setContentView(R.layout.empty);
				SocializeActionButton<Like> button = new SocializeActionButton<Like>(this);
				button.setEntity(Entity.newInstance("foo", "bar"));
				ViewGroup main = (ViewGroup) findViewById(R.id.main);
				main.addView(button);
			}
		}
		else {
			setContentView(R.layout.action_button);
		}
	}
}
