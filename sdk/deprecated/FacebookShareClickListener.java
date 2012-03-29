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
package com.socialize.ui.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.EditText;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookSharer;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.dialog.AlertDialogFactory;
import com.socialize.ui.dialog.ProgressDialogFactory;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class FacebookShareClickListener extends BaseShareClickListener {

	private ProgressDialogFactory progressDialogFactory;
	private AlertDialogFactory alertDialogFactory;
	private FacebookSharer facebookSharer;

	public FacebookShareClickListener(ActionBarView actionBarView) {
		super(actionBarView);
	}

	public FacebookShareClickListener(ActionBarView actionBarView, EditText commentView, OnActionBarEventListener onActionBarEventListener) {
		super(actionBarView, commentView, onActionBarEventListener);
	}
	
	@Override
	public boolean isAvailableOnDevice(Activity parent) {
		return getSocialize().isSupported(AuthProviderType.FACEBOOK);
	}	

	@Override
	protected void doShare(Activity context, Entity entity, String comment, ShareAddListener listener) {
		facebookSharer.shareEntity(context, entity, comment, true, new SocialNetworkListener() {
			ProgressDialog dialog;

			@Override
			public void onBeforePost(Activity parent, SocialNetwork network) {
				dialog = progressDialogFactory.show(parent, "Share", "Sharing to Facebook...");
			}

			@Override
			public void onAfterPost(Activity parent, SocialNetwork network) {
				if(dialog != null) dialog.dismiss();
				alertDialogFactory.show(parent, "Success", "Share successful!");
			}

			@Override
			public void onError(Activity parent, SocialNetwork network, String message, Throwable error) {
				if(dialog != null) dialog.dismiss();
				alertDialogFactory.show(parent, "Error", "Share failed.  Please try again");
			}
		});
	}
	
	@Override
	protected boolean isHtml() {
		return false;
	}
	
	@Override
	protected boolean isIncludeSocialize() {
		return true;
	}
	
	@Override
	protected ShareType getShareType() {
		return ShareType.FACEBOOK;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setAlertDialogFactory(AlertDialogFactory alertDialogFactory) {
		this.alertDialogFactory = alertDialogFactory;
	}

	public void setFacebookSharer(FacebookSharer facebookSharer) {
		this.facebookSharer = facebookSharer;
	}
}
