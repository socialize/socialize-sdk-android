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
package com.socialize.auth.twitter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.ui.dialog.DialogRegistration;

/**
 * @author Jason Polites
 *
 */
public class TwitterAuthUtils {
	
	private SocializeConfig config;
	private TwitterAuthProviderInfo info;
	private IBeanFactory<TwitterAuthView> twitterAuthViewFactory;
	
	public Dialog showAuthDialog(final Context context, TwitterAuthProviderInfo info, final TwitterAuthListener listener) {
		Dialog dialog = newDialog(context);
		dialog.setTitle("Twitter Authentication");
		dialog.setCancelable(true);
		dialog.setOnCancelListener(newOnCancelListener(listener));
		
		TwitterAuthView view = twitterAuthViewFactory.getBean(info.getConsumerKey(), info.getConsumerSecret());

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		view.setLayoutParams(params);
		
		dialog.setContentView(view);
		
		view.setTwitterAuthListener(newTwitterAuthDialogListener(dialog, listener));
		view.authenticate();
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(dialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
	    
	    DialogRegistration.register(context, dialog);
		
		dialog.show();
		
		return dialog;
	}
	
	protected OnCancelListener newOnCancelListener(final TwitterAuthListener listener) {
		return new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(listener != null) {
					listener.onCancel();
				}
			}
		};
	}
	
	protected Dialog newDialog(Context context) {
		return new Dialog(context, android.R.style.Theme_Dialog);
	}
	
	protected TwitterAuthDialogListener newTwitterAuthDialogListener(Dialog dialog, final TwitterAuthListener listener) {
		return new TwitterAuthDialogListener(dialog) {
			@Override
			public void onError(Dialog dialog, Exception e) {
				dialog.dismiss();
				if(listener != null) {
					listener.onError(SocializeException.wrap(e));
				}
			}
			
			@Override
			public void onCancel(Dialog dialog) {
				dialog.dismiss();
				if(listener != null) {
					listener.onCancel();
				}
			}
			
			@Override
			public void onAuthSuccess(Dialog dialog, String token, String secret, String screenName, String userId) {
				dialog.dismiss();
				if(listener != null) {
					listener.onAuthSuccess(token, secret, screenName, userId);
				}
			}
		};
	}
	
	public TwitterAuthProviderInfo getAuthProviderInfo() {
		if(info == null) {
			info = new TwitterAuthProviderInfo();
			info.setConsumerKey(config.getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
			info.setConsumerSecret(config.getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
		}
		return info;
	}
	
	public void setTwitterAuthViewFactory(IBeanFactory<TwitterAuthView> twitterAuthViewFactory) {
		this.twitterAuthViewFactory = twitterAuthViewFactory;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	protected AlertDialog.Builder newAlertDialogBuilder(Context context) {
		return new AlertDialog.Builder(context);
	}
	
	protected TwitterAuthView newTwitterAuthView(Context context) {
		return new TwitterAuthView(context);
	}
}
