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
import android.widget.EditText;

import com.socialize.Socialize;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.error.SocializeErrorHandler;
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
public abstract class BaseShareClickListener implements ShareClickListener {

	private SocializeLogger logger;
	private ActionBarView actionBarView;
	private EditText commentView;
	private SocializeErrorHandler errorHandler;
	
	protected ShareMessageBuilder shareMessageBuilder;
	protected OnActionBarEventListener onActionBarEventListener;
	
	public BaseShareClickListener(ActionBarView actionBarView) {
		this(actionBarView, null, null);
	}
	
	public BaseShareClickListener(ActionBarView actionBarView, EditText commentView, OnActionBarEventListener onActionBarEventListener) {
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
		
		try {

			Activity activity = getActivity(v);

			String comment = null;
			
			if(commentView != null) {
				comment = commentView.getText().toString();
			}
			
			Entity entity = actionBarView.getEntity();

			if(entity != null) {

				String text = comment;

				if(StringUtils.isEmpty(text)) {
					text = entity.getDisplayName();
				}

				// Record the share in Socialize
				if(isDoShareInline()) {
					Socialize.getSocialize().addShare(activity, entity, text, getShareType(), getShareAddListener());	
				}

				doShare(activity, entity, text, getShareAddListener());				
			}
			else {
				if(logger != null) {
					logger.error("Unable to complete share.  Entity key was not found in actionBarView.");
				}
			}
		} 
		catch (Exception e) {
			if(errorHandler != null) {
				errorHandler.handleError(v.getContext(), e);
			}
		}
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}
	
	protected ShareAddListener getShareAddListener() {
		return new ShareAddListener() {
			
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
		};
	}
	
	protected abstract void doShare(Activity context, Entity entity, String comment, ShareAddListener listener);
	
	protected abstract boolean isHtml();
	
	protected abstract boolean isIncludeSocialize();
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.share.ShareClickListener#isAvailableOnDevice(android.app.Activity)
	 */
	@Override
	public abstract boolean isAvailableOnDevice(Activity parent);
	
	/**
	 * Returns true if this listener should do a share to Socialize.
	 * Subclasses override.
	 * @return
	 */
	protected boolean isDoShareInline() {
		return true;
	}
	
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

	public void setErrorHandler(SocializeErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	
}
