package com.socialize.ui.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

import com.socialize.Socialize;
import com.socialize.ui.SocializeUI;

public class SampleActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		SocializeUI.getInstance().setFacebookUserCredentials(this, "blah", "blah");
		
		String consumerKey = "f04f5af0-5be0-4ae6-a1f1-8d418c0d7e6b";
		String consumerSecret = "7a9a2b20-d4de-4d46-9c0b-f1653f0f1089";
		
//		String consumerKey = "4d84d055-754b-4b81-9ffa-a44aa1f17fc3";
//		String consumerSecret = "417ad40f-f691-4e48-a8af-9a9f82991cc6";
		
		SocializeUI.getInstance().setAppCredentials(this, consumerKey, consumerSecret);
		SocializeUI.getInstance().setEntityUrl(this, "http://aaaa.com");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Socialize.getSocialize().destroy(true);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
}