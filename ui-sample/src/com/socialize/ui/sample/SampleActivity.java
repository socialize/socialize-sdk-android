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
		
		// Stage (Socialize Dev App: 196246)
		String consumerKey = "807de77a-9981-450a-b0fc-c239c9c3f9b0";
		String consumerSecret = "0ccc078d-a628-49d0-97ac-bcc892d9666f";
		
		// Prod
//		String consumerKey = "4d84d055-754b-4b81-9ffa-a44aa1f17fc3";
//		String consumerSecret = "417ad40f-f691-4e48-a8af-9a9f82991cc6";

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		SocializeUI.getInstance().setAppCredentials(this, consumerKey, consumerSecret);
		SocializeUI.getInstance().setEntityUrl(this, "http://aaaa.com");
		
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