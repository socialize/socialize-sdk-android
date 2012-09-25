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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 * 
 */
public class SocialNetworkSignOutClickListener implements OnClickListener {

	private Drawables drawables;
	private IBeanFactory<SocialNetworkSignOutTask> signOutTaskFactory;
	private SocializeLogger logger;
	
	private AlertDialog dialog;
	
	private SocialNetworkSignOutListener listener;
	
	private String networkName;
	private String iconImage;
	
	public SocialNetworkSignOutClickListener() {
		super();
	}
	
	@Override
	public void onClick(final View v) {
		dialog = makeDialog(v.getContext());
		dialog.show();
	}
	
	protected AlertDialog makeDialog(final Context context) {
		return new AlertDialog.Builder(context)
		.setIcon((drawables == null) ? null : drawables.getDrawable(iconImage))
		.setTitle("Sign Out of " + networkName)
		.setMessage("Are you sure you want to sign out of " + networkName + "?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				doSignOut(context);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				if(listener != null) {
					listener.onCancel();
				}
			}
		}).create();
	}
	
	protected void doSignOut(Context context) {
		SocialNetworkSignOutTask task = signOutTaskFactory.getBean(context);
		task.setSignOutListener(listener);
		task.doExecute();
	}
	
	protected AlertDialog getDialog() {
		return dialog;
	}

	protected SocialNetworkSignOutListener newSocialNetworkSignOutListener(final View v) {
		return new SocialNetworkSignOutListener() {
			@Override
			public void onCancel() {}

			@Override
			public void onSignOut() {
				getSocialize().saveSession(v.getContext());
			}
		};
	}
	
	protected void exitProfileActivity(final View v) {
		Context context = v.getContext();
		if(context instanceof Activity) {
			((Activity)context).finish();
		}
	}
	
	protected void logError(String msg, Exception error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			SocializeLogger.e(msg, error);
		}
	}
	
	// So we can mock
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setSignOutTaskFactory(IBeanFactory<SocialNetworkSignOutTask> signOutTaskFactory) {
		this.signOutTaskFactory = signOutTaskFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setListener(SocialNetworkSignOutListener listener) {
		this.listener = listener;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}
}
