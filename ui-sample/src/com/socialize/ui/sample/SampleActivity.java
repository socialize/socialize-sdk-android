package com.socialize.ui.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.socialize.ui.SocializeUI;

public class SampleActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		final Button btn = (Button) findViewById(R.id.btnCommentView);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String entityKey = txtEntity.getText().toString();
				SocializeUI.getInstance().showCommentView(SampleActivity.this, entityKey);
			}
		});
	}
}