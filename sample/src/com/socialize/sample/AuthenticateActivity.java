/*
 * Copyright (c) 2011 Socialize Inc. 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.socialize.sample.util.ErrorHandler;

public class AuthenticateActivity extends Activity {
	
	protected Properties properties;
	protected String consumerKey;
	protected String consumerSecret;
	protected String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			loadConfig();
			
			setContentView(R.layout.authenticate);
			
			final SocializeConfig config = Socialize.getSocialize().getConfig();

			final EditText txtHost = (EditText) findViewById(R.id.txtHost);
			final EditText txtConsumerKey = (EditText) findViewById(R.id.txtConsumerKey);
			final EditText txtConsumerSecret = (EditText) findViewById(R.id.txtConsumerSecret);
			
			txtHost.setText(url);
			txtConsumerKey.setText(consumerKey);
			txtConsumerSecret.setText(consumerSecret);
			
			final TextView txtAuthResult =  (TextView) findViewById(R.id.txtAuthResult);
			final TextView txtAuthUserID =  (TextView) findViewById(R.id.txtAuthUserID);
			
			final Button authButton = (Button) findViewById(R.id.btnAuthenticate);
			final Button btnApi = (Button) findViewById(R.id.btnApi);
			final Button btnExit = (Button) findViewById(R.id.btnExit);

			authButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					
					v.setEnabled(false);
					
					String host = txtHost.getText().toString().trim();
					String consumerKey = txtConsumerKey.getText().toString().trim();
					String consumerSecret = txtConsumerSecret.getText().toString().trim();
					
					// Override the location for the API
					config.setProperty(SocializeConfig.API_HOST, host);
					
					final ProgressDialog authProgress = ProgressDialog.show(AuthenticateActivity.this, "Authenticating", "Please wait...");
					
					Socialize.getSocialize().authenticate(consumerKey, consumerSecret, new SocializeAuthListener() {
						
						@Override
						public void onError(SocializeException error) {
							v.setEnabled(true);
							txtAuthResult.setText("FAIL: " + ErrorHandler.handleApiError(AuthenticateActivity.this, error));
							
							btnApi.setVisibility(View.GONE);
							
							authProgress.dismiss();
						}
						
						@Override
						public void onAuthSuccess(SocializeSession session) {
							v.setEnabled(true);
							txtAuthResult.setText("SUCCESS");
							txtAuthUserID.setText(session.getUser().getId().toString());
							
							btnApi.setVisibility(View.VISIBLE);
							
							authProgress.dismiss();
						}
						
						@Override
						public void onAuthFail(SocializeException error) {
							v.setEnabled(true);
							txtAuthResult.setText("FAIL");
							error.printStackTrace();
							
							btnApi.setVisibility(View.GONE);
							
							authProgress.dismiss();
						}
					});
				}
			});
			
			btnApi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(AuthenticateActivity.this, ApiActivity.class);
					startActivity(i);
				}
			});
			
			btnExit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		// For some reason it looks like robotium is clicking the back button here!
		// No idea why, but just removing this ablity to solve the problem.
	}

	@Override
	protected void onDestroy() {
		Log.i(getClass().getSimpleName(), "onDestroy called");
		Socialize.destroy(this);
		super.onDestroy();
	}
	
	private void loadConfig() {
		InputStream in = null;
		try {
			in = getAssets().open("sample-app.conf"); // located in sample app
			
			properties = new Properties();
			properties.load(in);
			
			consumerKey = properties.getProperty("socialize.consumer.key");
			consumerSecret = properties.getProperty("socialize.consumer.secret");
			url = properties.getProperty("socialize.api.url");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}