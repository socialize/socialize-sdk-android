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

import com.socialize.Socialize;
import com.socialize.sample.R;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.SocializeUIBeanOverrider;
import com.socialize.ui.dialog.SafeProgressDialog;

@Deprecated
public class SampleActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ui_main);
		
		final EditText txtEntity = (EditText) findViewById(R.id.txtEntity);
		
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
				SocializeUI.getInstance().destroy(SampleActivity.this, true);
				setupOverrides();
				SocializeUI.getInstance().setFacebookAppId(txtFB.getText().toString());
				SocializeUI.getInstance().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				SocializeUI.getInstance().showCommentView(SampleActivity.this, txtEntity.getText().toString());
			}
		});
		
		btnClearCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressDialog dialog = SafeProgressDialog.show(v.getContext(), "Clearing cache", "Clearing cache...");
				
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						try {
							Socialize.getSocialize().init(SampleActivity.this);
							Socialize.getSocialize().clearSessionCache(SampleActivity.this);
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
				Intent intent = new Intent(SampleActivity.this, ActionBarAutoActivity.class);
				SocializeUI.getInstance().setEntityKey(SampleActivity.this, intent, txtEntity.getText().toString());
				SocializeUI.getInstance().setFacebookAppId(txtFB.getText().toString());
				SocializeUI.getInstance().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		btnActionViewManual.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setupOverrides();
				Intent intent = new Intent(SampleActivity.this, ActionBarManualActivity.class);
				SocializeUI.getInstance().setEntityKey(SampleActivity.this, intent, txtEntity.getText().toString());
				SocializeUI.getInstance().setFacebookAppId(txtFB.getText().toString());
				SocializeUI.getInstance().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		btnActionViewPager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setupOverrides();
				Intent intent = new Intent(SampleActivity.this, ActionBarPagerActivity.class);
				SocializeUI.getInstance().setEntityKey(SampleActivity.this, intent, txtEntity.getText().toString());
				SocializeUI.getInstance().setFacebookAppId(txtFB.getText().toString());
				SocializeUI.getInstance().setFacebookSingleSignOnEnabled(chkSSO.isChecked());
				startActivity(intent);
			}
		});
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow( txtEntity.getWindowToken(), 0);
		imm.hideSoftInputFromWindow( getWindow().getDecorView().getWindowToken(), 0);
	}
	
	protected void setupOverrides() {
		
		final CheckBox chkMockFB = (CheckBox) findViewById(R.id.chkMockFB);
		final CheckBox chkMockSocialize = (CheckBox) findViewById(R.id.chkMockSocialize);
		
		SocializeUIBeanOverrider overrider = new SocializeUIBeanOverrider();
		
		if(chkMockFB.isChecked()) {
			if(chkMockSocialize.isChecked()) {
				overrider.setBeanOverrides("socialize_ui_mock_beans.xml", "socialize_ui_mock_socialize_beans.xml");
			}
			else {
				overrider.setBeanOverrides("socialize_ui_mock_beans.xml");
			}
		}
		else if(chkMockSocialize.isChecked()) {
			overrider.setBeanOverrides("socialize_ui_mock_socialize_beans.xml");
		}
		else {
			overrider.setBeanOverrides((String[]) null);
		}
	}
}