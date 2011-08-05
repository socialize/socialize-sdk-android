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
import com.socialize.activity.SocializeActivity;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.sample.util.ErrorHandler;

public class AuthenticateActivity extends SocializeActivity {

	protected Properties properties;
	protected String consumerKey;
	protected String consumerSecret;
	protected String url;
	protected String facebookAppId;

	EditText txtHost;
	EditText txtConsumerKey;
	EditText txtConsumerSecret;
	TextView txtAuthResult;
	TextView txtAuthUserID;
	SocializeConfig config;

	Button authButton;
	Button btnAuthenticateFB;
	Button btnApi;
	Button btnExit;
	Button btnClearAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			loadConfig();

			setContentView(R.layout.authenticate);

			txtHost = (EditText) findViewById(R.id.txtHost);
			txtConsumerKey = (EditText) findViewById(R.id.txtConsumerKey);
			txtConsumerSecret = (EditText) findViewById(R.id.txtConsumerSecret);
			txtAuthResult =  (TextView) findViewById(R.id.txtAuthResult);
			txtAuthUserID =  (TextView) findViewById(R.id.txtAuthUserID);
			config = Socialize.getSocialize().getConfig();

			txtHost.setText(url);
			txtConsumerKey.setText(consumerKey);
			txtConsumerSecret.setText(consumerSecret);

			authButton = (Button) findViewById(R.id.btnAuthenticate);
			btnAuthenticateFB = (Button) findViewById(R.id.btnAuthenticateFB);
			btnApi = (Button) findViewById(R.id.btnApi);
			btnExit = (Button) findViewById(R.id.btnExit);
			btnClearAuth = (Button) findViewById(R.id.btnClearAuth);

			authButton.setOnClickListener(new AuthenticateClickListener(false));
			btnAuthenticateFB.setOnClickListener(new AuthenticateClickListener(true));

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

			btnClearAuth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Facebook mFacebook = new Facebook(facebookAppId, null);
					
					try {
						mFacebook.logout(AuthenticateActivity.this);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
					Socialize.getSocialize().clearSessionCache();
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
			facebookAppId = properties.getProperty("facebook.app.id");
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

	class AuthenticateClickListener implements OnClickListener {

		private boolean fb;

		public AuthenticateClickListener(boolean fb) {
			super();
			this.fb = fb;
		}

		@Override
		public void onClick(final View v) {

			v.setEnabled(false);

			String host = txtHost.getText().toString().trim();
			String consumerKey = txtConsumerKey.getText().toString().trim();
			String consumerSecret = txtConsumerSecret.getText().toString().trim();

			// Override the location for the API
			config.setProperty(SocializeConfig.API_HOST, host);

			final ProgressDialog authProgress = ProgressDialog.show(AuthenticateActivity.this, "Authenticating", "Please wait...");

			if(fb) {
				Socialize.getSocialize().authenticate(consumerKey, consumerSecret, AuthProviderType.FACEBOOK, facebookAppId, new AuthListener(v, authProgress));
			}
			else {
				Socialize.getSocialize().authenticate(consumerKey, consumerSecret, new AuthListener(v, authProgress));
			}
		}

	}

	class AuthListener implements SocializeAuthListener {

		View v;
		ProgressDialog authProgress;

		public AuthListener(final View v, final ProgressDialog authProgress) {
			super();
			this.v = v;
			this.authProgress = authProgress;
		}

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
	}

}
