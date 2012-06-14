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
package com.socialize.auth.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.DialogFactory;

/**
 * @author Jason Polites
 *
 */
public class FacebookService {
	
	private Activity context;
	private Facebook facebook; 
	private FacebookSessionStore facebookSessionStore; 
	private AuthProviderListener listener;
	private DialogFactory dialogFactory;
	
	protected static final String[] DEFAULT_PERMISSIONS = {"offline_access", "publish_stream"};
	protected static final String[] PHOTO_PERMISSIONS = {"offline_access", "publish_stream", "photo_upload"};
	
	public FacebookService() {
		super();
	}

	public FacebookService(
			Activity context, 
			Facebook facebook, 
			FacebookSessionStore facebookSessionStore, 
			AuthProviderListener listener, 
			DialogFactory dialogFactory) {
		super();
		this.context = context;
		
		this.facebook = facebook;
		this.facebookSessionStore = facebookSessionStore;
		this.listener = listener;
		this.dialogFactory = dialogFactory;
	}
	
	/**
	 * Authenticates with default permissions and Single Sign On.
	 */
	public void authenticate() {
		authenticate(DEFAULT_PERMISSIONS);
	}
	
	public void authenticate(boolean sso) {
		authenticate(DEFAULT_PERMISSIONS, sso);
	}
	
	public void authenticate(boolean sso, boolean photos, String...permissions) {
		if(permissions != null && permissions.length > 0) {
			authenticate(permissions, sso);
		}
		else if(photos) {
			authenticate(PHOTO_PERMISSIONS, sso);
		}
		else {
			authenticate(DEFAULT_PERMISSIONS, sso);
		}
	}
	
	
	/**
	 * Authenticates with Single Sign On.
	 * @param permissions
	 */
	public void authenticate(String[] permissions) {
		authenticate(permissions, true);
	}
	
	public void authenticate(final String[] permissions, final boolean sso) {
		facebookSessionStore.restore(facebook, context);
		
		FacebookDialogListener facebookDialogListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				finish();
			}
			
			@Override
			public void handleError(Throwable error) {
				if(listener != null) {
					listener.onError(new SocializeException(error));
				}
				else {
					doError(error, permissions, sso);
				}
			}
		};
		
		if(sso) {
			facebook.authorize(context, permissions, facebookDialogListener);
		}
		else {
			facebook.authorize(context, permissions, Facebook.FORCE_DIALOG_AUTH, facebookDialogListener);
		}
	}
	
	public void cancel() {
		if(listener != null) {
			listener.onCancel();
		}
		else {
			Toast.makeText(context, "Request cancelled", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void logout() {
		try {
			facebook.logout(context);
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onError(new SocializeException(e));
			}
			else {
				e.printStackTrace();
			}
		}
	}
	
	public void doError(final Throwable e, final String[] permissions, final boolean sso) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				Log.e(SocializeLogger.LOG_TAG, "Facebook error", e);
				doErrorUI(e.getMessage(), permissions, sso);
			}
		});
	}
	
	public void doErrorUI(String error, String[] permissions, boolean sso) {
		try {
			makeErrorDialog(error, permissions, sso).show();
		}
		catch (Exception e) {
			Log.e(SocializeLogger.LOG_TAG, "Facebook error", e);
		}
	}
	
	public void finish() {
		context.finish();
	}
	
	public AlertDialog makeErrorDialog(String error, final String[] permissions, final boolean sso) {
		AlertDialog.Builder builder = dialogFactory.getAlertDialogBuilder(context);
		builder.setTitle("Oops!");
		builder.setMessage("Oops!\nSomething went wrong...\n[" + error + "]");
		builder.setCancelable(false);
		builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				authenticate(permissions, sso);
			}
		});	
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish();
			}
		});	
		
		AlertDialog dialog = builder.create();
		
		return dialog;
	}
	
}
