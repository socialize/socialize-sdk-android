package com.socialize.ui.sample;

import android.app.Activity;
import android.os.Bundle;

import com.socialize.ui.SocializeUI;

public class SampleActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SocializeUI.getInstance().setFacebookUserCredentials(this, "blah", "blah");
		SocializeUI.getInstance().setAppCredentials(this, "2db4bc2f-5967-47a3-aba9-11102c8912f4", "fd1d7bb8-56eb-41f1-8ade-375831b5ffcf");
		SocializeUI.getInstance().setEntityUrl(this, "http://aaa.com");
		
		setContentView(R.layout.main);
	}
}