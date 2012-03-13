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
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.dialog.AlertDialogFactory;
import com.socialize.ui.dialog.ProgressDialogFactory;

/**
 * @author Jason Polites
 *
 */
public class TwitterShareClickListener extends BaseShareClickListener {

	private ProgressDialogFactory progressDialogFactory;
	private AlertDialogFactory alertDialogFactory;

	public TwitterShareClickListener(ActionBarView actionBarView) {
		super(actionBarView);
	}

	public TwitterShareClickListener(ActionBarView actionBarView, EditText commentView, OnActionBarEventListener onActionBarEventListener) {
		super(actionBarView, commentView, onActionBarEventListener);
	}
	
	@Override
	public boolean isAvailableOnDevice(Activity parent) {
		return getSocialize().isSupported(AuthProviderType.TWITTER);
	}	
	
	@Override
	protected boolean isDoShareInline() {
		return false;
	}

	@Override
	protected void doShare(final Activity context, Entity entity, String comment, final ShareAddListener listener) {
		final ProgressDialog dialog = progressDialogFactory.show(context, "Share", "Sharing to Twitter...");
		ShareOptions options = new ShareOptions();
		options.setShareTo(SocialNetwork.TWITTER);
		options.setShareLocation(true);
		Socialize.getSocialize().share(context, entity, comment, options, new ShareAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				if(dialog != null) dialog.dismiss();
				
				if(listener != null) {
					listener.onError(error);
				}
				
				alertDialogFactory.show(context, "Error", "Share failed.  Please try again");				
			}
			
			@Override
			public void onCreate(Share entity) {
				if(dialog != null) dialog.dismiss();
				
				if(listener != null) {
					listener.onCreate(entity);
				}
				
				alertDialogFactory.show(context, "Success", "Share successful!");				
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
		return ShareType.TWITTER;
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
}
