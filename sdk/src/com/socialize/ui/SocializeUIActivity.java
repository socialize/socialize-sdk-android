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
package com.socialize.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import com.socialize.Socialize;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.DialogRegister;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Jason Polites
 *
 */
public abstract class SocializeUIActivity extends Activity implements DialogRegister {
	
	public static final int PROFILE_UPDATE = 1347;
	
	private Set<Dialog> dialogs;

	private SocializeActivityLifecycleListener sall;

	@Override
	public final void onCreate(Bundle savedInstanceState) {

		sall = Socialize.getSocializeActivityLifecycleListener();

		if(sall != null) {
			sall.onCreate(this, savedInstanceState);
		}

		try {
			super.onCreate(savedInstanceState);
			dialogs = new LinkedHashSet<Dialog>();
			onCreateSafe(savedInstanceState);
		}
		catch (Throwable e) {
			SocializeLogger.e("", e);
			finish();
		}
	}


	@Override
	protected void onDestroy() {

		if(sall != null) {
			sall.onDestroy(this);
		}

		if(dialogs != null) {
			for (Dialog dialog : dialogs) {
				dialog.dismiss();
			}
			dialogs.clear();
		}

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if(sall != null) {
			sall.onPause(this);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(sall != null) {
			sall.onResume(this);
		}
		super.onResume();
	}

	@Override
	protected void onStart() {
		if(sall != null) {
			sall.onStart(this);
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		if(sall != null) {
			sall.onStop(this);
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		if(sall != null) {
			sall.onRestart(this);
		}
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		try {
			super.onNewIntent(intent);

			if(sall != null) {
				sall.onNewIntent(this, intent);
			}

			onNewIntentSafe(intent);
		}
		catch (Throwable e) {
			SocializeLogger.e("", e);
			finish();
		}
	}
	
	@Override
	public void register(Dialog dialog) {
		dialogs.add(dialog);
	}
	
	@Override
	public Collection<Dialog> getDialogs() {
		return dialogs;
	}

	@Override
	public void onBackPressed() {
		if(sall == null || !sall.onBackPressed(this)) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(sall == null || !sall.onActivityResult(this, requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if(sall == null || !sall.onCreateContextMenu(this, menu,v, menuInfo)) {
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}

	@Override
	public void onContextMenuClosed(Menu menu) {
		if(sall == null || !sall.onContextMenuClosed(this, menu)) {
			super.onContextMenuClosed(menu);
		}
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		if(sall == null || !sall.onOptionsMenuClosed(this, menu)) {
			super.onOptionsMenuClosed(menu);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		if(sall != null) {
			return sall.onCreateDialog(this, id, args);
		}
		return super.onCreateDialog(id, args);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return sall != null && (sall.onContextItemSelected(this, item) || super.onContextItemSelected(item));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return sall != null && (sall.onCreateOptionsMenu(this, menu) || super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return sall != null && (sall.onOptionsItemSelected(this, item) || super.onOptionsItemSelected(item));
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return sall != null && (sall.onMenuItemSelected(this, featureId, item) || super.onMenuItemSelected(featureId, item));
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return sall != null && (sall.onMenuOpened(this, featureId, menu) || super.onMenuOpened(featureId, menu));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return sall != null && (sall.onTouchEvent(this, event) || super.onTouchEvent(event));
	}

	protected void onNewIntentSafe(Intent intent) {}
	
	protected abstract void onCreateSafe(Bundle savedInstanceState);
}
