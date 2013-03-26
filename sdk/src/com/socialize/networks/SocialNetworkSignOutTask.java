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
package com.socialize.networks;

import android.app.Dialog;
import android.content.Context;
import com.socialize.auth.AuthProviderType;
import com.socialize.concurrent.ManagedAsyncTask;
import com.socialize.i18n.I18NConstants;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.util.IAsyncTask;

import java.lang.ref.WeakReference;

/**
 * @author Jason Polites
 *
 */
public class SocialNetworkSignOutTask extends ManagedAsyncTask<Void, Void, Void> implements IAsyncTask<Void, Void, Void> {

	private WeakReference<Context> context;
	private ProgressDialogFactory dialogFactory;
	private SocialNetworkSignOutListener signOutListener;
	
	private Dialog dialog;
	
	private AuthProviderType type;
	
	public SocialNetworkSignOutTask(Context context) {
		super();
		this.context = new WeakReference<Context>(context);
	}

	@Override
	protected void onPreExecute() {
		dialog = dialogFactory.show(context.get(), I18NConstants.DLG_AUTH_SIGNOUT, I18NConstants.PLEASE_WAIT);
	}

	@Override
	public void doExecute(Void... params) {
		execute(params);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... args) {
		switch (type) {
		case FACEBOOK:
			FacebookUtils.unlink(context.get());
			break;
		case TWITTER:
			TwitterUtils.unlink(context.get());
			break;
		}
		return null;
	}

	@Override
	protected void onPostExecuteManaged(Void result) {
		dialog.dismiss();
		
		if(signOutListener != null) {
			signOutListener.onSignOut();
		}
	}

	public void setDialogFactory(ProgressDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	public void setSignOutListener(SocialNetworkSignOutListener facebookSignOutListener) {
		this.signOutListener = facebookSignOutListener;
	}

	public void setType(AuthProviderType type) {
		this.type = type;
	}
	
	public void setType(String type) {
		this.type = AuthProviderType.valueOf(type.toUpperCase().trim());
	}
}
