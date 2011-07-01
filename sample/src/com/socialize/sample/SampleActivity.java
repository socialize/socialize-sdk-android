package com.socialize.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.socialize.sample.util.ErrorHandler;

public class SampleActivity extends Activity {
	
	protected Properties properties;
	protected String consumerKey;
	protected String consumerSecret;
	protected String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final ProgressDialog progress = ProgressDialog.show(this, "Initializing", "Please wait...");
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				loadConfig();
				Socialize.init(SampleActivity.this);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				setContentView(R.layout.sample);
				
				final SocializeConfig config = Socialize.getSocialize().getConfig();

				final EditText txtHost = (EditText) findViewById(R.id.txtHost);
				final EditText txtConsumerKey = (EditText) findViewById(R.id.txtConsumerKey);
				final EditText txtConsumerSecret = (EditText) findViewById(R.id.txtConsumerSecret);
				
				txtHost.setText(url);
				txtConsumerKey.setText(consumerKey);
				txtConsumerSecret.setText(consumerSecret);
				
				final TextView txtAuthResult =  (TextView) findViewById(R.id.txtAuthResult);
				
				final Button authButton = (Button) findViewById(R.id.btnAuthenticate);
				final Button btnComments = (Button) findViewById(R.id.btnComments);
				final Button btnEntity = (Button) findViewById(R.id.btnEntity);
				final Button btnLike = (Button) findViewById(R.id.btnLike);
				final Button btnShare = (Button) findViewById(R.id.btnShare);
				final Button btnView = (Button) findViewById(R.id.btnView);

				authButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(final View v) {
						
						v.setEnabled(false);
						
						String host = txtHost.getText().toString().trim();
						String consumerKey = txtConsumerKey.getText().toString().trim();
						String consumerSecret = txtConsumerSecret.getText().toString().trim();
						
						// Override the location for the API
						config.setProperty(SocializeConfig.API_HOST, host);
						
						Socialize.getSocialize().authenticate(consumerKey, consumerSecret, new SocializeAuthListener() {
							
							@Override
							public void onError(SocializeException error) {
								v.setEnabled(true);
								txtAuthResult.setText("FAIL: " + ErrorHandler.handleApiError(SampleActivity.this, error));
								
								btnComments.setVisibility(View.GONE);
								btnEntity.setVisibility(View.GONE);
								btnLike.setVisibility(View.GONE);
								btnShare.setVisibility(View.GONE);
								btnView.setVisibility(View.GONE);
							}
							
							@Override
							public void onAuthSuccess(SocializeSession session) {
								v.setEnabled(true);
								txtAuthResult.setText("SUCCESS");
								
								btnComments.setVisibility(View.VISIBLE);
								btnEntity.setVisibility(View.VISIBLE);
								btnLike.setVisibility(View.VISIBLE);
								btnShare.setVisibility(View.VISIBLE);
								btnView.setVisibility(View.VISIBLE);
							}
							
							@Override
							public void onAuthFail(SocializeException error) {
								v.setEnabled(true);
								txtAuthResult.setText("FAIL");
								error.printStackTrace();
								
								btnComments.setVisibility(View.GONE);
								btnEntity.setVisibility(View.GONE);
								btnLike.setVisibility(View.GONE);
								btnShare.setVisibility(View.GONE);
								btnView.setVisibility(View.GONE);
							}
						});
					}
				});
				
				
				btnComments.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(SampleActivity.this, CommentActivity.class);
						startActivity(i);
					}
				});
			
				btnEntity.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(SampleActivity.this, EntityActivity.class);
						startActivity(i);
					}
				});
				
				btnLike.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(SampleActivity.this, LikeActivity.class);
						startActivity(i);
					}
				});
				
				btnShare.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(SampleActivity.this, ShareActivity.class);
						startActivity(i);
					}
				});
				
				btnView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(SampleActivity.this, ViewActivity.class);
						startActivity(i);
					}
				});
				
				progress.dismiss();
			}
		}.execute((Void)null);
		
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
	
	private void loadConfig() {
		InputStream in = null;
		try {
			in = getAssets().open("integration-test.conf"); // located in sample app
			
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
