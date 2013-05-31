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
package com.socialize.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gcm.GCMRegistrar;
import com.socialize.ConfigUtils;
import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.ui.DefaultSocializeActivityLifecycleListener;
import com.socialize.ui.SocializeUIActivity;
import com.socialize.ui.dialog.DialogRegister;
import com.socialize.ui.dialog.SafeProgressDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author Jason Polites
 *
 */
public abstract class DemoActivity extends Activity implements DialogRegister {
	
	private List<Dialog> dialogs = new ArrayList<Dialog>();
	protected Entity entity;
	
	public static final int PAGE_SIZE = 10;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StictModeUtils.enableDefaults();

		Socialize.onCreate(this, savedInstanceState);

		final SafeProgressDialog dialog = SafeProgressDialog.show(this);

		// NOTE:
		// THIS call to initAsync is NOT required by normal apps.  We are just
		// doing this in our demo to disable some analytics reporting.
		Socialize.initAsync(this, new SocializeInitListener() {
			@Override
			public void onInit(Context context, IOCContainer container) {
				SocializeConfig config = ConfigUtils.getConfig(DemoActivity.this);

				config.setProperty(SocializeConfig.SOCIALIZE_EVENTS_AUTH_ENABLED, "false");
				config.setProperty(SocializeConfig.SOCIALIZE_EVENTS_SHARE_ENABLED, "false");

				String entityKey = config.getProperty("entity.key");
				String entityName = config.getProperty("entity.name");

				entity = Entity.newInstance(entityKey, entityName);
				entity.setType("article");

				// Standard GCM Registration
				// This is simply to verify that SmartAlerts work where there is already a GCM implementation
				// If you are not already using GCM you can ignore this.
				GCMRegistrar.checkDevice(DemoActivity.this);
				GCMRegistrar.checkManifest(DemoActivity.this);

				final String regId = GCMRegistrar.getRegistrationId(DemoActivity.this);

				if (regId.equals("")) {
					GCMRegistrar.register(DemoActivity.this, GCMIntentService.SENDER_ID);
				}

				onCreate();

				dialog.dismiss();
			}

			@Override
			public void onError(SocializeException error) {
				// Handle error
				error.printStackTrace();

				dialog.dismiss();
			}
		});


		// Set a listener for SocializeUIActivity events
		Socialize.setSocializeActivityLifecycleListener(new DefaultSocializeActivityLifecycleListener() {

			@Override
			public boolean onActivityResult(SocializeUIActivity activity, int requestCode, int resultCode, Intent data) {
				toast(activity, "onActivityResult");
				return super.onActivityResult(activity, requestCode, resultCode, data);
			}

			@Override
			public boolean onBackPressed(SocializeUIActivity activity) {
				toast(activity, "onBackPressed");
				return super.onBackPressed(activity);
			}

			@Override
			public boolean onContextItemSelected(SocializeUIActivity activity, MenuItem item) {
				toast(activity, "onContextItemSelected");
				return super.onContextItemSelected(activity, item);
			}

			@Override
			public boolean onContextMenuClosed(SocializeUIActivity activity, Menu menu) {
				toast(activity, "onContextMenuClosed");
				return super.onContextMenuClosed(activity, menu);
			}

			@Override
			public void onCreate(SocializeUIActivity activity, Bundle savedInstanceState) {
				toast(activity, "onCreate");
				super.onCreate(activity, savedInstanceState);
			}

			@Override
			public boolean onCreateContextMenu(SocializeUIActivity activity, ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
				toast(activity, "onCreateContextMenu");
				return super.onCreateContextMenu(activity, menu, v, menuInfo);
			}

			@Override
			public Dialog onCreateDialog(SocializeUIActivity activity, int id, Bundle args) {
				toast(activity, "onCreateDialog");
				return super.onCreateDialog(activity, id, args);
			}

			@Override
			public boolean onCreateOptionsMenu(SocializeUIActivity activity, Menu menu) {
				toast(activity, "onCreateOptionsMenu");
				return super.onCreateOptionsMenu(activity, menu);
			}

			@Override
			public void onDestroy(SocializeUIActivity activity) {
				toast(activity, "onDestroy");
				super.onDestroy(activity);
			}

			@Override
			public boolean onMenuItemSelected(SocializeUIActivity activity, int featureId, MenuItem item) {
				toast(activity, "onMenuItemSelected");
				return super.onMenuItemSelected(activity, featureId, item);
			}

			@Override
			public boolean onMenuOpened(SocializeUIActivity activity, int featureId, Menu menu) {
				toast(activity, "onMenuOpened");
				return super.onMenuOpened(activity, featureId, menu);
			}

			@Override
			public void onNewIntent(SocializeUIActivity activity, Intent intent) {
				toast(activity, "onNewIntent");
				super.onNewIntent(activity, intent);
			}

			@Override
			public boolean onOptionsItemSelected(SocializeUIActivity activity, MenuItem item) {
				toast(activity, "onOptionsItemSelected");
				return super.onOptionsItemSelected(activity, item);
			}

			@Override
			public boolean onOptionsMenuClosed(SocializeUIActivity activity, Menu menu) {
				toast(activity, "onOptionsMenuClosed");
				return super.onOptionsMenuClosed(activity, menu);
			}

			@Override
			public void onPause(SocializeUIActivity activity) {
				toast(activity, "onPause");
				super.onPause(activity);
			}

			@Override
			public void onRestart(SocializeUIActivity activity) {
				toast(activity, "onRestart");
				super.onRestart(activity);
			}

			@Override
			public void onResume(SocializeUIActivity activity) {
				toast(activity, "onResume");
				super.onResume(activity);
			}

			@Override
			public void onStart(SocializeUIActivity activity) {
				toast(activity, "onStart");
				super.onStart(activity);
			}

			@Override
			public void onStop(SocializeUIActivity activity) {
				toast(activity, "onStop");
				super.onStop(activity);
			}
		});
	}

	private void toast(Activity activity, String text) {
		Log.i("Socialize", activity.getClass().getSimpleName() +  ":SocializeActivityLifecycleListener:" + text);
	}

	protected abstract void onCreate();

	/* (non-Javadoc)
	 * @see com.socialize.ui.dialog.DialogRegister#register(android.app.Dialog)
	 */
	@Override
	public void register(Dialog dialog) {
		dialogs.add(dialog);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.dialog.DialogRegister#getDialogs()
	 */
	@Override
	public Collection<Dialog> getDialogs() {
		return dialogs;
	}
	
	protected void handleError(Activity context, Exception error) {
		error.printStackTrace();
		DemoUtils.showErrorDialog(context, error);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Socialize.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Socialize.onResume(this);
	}

	@Override
	protected void onDestroy() {
		if(dialogs != null) {
			for (Dialog dialog : dialogs) {
				dialog.dismiss();
			}
			dialogs.clear();
		}		
		
		Socialize.onDestroy(this);
		
		super.onDestroy();
	}
}
