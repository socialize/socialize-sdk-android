package com.socialize.sample.ui;

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

import com.socialize.Socialize;
import com.socialize.SocializeAccess;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.sample.R;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.dialog.SafeProgressDialog;

public class SampleActivity2 extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ui_main2);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		final EditText txtEntityName = (EditText) findViewById(R.id.txtEntityName);
		
		final EditText txtFB = (EditText) findViewById(R.id.txtFBId);
		final CheckBox chkSSO = (CheckBox) findViewById(R.id.chkFacebook);
		final CheckBox chkNotifications = (CheckBox) findViewById(R.id.chkNotifications);
		
		Socialize.getSocialize().init(this);
		txtFB.setText(Socialize.getSocialize().getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID));

		final Button btn = (Button) findViewById(R.id.btnCommentView);
		final Button btnClearCache = (Button) findViewById(R.id.btnClearCache);
		final Button btnActionViewAuto = (Button) findViewById(R.id.btnActionViewAuto);
		final Button btnActionViewManual = (Button) findViewById(R.id.btnActionViewManual);
		final Button btnActionViewPager = (Button) findViewById(R.id.btnActionViewPager);
		final Button btnActionButton = (Button) findViewById(R.id.btnActionButton);
		final Button btnActionButtonManual = (Button) findViewById(R.id.btnActionButtonManual);
		
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
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setupOverrides();
				Entity entity = new Entity();
				entity.setKey(txtEntity.getText().toString());
				entity.setName(txtEntityName.getText().toString());				
				Socialize.getSocialize().setEntityLoader(loader);
				Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, String.valueOf(chkNotifications.isChecked()));
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				Socialize.getSocializeUI().showCommentView(SampleActivity2.this, entity);
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
				Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, String.valueOf(chkNotifications.isChecked()));
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		btnActionViewManual.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setupOverrides();
				Intent intent = new Intent(SampleActivity2.this, ActionBarManualActivity2.class);
				
				Entity entity = new Entity();
				entity.setKey(txtEntity.getText().toString());
				entity.setName(txtEntityName.getText().toString());	
				
				intent.putExtra(Socialize.ENTITY_OBJECT, entity);
				
				Socialize.getSocialize().setEntityLoader(loader);
				Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, String.valueOf(chkNotifications.isChecked()));
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});

		btnActionViewPager.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				setupOverrides();
				Intent intent = new Intent(SampleActivity2.this, ActionBarPagerActivity.class);
				
				Entity entity = new Entity();
				entity.setKey(txtEntity.getText().toString());
				entity.setName(txtEntityName.getText().toString());	
				
				intent.putExtra(Socialize.ENTITY_OBJECT, entity);
				
				Socialize.getSocialize().setEntityLoader(loader);
				Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_REGISTER_NOTIFICATION, String.valueOf(chkNotifications.isChecked()));
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		btnActionButtonManual.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SampleActivity2.this, ActionButtonActivity.class);
				i.putExtra("manual", true);
				startActivity(i);
			}
		}); 
		
		btnActionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SampleActivity2.this, ActionButtonActivity.class);
				i.putExtra("manual", false);
				startActivity(i);
			}
		}); 
				
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( txtEntity.getWindowToken(), 0);
		imm.hideSoftInputFromWindow( getWindow().getDecorView().getWindowToken(), 0);
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