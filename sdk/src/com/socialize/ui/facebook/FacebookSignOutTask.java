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
package com.socialize.ui.facebook;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.socialize.Socialize;
import com.socialize.auth.AuthProviderType;
import com.socialize.ui.dialog.ProgressDialogFactory;

/**
 * @author Jason Polites
 *
 */
public class FacebookSignOutTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private ProgressDialogFactory dialogFactory;
	private FacebookSignOutListener facebookSignOutListener;
	
	private Dialog dialog;
	
	public FacebookSignOutTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		dialog = dialogFactory.show(context, "Signing out", "Please wait...");
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... args) {
		Socialize.getSocialize().clear3rdPartySession(context, AuthProviderType.FACEBOOK);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		dialog.dismiss();
		
		if(facebookSignOutListener != null) {
			facebookSignOutListener.onSignOut();
		}
	}

	public void setDialogFactory(ProgressDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	public void setFacebookSignOutListener(FacebookSignOutListener facebookSignOutListener) {
		this.facebookSignOutListener = facebookSignOutListener;
	}
	
}
