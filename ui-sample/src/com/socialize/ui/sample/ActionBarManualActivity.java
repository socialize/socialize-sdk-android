package com.socialize.ui.sample;

import com.socialize.ui.SocializeUI;

import android.app.Activity;
import android.os.Bundle;

public class ActionBarManualActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SocializeUI.getInstance().setEntityUrl(this, "http://entity3.com");
		setContentView(R.layout.action_bar_manual);
	}
}
