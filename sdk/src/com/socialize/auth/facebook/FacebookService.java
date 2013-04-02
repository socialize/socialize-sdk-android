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
import com.socialize.auth.AuthProviderResponse;
import com.socialize.error.SocializeException;
import com.socialize.facebook.Facebook;
import com.socialize.listener.AuthProviderListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.facebook.FacebookFacade;
import com.socialize.util.DialogFactory;

/**
 * @author Jason Polites
 */
@SuppressWarnings("deprecation")
public class FacebookService {
	
	private FacebookFacade facebookFacade; 
	private AuthProviderListener listener;
	private SocializeLogger logger;
	private String appId;
	private FacebookSessionStore facebookSessionStore;
	private Facebook facebook;
	private DialogFactory dialogFactory;
	
	public FacebookService() {
		super();
	}
	
	@Deprecated
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

	public FacebookService(
			String appId,
			FacebookFacade facebookFacade, 
			AuthProviderListener listener, 
			SocializeLogger logger) {
		super();
		this.appId = appId;
		this.facebookFacade = facebookFacade;
		this.listener = listener;
		this.logger = logger;
	}
	
	/**
	 * Authenticates with default permissions and Single Sign On.
	 */
	@Deprecated
	public void authenticate(Activity context) {
		authenticate(context, FacebookFacade.DEFAULT_PERMISSIONS, true, false);
	}
	
	@Deprecated
	public void authenticate(Activity context, boolean sso) {
		authenticate(context, FacebookFacade.DEFAULT_PERMISSIONS, sso, false);
	}
	
	@Deprecated
	public void authenticate(Activity context, boolean sso, String...permissions) {
		if(permissions != null && permissions.length > 0) {
			authenticate(context, permissions, sso, false);
		}
		else {
			authenticate(context, FacebookFacade.DEFAULT_PERMISSIONS, sso, false);
		}
	}
	
	/**
	 * Authenticates with Single Sign On.
	 */
	public void authenticateForRead(Activity context, boolean sso, String[] permissions) {
		authenticate(context, permissions, sso, true);
	}
	
	public void authenticateForWrite(Activity context, boolean sso, String[] permissions) {
		authenticate(context, permissions, sso, false);
	}	
	
	protected void authenticate(final Activity context, final String[] permissions, final boolean sso, final boolean read) {
		if(facebookFacade != null) {
			
			facebookFacade.authenticate(context, appId, permissions, sso, read, new AuthProviderListener() {
				@Override
				public void onError(SocializeException error) {
					finish(context);
					if(listener != null) {
						listener.onError(error);
					}
				}
				
				@Override
				public void onCancel() {
					finish(context);
					if(listener != null) {
						listener.onCancel();
					}
				}
				
				@Override
				public void onAuthSuccess(AuthProviderResponse response) {
					finish(context);
					if(listener != null) {
						listener.onAuthSuccess(response);
					}
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					finish(context);
					if(listener != null) {
						listener.onAuthFail(error);
					}
				}
			});
		}
		else {
			authenticateLegacy(context, permissions, sso);
		}
	}
	
	@Deprecated
	private void authenticateLegacy(final Activity context, final String[] permissions, final boolean sso) {
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
	
	// Mockable
	public void finish(Activity context) {
		context.finish();
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
			if(facebookFacade != null) {
				facebookFacade.logout(context);
			}
			else if(facebook != null) { // Legacy
				facebook.logout(context);
			}
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
	
	@Deprecated
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
	
	@Deprecated
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
	
	@Deprecated
	public AlertDialog makeErrorDialog(final Activity context, String error, final String[] permissions, final boolean sso) {
		AlertDialog.Builder builder = dialogFactory.getAlertDialogBuilder(context);
		builder.setTitle("Oops!");
		builder.setMessage("Oops!\nSomething went wrong...\n[" + error + "]");
		builder.setCancelable(false);
		builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				authenticate((Activity) context, permissions, sso, false);
			}
		});	
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				finish((Activity) context);
			}
		});

		return builder.create();
	}	
}
