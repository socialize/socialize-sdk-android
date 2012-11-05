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
package com.socialize.ui.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.i18n.I18NConstants;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.dialog.AlertDialogFactory;
import com.socialize.ui.dialog.ProgressDialogFactory;


/**
 * @author Jason Polites
 *
 */
public class ShareClickListener implements OnClickListener {
	
	private ShareType shareType;
	private Activity context;
	private Entity entity;
	private ShareInfoProvider provider;
	private ProgressDialogFactory progressDialogFactory;
	private AlertDialogFactory alertDialogFactory;
	private OnActionBarEventListener onActionBarEventListener;
	private ActionBarView actionBarView;
	
	public ShareClickListener(Activity context) {
		super();
		this.context = context;
	}

	public ShareClickListener(
			Activity context, 
			Entity entity, 
			ShareType shareType, 
			ShareInfoProvider provider,
			OnActionBarEventListener onActionBarEventListener,
			ActionBarView actionBarView) {
		this(context);
		this.shareType = shareType;
		this.entity = entity;
		this.provider = provider;
		this.onActionBarEventListener = onActionBarEventListener;
		this.actionBarView = actionBarView;
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		final ProgressDialog dialog = progressDialogFactory.show(view.getContext(), I18NConstants.DLG_SHARE, I18NConstants.DLG_SHARE_MESSAGE + " " + shareType.getDisplayName() + "...");
		
		ShareOptions shareOptions = ShareUtils.getUserShareOptions(context);
		shareOptions.setText(provider.getShareText());
		
		ShareUtils.registerShare(context, entity, shareOptions, new ShareAddListener() {
			@Override
			public void onError(SocializeException error) {
				dialog.dismiss();
				alertDialogFactory.showToast(context, "Share failed.  Please try again");
			}
			
			@Override
			public void onCreate(Share share) {
				dialog.dismiss();
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onPostShare(actionBarView, share);
				}
			}
		}, SocialNetwork.valueOf(shareType));
	}
	
	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setAlertDialogFactory(AlertDialogFactory alertDialogFactory) {
		this.alertDialogFactory = alertDialogFactory;
	}
	
	public void setShareType(ShareType shareType) {
		this.shareType = shareType;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public void setProvider(ShareInfoProvider provider) {
		this.provider = provider;
	}

	public void setOnActionBarEventListener(OnActionBarEventListener onActionBarEventListener) {
		this.onActionBarEventListener = onActionBarEventListener;
	}
	
	public void setActionBarView(ActionBarView actionBarView) {
		this.actionBarView = actionBarView;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
}
