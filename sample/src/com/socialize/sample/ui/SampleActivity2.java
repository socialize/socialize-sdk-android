package com.socialize.sample.ui;

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

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.sample.R;

@SuppressWarnings("deprecation")
public class SampleActivity2 extends SampleActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ui_main2);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		final EditText txtEntityName = (EditText) findViewById(R.id.txtEntityName);
		
		final Entity entity = new Entity();
		entity.setKey(txtEntity.getText().toString());
		entity.setName(txtEntityName.getText().toString());
		
		final EditText txtFB = (EditText) findViewById(R.id.txtFBId);
		final CheckBox chkSSO = (CheckBox) findViewById(R.id.chkFacebook);

		final Button btn = (Button) findViewById(R.id.btnCommentView);
		final Button btnClearCache = (Button) findViewById(R.id.btnClearCache);
		final Button btnActionViewAuto = (Button) findViewById(R.id.btnActionViewAuto);
		final Button btnActionViewManual = (Button) findViewById(R.id.btnActionViewManual);
		final Button btnActionViewPager = (Button) findViewById(R.id.btnActionViewPager);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Socialize.getSocialize().destroy(true);
				setupOverrides();
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				Socialize.getSocializeUI().showCommentView(SampleActivity2.this, entity);
			}
		});
		
		btnClearCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog dialog = ProgressDialog.show(v.getContext(), "Clearing cache", "Clearing cache...");
				
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						try {
							Socialize.getSocialize().init(SampleActivity2.this);
							Socialize.getSocialize().clearSessionCache(SampleActivity2.this);
						} 
						finally {
							Socialize.getSocialize().destroy();
						}
						
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
				
				intent.putExtra(ActionBarAutoActivity2.ENTITY_KEY, entity.getKey());
				intent.putExtra(ActionBarAutoActivity2.ENTITY_NAME, entity.getName());
				
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
				intent.putExtra(ActionBarAutoActivity2.ENTITY_KEY, entity.getKey());
				intent.putExtra(ActionBarAutoActivity2.ENTITY_NAME, entity.getName());
				
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		btnActionViewPager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setupOverrides();
				Intent intent = new Intent(SampleActivity2.this, ActionBarPagerActivity.class);
				intent.putExtra(ActionBarAutoActivity2.ENTITY_KEY, entity.getKey());
				intent.putExtra(ActionBarAutoActivity2.ENTITY_NAME, entity.getName());
				
				Socialize.getSocialize().getConfig().setFacebookAppId(txtFB.getText().toString());
				Socialize.getSocialize().getConfig().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( txtEntity.getWindowToken(), 0);
		imm.hideSoftInputFromWindow( getWindow().getDecorView().getWindowToken(), 0);
	}
}