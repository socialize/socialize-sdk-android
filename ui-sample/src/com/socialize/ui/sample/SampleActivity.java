package com.socialize.ui.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.socialize.ui.SocializeUI;

public class SampleActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		
		final EditText txtFB = (EditText) findViewById(R.id.txtFBId);
		final CheckBox chkSSO = (CheckBox) findViewById(R.id.chkFacebook);
		final Button btn = (Button) findViewById(R.id.btnCommentView);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String entityKey = txtEntity.getText().toString();
				SocializeUI.getInstance().setFacebookAppId(txtFB.getText().toString());
				SocializeUI.getInstance().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				SocializeUI.getInstance().showCommentView(SampleActivity.this, entityKey);
			}
		});
	}
}