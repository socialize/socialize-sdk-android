package com.socialize.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;

public class SampleActivity extends Activity {

	Socialize socialize = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sample);
		
		socialize = new Socialize();
		socialize.init(this);
		final SocializeConfig config = socialize.getConfig();

		final EditText txtHost = (EditText) findViewById(R.id.txtHost);
		final EditText txtConsumerKey = (EditText) findViewById(R.id.txtConsumerKey);
		final EditText txtConsumerSecret = (EditText) findViewById(R.id.txtConsumerSecret);
		final TextView txtAuthResult =  (TextView) findViewById(R.id.txtAuthResult);
		
		Button authButton = (Button) findViewById(R.id.btnAuthenticate);
		

		authButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				
				v.setEnabled(false);
				
				String host = txtHost.getText().toString().trim();
				String consumerKey = txtConsumerKey.getText().toString().trim();
				String consumerSecret = txtConsumerSecret.getText().toString().trim();
				
				// Override the location for the API
				config.setProperty(SocializeConfig.API_HOST, host);
				
				socialize.authenticate(consumerKey, consumerSecret, new SocializeAuthListener() {
					
					@Override
					public void onError(SocializeException error) {
						v.setEnabled(true);
						txtAuthResult.setText("FAIL");
						error.printStackTrace();
					}
					
					@Override
					public void onAuthSuccess(SocializeSession session) {
						v.setEnabled(true);
						txtAuthResult.setText("SUCCESS");
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						v.setEnabled(true);
						txtAuthResult.setText("FAIL");
						error.printStackTrace();
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy() {
		if(socialize != null) {
			socialize.destroy();
		}
		super.onDestroy();
	}

	
	
	
}
