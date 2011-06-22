package com.socialize.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityCreateListener;
import com.socialize.sample.util.ErrorHandler;

public class EntityActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entity);
		
		Socialize.init(this);
		
		final EditText txtKey = (EditText) findViewById(R.id.txtKey);
		final EditText txtName = (EditText) findViewById(R.id.txtName);
		final TextView txtEntityCreateResult = (TextView) findViewById(R.id.txtEntityCreateResult);
		
		final Button btnEntityCreate = (Button) findViewById(R.id.btnEntityCreate);
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
			btnEntityCreate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String key = txtKey.getText().toString();
					String name = txtName.getText().toString();
					
					Socialize.getSocialize().createEntity(key, name, new EntityCreateListener() {
						
						@Override
						public void onError(SocializeException error) {
							txtEntityCreateResult.setText("FAIL");
							ErrorHandler.handleApiError(EntityActivity.this, error);
						}
						
						@Override
						public void onCreate(Entity entity) {
							txtEntityCreateResult.setText("SUCCESS");
						}
					});
				}
			});
		}
		else {
			// Not authorized, you would normally do a re-auth here
			txtEntityCreateResult.setText("AUTH FAIL");
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
}
