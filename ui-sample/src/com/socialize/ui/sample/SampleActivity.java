package com.socialize.ui.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.socialize.ui.SocializeUI;

public class SampleActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		Button btn = (Button) findViewById(R.id.btnCommentView);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String entityKey = txtEntity.getText().toString();
				
//				SocializeUI.getInstance().setFacebookUserCredentials(this, "blah", "blah");
//				SocializeUI.getInstance().setSocializeCredentials("807de77a-9981-450a-b0fc-c239c9c3f9b0", "0ccc078d-a628-49d0-97ac-bcc892d9666f");
				SocializeUI.getInstance().showCommentView(SampleActivity.this, entityKey);
			}
		});
	}
}