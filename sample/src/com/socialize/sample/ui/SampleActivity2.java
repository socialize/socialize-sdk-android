package com.socialize.sample.ui;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.socialize.CommentUtils;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.sample.R;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.dialog.SafeProgressDialog;

public class SampleActivity2 extends BaseActivity {
	
	public final Lock INIT_LOCK = new ReentrantReadWriteLock().writeLock();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ui_main2);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		final EditText txtEntityName = (EditText) findViewById(R.id.txtEntityName);
		final EditText txtFB = (EditText) findViewById(R.id.txtFBId);
		final EditText txtTW_Key = (EditText) findViewById(R.id.txtTW_Key);
		final EditText txtTW_Sec = (EditText) findViewById(R.id.txtTW_Sec);
		
		final CheckBox chkSSO = (CheckBox) findViewById(R.id.chkFacebook);
		final CheckBox chkNotifications = (CheckBox) findViewById(R.id.chkNotifications);
		final CheckBox chkLocation = (CheckBox) findViewById(R.id.chkLocation);
		
		final Button btnCommentView = (Button) findViewById(R.id.btnCommentView);
		final Button btnClearCache = (Button) findViewById(R.id.btnClearCache);
		final Button btnActionViewAuto = (Button) findViewById(R.id.btnActionViewAuto);
		final Button btnActionViewManual = (Button) findViewById(R.id.btnActionViewManual);
		
		final SafeProgressDialog progress = SafeProgressDialog.show(this);
		
		INIT_LOCK.lock();
		
		Socialize.getSocialize().initAsync(this, new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				progress.dismiss();
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {

				final SocializeConfig config = ConfigUtils.getConfig(SampleActivity2.this);
				
				txtFB.setText(config.getProperty(SocializeConfig.FACEBOOK_APP_ID));
				
				txtTW_Key.setText(config.getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
				txtTW_Sec.setText(config.getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
				
				final SocializeEntityLoader loader = new SocializeEntityLoader() {
					@Override
					public boolean canLoad(Context context, Entity entity) {
						return true;
					}
					
					@Override
					public void loadEntity(Activity activity, Entity entity) {
						Toast.makeText(activity, "Entity loader triggered", Toast.LENGTH_SHORT).show();
					}
				};
				
				btnCommentView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setupOverrides();
						Entity entity = new Entity();
						entity.setKey(txtEntity.getText().toString());
						entity.setName(txtEntityName.getText().toString());				
						Socialize.getSocialize().setEntityLoader(loader);
						config.setProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, String.valueOf(chkNotifications.isChecked()));
						config.setProperty(SocializeConfig.SOCIALIZE_NOTIFICATIONS_ENABLED, String.valueOf(chkNotifications.isChecked()));
						config.setProperty(SocializeConfig.SOCIALIZE_LOCATION_ENABLED, String.valueOf(chkLocation.isChecked()));
						config.setFacebookAppId(txtFB.getText().toString());
						config.setFacebookSingleSignOnEnabled(chkSSO.isChecked());
						config.setTwitterKeySecret(txtTW_Key.getText().toString(), txtTW_Sec.getText().toString());
						CommentUtils.showCommentView(SampleActivity2.this, entity);
					}
				});
				
				btnClearCache.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final ProgressDialog dialog = SafeProgressDialog.show(v.getContext(), "Clearing cache", "Clearing cache...");
						
						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								Socialize.getSocialize().init(SampleActivity2.this);
								Socialize.getSocialize().clearSessionCache(SampleActivity2.this);
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								dialog.dismiss();
							}
						}.execute((Void)null);
					}
				});
				
				btnActionViewAuto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setupOverrides();
						Intent intent = new Intent(SampleActivity2.this, ActionBarAutoActivity2.class);
						
						Entity entity = new Entity();
						entity.setKey(txtEntity.getText().toString());
						entity.setName(txtEntityName.getText().toString());	
						
						intent.putExtra(Socialize.ENTITY_OBJECT, entity);
						Socialize.getSocialize().setEntityLoader(loader);
						config.setProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, String.valueOf(chkNotifications.isChecked()));
						config.setProperty(SocializeConfig.SOCIALIZE_NOTIFICATIONS_ENABLED, String.valueOf(chkNotifications.isChecked()));
						config.setProperty(SocializeConfig.SOCIALIZE_LOCATION_ENABLED, String.valueOf(chkLocation.isChecked()));
						config.setFacebookAppId(txtFB.getText().toString());
						config.setFacebookSingleSignOnEnabled(chkSSO.isChecked());
						config.setTwitterKeySecret(txtTW_Key.getText().toString(), txtTW_Sec.getText().toString());
						startActivity(intent);
					}
				});
				
				btnActionViewManual.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setupOverrides();
						Intent intent = new Intent(SampleActivity2.this, ActionBarAutoActivity2.class);
						
						Entity entity = new Entity();
						entity.setKey(txtEntity.getText().toString());
						entity.setName(txtEntityName.getText().toString());	
						
						intent.putExtra(Socialize.ENTITY_OBJECT, entity);
						intent.putExtra("manual", true);
						
						Socialize.getSocialize().setEntityLoader(loader);
						config.setProperty(SocializeConfig.SOCIALIZE_CHECK_NOTIFICATIONS, String.valueOf(chkNotifications.isChecked()));
						config.setProperty(SocializeConfig.SOCIALIZE_NOTIFICATIONS_ENABLED, String.valueOf(chkNotifications.isChecked()));
						config.setProperty(SocializeConfig.SOCIALIZE_LOCATION_ENABLED, String.valueOf(chkLocation.isChecked()));
						config.setFacebookAppId(txtFB.getText().toString());
						config.setFacebookSingleSignOnEnabled(chkSSO.isChecked());
						config.setTwitterKeySecret(txtTW_Key.getText().toString(), txtTW_Sec.getText().toString());
						startActivity(intent);
					}
				});	
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow( txtEntity.getWindowToken(), 0);
				imm.hideSoftInputFromWindow( txtEntityName.getWindowToken(), 0);
				imm.hideSoftInputFromWindow( txtFB.getWindowToken(), 0);
				imm.hideSoftInputFromWindow( txtTW_Key.getWindowToken(), 0);
				imm.hideSoftInputFromWindow( txtTW_Sec.getWindowToken(), 0);
				imm.hideSoftInputFromWindow( getWindow().getDecorView().getWindowToken(), 0);
				
				progress.dismiss();
				
				INIT_LOCK.unlock();
			}
		});
	}
	
	protected void setupOverrides() {
		
		final CheckBox chkMockFB = (CheckBox) findViewById(R.id.chkMockFB);
		final CheckBox chkMockSocialize = (CheckBox) findViewById(R.id.chkMockSocialize);
		
		if(chkMockFB.isChecked()) {
			if(chkMockSocialize.isChecked()) {
				SocializeAccess.setBeanOverrides("socialize_ui_mock_beans.xml", "socialize_ui_mock_socialize_beans.xml");
			}
			else {
				SocializeAccess.setBeanOverrides("socialize_ui_mock_beans.xml");
			}
		}
		else if(chkMockSocialize.isChecked()) {
			SocializeAccess.setBeanOverrides("socialize_ui_mock_socialize_beans.xml");
		}
		else {
			SocializeAccess.setBeanOverrides((String[]) null);
		}
	}
}