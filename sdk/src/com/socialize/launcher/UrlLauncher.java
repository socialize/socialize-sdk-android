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
package com.socialize.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.FullScreenDialogFactory;
import com.socialize.ui.notiifcations.DirectUrlWebView;
import com.socialize.util.StringUtils;


/**
 * @author Jason Polites
 *
 */
public class UrlLauncher implements Launcher {
	
	private SocializeLogger logger;
	private FullScreenDialogFactory dialogFactory;
	private IBeanFactory<DirectUrlWebView> directUrlWebViewFactory;

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#launch(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public boolean launch(Activity context, Bundle data) {
		String url = data.getString(Socialize.DIRECT_URL);
		if(!StringUtils.isEmpty(url)) {
			
//			DirectUrlWebView webView = directUrlWebViewFactory.getBean(context);
//			Dialog build = dialogFactory.build(webView);
//			build.setOnCancelListener(new OnCancelListener() {
//				
//				@Override
//				public void onCancel(DialogInterface dialog) {
//				}
//			});
			
			// TODO: Handle URL
		}
		else {
			handleWarn("No url found under key [" +
					Socialize.DIRECT_URL +
					"]");
		}
		return false;

	}
	
	protected void handleCloseEvent(Activity context) {
		
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
	 * @see com.socialize.launcher.Launcher#onResult(android.app.Activity, int, int, android.content.Intent, android.content.Intent)
	 */
	@Override
	public void onResult(Activity context, int requestCode, int resultCode, Intent returnedIntent, Intent originalIntent) {}

	/* (non-Javadoc)
	 * @see com.socialize.launcher.Launcher#shouldFinish()
	 */
	@Override
	public boolean shouldFinish() {
		return false;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
