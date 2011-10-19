package com.socialize.ui.sample;

import com.socialize.ui.SocializeUI;

import android.app.Activity;
import android.os.Bundle;

public class ActionBarActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.action_bar);
		
		SocializeUI.getInstance().setEntityUrl(this, "http://entity3.com");
		SocializeUI.getInstance().setContentViewWithActionBar(this, R.layout.action_bar, true);
	}
}
