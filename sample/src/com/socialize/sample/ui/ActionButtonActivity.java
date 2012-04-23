package com.socialize.sample.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetwork;
import com.socialize.sample.R;
import com.socialize.ui.actionbutton.ActionButtonConfig;
import com.socialize.ui.actionbutton.SocializeLikeButton;
import com.socialize.ui.util.Colors;

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
				SocializeLikeButton button = (SocializeLikeButton) findViewById(R.id.socializeLikeButton);
				button.setEntity(Entity.newInstance("foo", "bar"));
			}
			else {
				setContentView(R.layout.empty);
				SocializeLikeButton button = new SocializeLikeButton(this);
				button.setEntity(Entity.newInstance("foo", "bar"));
				
				ActionButtonConfig config = button.getConfig();
				config.setBackgroundColor(Colors.parseColor("#555555"));
				config.setImageResIdOn(R.drawable.action_button_on);
				config.setImageResIdOff(R.drawable.action_button_off);
				config.setImageResIdDisabled(R.drawable.action_button_disabled);
				config.setShareToNetworks(SocialNetwork.FACEBOOK);
				config.setAutoAuth(true);
				config.setTextColor(Color.WHITE);
				config.setShowCount(true);
				config.setTextOff("Like");
				config.setTextOn("Unlike");
				
				ViewGroup main = (ViewGroup) findViewById(R.id.main);
				main.addView(button);
			}
		}
		else {
			setContentView(R.layout.action_button);
		}
	}
}
