/*
 * Copyright (c) 2012 Socialize Inc. 
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

import java.util.List;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.User;
import com.socialize.entity.UserAuthData;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.sample.util.ErrorHandler;
import com.socialize.ui.SocializeActivity;
import com.socialize.ui.dialog.SafeProgressDialog;

@Deprecated
public class AuthenticateActivity extends SocializeActivity {

//	protected Properties properties;
	protected String consumerKey;
	protected String consumerSecret;
	protected String url;
	protected String facebookAppId;

	EditText txtHost;
	EditText txtConsumerKey;
	EditText txtConsumerSecret;
	TextView txtAuthResult;
	TextView txtAuthUserID;
	TextView txtAuthUser3rdPartyID;
	SocializeConfig config;

	Button btnAuth;
	Button btnAuthFB;
	
	Button btnCheckAuth;
	Button btnCheckAuthFB;
	
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
			txtAuthUser3rdPartyID =  (TextView) findViewById(R.id.txtAuthUser3rdPartyID);
			config = ConfigUtils.getConfig(this);

			txtHost.setText(url);
			txtConsumerKey.setText(consumerKey);
			txtConsumerSecret.setText(consumerSecret);

			btnAuth = (Button) findViewById(R.id.btnAuthenticate);
			btnAuthFB = (Button) findViewById(R.id.btnAuthenticateFB);
			
			btnCheckAuth = (Button) findViewById(R.id.btnCheckAuthenticate);
			btnCheckAuthFB = (Button) findViewById(R.id.btnCheckAuthenticateFB);
			
			btnApi = (Button) findViewById(R.id.btnApi);
			btnExit = (Button) findViewById(R.id.btnExit);
			btnClearAuth = (Button) findViewById(R.id.btnClearAuth);

			btnAuth.setOnClickListener(new AuthenticateClickListener(false));
			btnAuthFB.setOnClickListener(new AuthenticateClickListener(true));

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
			
			btnCheckAuth.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(Socialize.getSocialize().isAuthenticated()) {
						txtAuthResult.setText("AUTHENTICATED");
					}
					else {
						txtAuthResult.setText("NOT AUTHENTICATED");
					}
				}
			});
			
			btnCheckAuthFB.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
						txtAuthResult.setText("AUTHENTICATED");
					}
					else {
						txtAuthResult.setText("NOT AUTHENTICATED");
					}
				}
			});

			btnClearAuth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					
					final ProgressDialog progress = SafeProgressDialog.show(AuthenticateActivity.this, "Clearing Cache", "Please wait...");
					txtAuthResult.setText("");
					
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							
							Socialize.getSocialize().clearSessionCache(v.getContext());
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							progress.dismiss();
							txtAuthResult.setText("SUCCESS");
						}
					}.execute((Void)null);
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
//		InputStream in = null;
//		try {
			
			
			
			SocializeConfig cfg = ConfigUtils.getConfig(this);
			
			consumerKey =  cfg.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
			consumerSecret =  cfg.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
			url =  cfg.getProperty(SocializeConfig.API_HOST);
			facebookAppId = cfg.getProperty(SocializeConfig.FACEBOOK_APP_ID);		
			

//			in = getAssets().open("sample-app.conf"); // located in sample app
//			properties = new Properties();
//			properties.load(in);
//			consumerKey = getConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_KEY, properties);
//			consumerSecret = getConfigValue(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, properties);
//			url = getConfigValue(SocializeConfig.API_HOST, properties);
//			facebookAppId = getConfigValue(SocializeConfig.FACEBOOK_APP_ID, properties);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		finally {
//			if (in != null) {
//				try {
//					in.close();
//				}
//				catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
	
//	private String getConfigValue(String key, Properties properties) {
//		String value = properties.getProperty(key);
//		if(StringUtils.isEmpty(value)) {
//			value = ConfigUtils.getConfig(this).getProperty(key);
//		}
//		return value;
//	}

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

			final ProgressDialog authProgress = SafeProgressDialog.show(v.getContext(), "Authenticating", "Please wait...");

			if(fb) {
				FacebookAuthProviderInfo fbInfo = new FacebookAuthProviderInfo();
				fbInfo.setAppId(facebookAppId);
				Socialize.getSocialize().authenticate(AuthenticateActivity.this, consumerKey, consumerSecret, fbInfo, new AuthListener(v, authProgress));
			}
			else {
				Socialize.getSocialize().authenticate(AuthenticateActivity.this, consumerKey, consumerSecret, new AuthListener(v, authProgress));
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
		public void onCancel() {}

		@Override
		public void onError(SocializeException error) {
			v.setEnabled(true);
			txtAuthResult.setText("FAIL: " + ErrorHandler.handleApiError(AuthenticateActivity.this, error));

			btnApi.setVisibility(View.GONE);

			try {
				authProgress.dismiss();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		@Override
		public void onAuthSuccess(SocializeSession session) {
			v.setEnabled(true);
			txtAuthResult.setText("SUCCESS");
			txtAuthUserID.setText(session.getUser().getId().toString());

			btnApi.setVisibility(View.VISIBLE);
			
			User user = session.getUser();
			List<UserAuthData> authData = user.getAuthData();
			
			if(authData != null && authData.size() > 0) {
				UserAuthData userAuthData = authData.get(0);
				txtAuthUser3rdPartyID.setText("3rd Party ID: " + userAuthData.getId());
			}
			else {
				txtAuthUser3rdPartyID.setText("3rd Party ID: NONE");
			}

			try {
				authProgress.dismiss();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAuthFail(SocializeException error) {
			v.setEnabled(true);
			txtAuthResult.setText("FAIL");
			error.printStackTrace();

			btnApi.setVisibility(View.GONE);

			try {
				authProgress.dismiss();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
