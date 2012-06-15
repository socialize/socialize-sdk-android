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
package com.socialize.launcher;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.FullScreenDialogFactory;
import com.socialize.ui.notifications.DirectUrlListener;
import com.socialize.ui.notifications.DirectUrlWebView;
import com.socialize.util.DefaultAppUtils;
import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class SocializeUrlLauncher extends BaseLauncher implements UrlLauncher {
	
	private SocializeLogger logger;
	private FullScreenDialogFactory dialogFactory;
	private IBeanFactory<DirectUrlWebView> directUrlWebViewFactory;
	private DirectUrlListener directUrlListener;

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#launch(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public boolean launch(final Activity context, Bundle data) {
		String url = data.getString(Socialize.DIRECT_URL);
		
		if(!StringUtils.isEmpty(url)) {
			
			final DirectUrlWebView webView = directUrlWebViewFactory.getBean();
			webView.setListener(directUrlListener);
			
			Dialog dialog = dialogFactory.build(context, webView, true);
			
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					handleCloseEvent(webView, context);
				}
			});
			
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					handleCloseEvent(webView, context);
				}
			});
			
			dialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK) {
						if(webView.canGoBack()) {
							webView.goBack();
							return true;
						}
					}
					return false;
				}
			});
			
		
			boolean show = true;
			
			if(directUrlListener != null) {
				show = directUrlListener.onBeforePageLoaded(webView, url);
			}
			
			if(show) {
				dialog.show();
				webView.loadUrl(url);
			}
			
			return show;
		}
		else {
			handleWarn("No url found under key [" +
					Socialize.DIRECT_URL +
					"]");
		}
		
		return false;
	}
	
	protected void handleCloseEvent(WebView webView, Activity context) {
		
		if(directUrlListener != null) {
			directUrlListener.onDialogClose();
		}
		
		if(webView != null) {
			try {
				webView.destroy();
			}
			catch (Throwable ignore) {}
		}		
		
		DefaultAppUtils.launchMainApp(context);
		context.finish();
	}
	
	protected void handleWarn(String msg) {
		if(logger != null) {
			logger.warn(msg);
		}
		else {
			System.err.println(msg);
		}
	}	

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#shouldFinish()
	 */
	@Override
	public boolean shouldFinish(Activity context) {
		// Don't finish!
		return false;
	}
	
	@Override
	public boolean isAsync() {
		return false;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setDialogFactory(FullScreenDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	public void setDirectUrlWebViewFactory(IBeanFactory<DirectUrlWebView> directUrlWebViewFactory) {
		this.directUrlWebViewFactory = directUrlWebViewFactory;
	}
	
	public void setDirectUrlListener(DirectUrlListener directUrlListener) {
		this.directUrlListener = directUrlListener;
	}
	
}
