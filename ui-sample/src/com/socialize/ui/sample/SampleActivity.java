package com.socialize.ui.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.socialize.ui.SocializeUI;

public class SampleActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		// These can be specified in a config file 
		// called socialize.properties and places in your assets path.
		final String consumerKey = "807de77a-9981-450a-b0fc-c239c9c3f9b0";
		final String consumerSecret = "0ccc078d-a628-49d0-97ac-bcc892d9666f";
		
		// Your entity key.  May be passed as a Bundle parameter to your activity
		final String entityKey = "http://someurl.com";
		
		final Button btn = (Button) findViewById(R.id.btnCommentView);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SocializeUI.getInstance().setSocializeCredentials(consumerKey, consumerSecret);
				SocializeUI.getInstance().showCommentView(SampleActivity.this, entityKey);
			}
		});
	}
}