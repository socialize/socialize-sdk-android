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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.socialize.Socialize;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public abstract class ShareClickListener implements OnClickListener {

	private SocializeLogger logger;
	protected ShareMessageBuilder shareMessageBuilder;
	private ActionBarView actionBarView;
	private OnActionBarEventListener onActionBarEventListener;
	private EditText commentView;
	
	public ShareClickListener(ActionBarView actionBarView) {
		this(actionBarView, null, null);
	}
	
	public ShareClickListener(ActionBarView actionBarView, EditText commentView, OnActionBarEventListener onActionBarEventListener) {
		super();
		this.actionBarView = actionBarView;
		this.onActionBarEventListener = onActionBarEventListener;
		this.commentView = commentView;
	}

	protected Activity getActivity(View v) {
		Context context = v.getContext();
		
		if(context instanceof Activity) {
			return (Activity) context;
		}
		else {
			String warn = "Context instance in [" + getClass().getName() + "] is not an Activity";
			
			if(logger != null) {
				logger.warn(warn);
			}
			else {
				System.err.println(warn);
			}
		}
		
		return null;
	}

	@Override
	public final void onClick(View v) {
		Activity activity = getActivity(v);

		String comment = null;
		
		if(commentView != null) {
			comment = commentView.getText().toString();
		}
		
		String entityKey = actionBarView.getEntityKey();

		if(entityKey != null) {

			String text = comment;

			if(StringUtils.isEmpty(text)) {
				text = entityKey;
			}

			// Record the share in Socialize
			Socialize.getSocialize().share(entityKey, text, getShareType(),
				new ShareAddListener() {
	
					@Override
					public void onError(SocializeException error) {
						if(logger != null) {
							logger.error("Error creating share", error);
						}
						else {
							error.printStackTrace();
						}
					}
	
					@Override
					public void onCreate(Share share) {
						// TOOD: Update UI?
						if(onActionBarEventListener != null) {
							onActionBarEventListener.onPostShare(actionBarView, share);
						}
					}
				});			

			String title = "Share";
			String subject = null;
			String body = null;
			if(isGenerateShareMessage()) {
				subject = shareMessageBuilder.buildShareSubject(actionBarView.getEntityKey(), actionBarView.getEntityName());
				body = shareMessageBuilder.buildShareMessage(actionBarView.getEntityKey(), actionBarView.getEntityName(), comment, isHtml(), isIncludeSocialize());
			}

			doShare(activity, title, subject, body, comment);				
		}
		else {
			if(logger != null) {
				logger.error("Unable to complete share.  Entity key was not found in actionBarView.");
			}
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
	
	protected abstract void doShare(Activity parent, String title, String subject, String body, String comment);
	
	protected abstract boolean isHtml();
	
	protected abstract boolean isIncludeSocialize();
	
	public abstract boolean isAvailableOnDevice(Activity parent);
	
	protected abstract ShareType getShareType();
	
	protected boolean isAvailable(Activity parent, Intent intent) {
		return parent.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null;
	}
	
	protected boolean isGenerateShareMessage() {
		return true;
	}

	public void setOnActionBarEventListener(OnActionBarEventListener onActionBarEventListener) {
		this.onActionBarEventListener = onActionBarEventListener;
	}
	
	
}
