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
	
	private Facebook facebook; 
	private FacebookSessionStore facebookSessionStore; 
	private AuthProviderListener listener;
	private DialogFactory dialogFactory;
	private SocializeLogger logger;
	
	public static final String[] DEFAULT_PERMISSIONS = {"publish_stream", "publish_actions", "photo_upload"};
	
	public FacebookService() {
		super();
	}

	public FacebookService(
			Facebook facebook, 
			FacebookSessionStore facebookSessionStore, 
			AuthProviderListener listener, 
			DialogFactory dialogFactory,
			SocializeLogger logger) {
		super();
		this.facebook = facebook;
		this.facebookSessionStore = facebookSessionStore;
		this.listener = listener;
		this.dialogFactory = dialogFactory;
		this.logger = logger;
	}
	
	/**
	 * Authenticates with default permissions and Single Sign On.
	 */
	public void authenticate(Activity context) {
		authenticate(context, DEFAULT_PERMISSIONS);
	}
	
	public void authenticate(Activity context, boolean sso) {
		authenticate(context, DEFAULT_PERMISSIONS, sso);
	}
	
	public void authenticate(Activity context, boolean sso, String...permissions) {
		if(permissions != null && permissions.length > 0) {
			authenticate(context, permissions, sso);
		}
		else {
			authenticate(context, DEFAULT_PERMISSIONS, sso);
		}
	}
	
	
	/**
	 * Authenticates with Single Sign On.
	 * @param permissions
	 */
	public void authenticate(Activity context, String[] permissions) {
		authenticate(context, permissions, true);
	}
	
	public void authenticate(final Activity context, final String[] permissions, final boolean sso) {
		facebookSessionStore.restore(facebook, context);
		
		FacebookDialogListener facebookDialogListener = new FacebookDialogListener(context, facebook, facebookSessionStore, listener) {
			
			@Override
			public void onFinish() {
				finish(context);
			}
			
			@Override
			public void handleError(Throwable error) {
				if(listener != null) {
					listener.onError(new SocializeException(error));
				}
				else {
					doError(context, error, permissions, sso);
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
	
	public void cancel(Activity context) {
		if(listener != null) {
			listener.onCancel();
		}
		else {
			Toast.makeText(context, "Request cancelled", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void logout(Activity context) {
		try {
			facebook.logout(context);
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onError(new SocializeException(e));
			}
			else {
				if(logger != null) {
					logger.error(e.getMessage(), e);
				}
				else {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void doError(final Activity context, final Throwable e, final String[] permissions, final boolean sso) {
		context.runOnUiThread(new Runnable() {
			public void run() {
				if(logger != null) {
					logger.error("Facebook error", e);
				}
				else {
					e.printStackTrace();
				}
				
				doErrorUI(context, e.getMessage(), permissions, sso);
			}
		});
	}
	
	public void doErrorUI(final Activity context, String error, String[] permissions, boolean sso) {
		try {
			makeErrorDialog(context, error, permissions, sso).show();
		}
		catch (Exception e) {
			if(logger != null) {
				logger.error("Facebook error", e);
			}
			else {
				e.printStackTrace();
			}
			
		}
	}
	
	public void finish(Activity context) {
		context.finish();
	}
	
	public AlertDialog makeErrorDialog(final Activity context, String error, final String[] permissions, final boolean sso) {
		AlertDialog.Builder builder = dialogFactory.getAlertDialogBuilder(context);
		builder.setTitle("Oops!");
		builder.setMessage("Oops!\nSomething went wrong...\n[" + error + "]");
		builder.setCancelable(false);
		builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				authenticate(context, permissions, sso);
			}
		});	
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish(context);
			}
		});	
		
		AlertDialog dialog = builder.create();
		
		return dialog;
	}
	
}
