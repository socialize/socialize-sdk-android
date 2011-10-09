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
package com.socialize.ui.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 * 
 */
public class FacebookSignOutClickListener implements OnClickListener {

	private Drawables drawables;
	private IBeanFactory<FacebookSignOutTask> facebookSignOutTaskFactory;
	private SocializeConfig config;
	private SocializeLogger logger;

	@Override
	public void onClick(final View v) {

		new AlertDialog.Builder(v.getContext())
		
		.setIcon(drawables.getDrawable("fb_button.png"))
		.setTitle("Sign Out of Facebook")
		.setMessage("Are you sure you want to sign out of Facebook?")
		.setCancelable(true)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				FacebookSignOutTask task = facebookSignOutTaskFactory.getBean(v.getContext());
				task.setFacebookSignOutListener(new FacebookSignOutListener() {
					@Override
					public void onSignOut() {
						
						String consumerKey = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
						String consumerSecret = config.getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
						
						// Re-authenticate as anonymous
						Socialize.getSocialize().authenticate(consumerKey, consumerSecret, new SocializeAuthListener() {
							
							@Override
							public void onError(SocializeException error) {
								logError("Error during authentication", error);
								exitProfileActivity(v);
							}
							
							@Override
							public void onAuthSuccess(SocializeSession session) {
								exitProfileActivity(v);
							}
							
							@Override
							public void onAuthFail(SocializeException error) {
								logError("Error during authentication", error);
								exitProfileActivity(v);
							}
						});
					}
				});
				
				task.execute((Void[])null);
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		})
		.create()
		.show();
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
			error.printStackTrace();
		}
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setFacebookSignOutTaskFactory(IBeanFactory<FacebookSignOutTask> facebookSignOutTaskFactory) {
		this.facebookSignOutTaskFactory = facebookSignOutTaskFactory;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	
}
