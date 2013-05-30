/*
 * Copyright (c) 2013 Socialize Inc.
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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;

/**
 * Default implementation of a SocializeActivityLifecycleListener
 * @author Jason Polites
 */
public class DefaultSocializeActivityLifecycleListener implements SocializeActivityLifecycleListener {

	/**
	 * Called BEFORE the default activity onCreate lifecycle event.
	 *
	 * @param activity           The calling activity.
	 * @param savedInstanceState Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
	 */
	@Override
	public void onCreate(SocializeUIActivity activity, Bundle savedInstanceState) {

	}

	/**
	 * Called BEFORE the default activity onPause lifecycle event.
	 *
	 * @param activity The calling activity.
	 */
	@Override
	public void onPause(SocializeUIActivity activity) {

	}

	/**
	 * Called BEFORE the default activity onResume lifecycle event.
	 *
	 * @param activity The calling activity.
	 */
	@Override
	public void onResume(SocializeUIActivity activity) {

	}

	/**
	 * Called BEFORE the default activity onStart lifecycle event.
	 *
	 * @param activity The calling activity.
	 */
	@Override
	public void onStart(SocializeUIActivity activity) {

	}

	/**
	 * Called BEFORE the default activity onStop lifecycle event.
	 *
	 * @param activity The calling activity.
	 */
	@Override
	public void onStop(SocializeUIActivity activity) {

	}

	/**
	 * Called BEFORE the default activity onRestart lifecycle event.
	 *
	 * @param activity The calling activity.
	 */
	@Override
	public void onRestart(SocializeUIActivity activity) {

	}

	/**
	 * Called BEFORE the default activity onDestroy lifecycle event.
	 *
	 * @param activity The calling activity.
	 */
	@Override
	public void onDestroy(SocializeUIActivity activity) {

	}

	/**
	 * Called BEFORE the default activity onNewIntent lifecycle event.
	 *
	 * @param activity The calling activity.
	 * @param intent   The new intent that was started for the activity.
	 */
	@Override
	public void onNewIntent(SocializeUIActivity activity, Intent intent) {

	}

	/**
	 * Called BEFORE the default activity onBackPressed event.
	 *
	 * @param activity The calling activity.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onBackPressed(SocializeUIActivity activity) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onTouchEvent event.
	 *
	 * @param activity The calling activity.
	 * @param event    The touch screen event being processed.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onTouchEvent(SocializeUIActivity activity, MotionEvent event) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onActivityResult event.
	 *
	 * @param activity    The calling activity.
	 * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
	 * @param resultCode  The integer result code returned by the child activity through its setResult().
	 * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onActivityResult(SocializeUIActivity activity, int requestCode, int resultCode, Intent data) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onCreateContextMenu event.
	 *
	 * @param activity The calling activity.
	 * @param menu     The context menu that is being built
	 * @param v        The view for which the context menu is being built
	 * @param menuInfo Extra information about the item for which the context menu should be shown. This information will vary depending on the class of v.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onCreateContextMenu(SocializeUIActivity activity, ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onContextItemSelected event.
	 *
	 * @param activity The calling activity.
	 * @param item     The context menu item that was selected.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onContextItemSelected(SocializeUIActivity activity, MenuItem item) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onContextMenuClosed event.
	 *
	 * @param activity The calling activity.
	 * @param menu     The context menu item that was selected.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onContextMenuClosed(SocializeUIActivity activity, Menu menu) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onCreateOptionsMenu event.
	 *
	 * @param activity The calling activity.
	 * @param menu     The options menu in which you place your items.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onCreateOptionsMenu(SocializeUIActivity activity, Menu menu) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onOptionsItemSelected event.
	 *
	 * @param activity The calling activity.
	 * @param item     The options menu item that was selected.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onOptionsItemSelected(SocializeUIActivity activity, MenuItem item) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onOptionsMenuClosed event.
	 *
	 * @param activity The calling activity.
	 * @param menu     The options menu in which you place your items.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onOptionsMenuClosed(SocializeUIActivity activity, Menu menu) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onMenuItemSelected event.
	 *
	 * @param activity  The calling activity.
	 * @param featureId The panel that the menu is in.
	 * @param item      he options menu item that was selected.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onMenuItemSelected(SocializeUIActivity activity, int featureId, MenuItem item) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onMenuOpened event.
	 *
	 * @param activity  The calling activity.
	 * @param featureId The panel that the menu is in.
	 * @param menu      The options menu in which you place your items.
	 * @return True to PREVENT the default implementation from executing.
	 */
	@Override
	public boolean onMenuOpened(SocializeUIActivity activity, int featureId, Menu menu) {
		return false;
	}

	/**
	 * Called BEFORE the default activity onCreateDialog event.
	 *
	 * @param activity The calling activity.
	 * @param id       The id of the dialog.
	 * @param args     The dialog arguments provided to showDialog(int, Bundle).
	 * @return Returning null will cause the default implementation to execute.
	 */
	@Override
	public Dialog onCreateDialog(SocializeUIActivity activity, int id, Bundle args) {
		return null;
	}
}
