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
package com.socialize.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.api.event.EventSystem;
import com.socialize.concurrent.ManagedAsyncTask;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.error.SocializeException;
import com.socialize.launcher.*;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * Generic launcher activity.
 * @author Jason Polites
 */
public class SocializeLaunchActivity extends Activity {

	public static final String LAUNCH_ACTION = "socialize.launch.action";
	public static final String LAUNCH_TASK = "socialize.launch.task";
	
	protected IOCContainer container;
	protected Launcher launcher;
	protected SocializeLogger logger;
	protected SocializeErrorHandler errorHandler;
	protected Intent originalIntent;
	protected EventSystem eventSystem;
	
	@Override
	protected void onNewIntent(Intent intent) {
		originalIntent = intent;
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		superOnCreate(savedInstanceState);
		
		originalIntent = getIntent();
		
		setupLayout();
		
		new Initializer().execute();
	}
	
	protected void setupLayout() {

		RelativeLayout layout = new RelativeLayout(this);
		
		layout.setBackgroundColor(Color.BLACK);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		TextView text = new TextView(this);
		text.setText("Loading...");
		text.setTextColor(Color.WHITE);
		
		LayoutParams text_params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		text_params.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		text.setLayoutParams(text_params);
		
		layout.addView(text);
		
		layout.setLayoutParams(params);
		
		setContentView(layout);
	}
	
	protected void doInit() {
		initSocialize();
		container = getContainer();
		logger = container.getBean("logger");
		errorHandler = container.getBean("socializeUIErrorHandler");
		eventSystem = container.getBean("eventSystem");
	}
	
	protected void doAuthenticate() {
		// Authenticate the user
		getSocialize().authenticate(this, getAuthListener(container));
	}
	
	protected SocializeAuthListener getAuthListener(final IOCContainer container) {
		
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError(error);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError(error);
			}			
			
			@Override
			public void onCancel() {
				finish();
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				Bundle extras = getIntent().getExtras();
				if(extras != null) {
					String action = extras.getString(LAUNCH_ACTION);
					String task = extras.getString(LAUNCH_TASK);
					
					if(!StringUtils.isEmpty(task)) {
						
						if(logger != null && logger.isDebugEnabled()) {
							logger.debug("Looking for launch task [" +
									task +
									"]");
						}
						
						LaunchTask launchTask = container.getBean(task);
						
						if(launchTask != null) {
							try {
								
								if(logger != null && logger.isDebugEnabled()) {
									logger.debug("Executing launch task [" +
											launchTask.getClass() +
											"]");
								}								
								
								launchTask.execute(SocializeLaunchActivity.this, extras);
							} 
							catch (Throwable e) {
								if(logger != null) {
									logger.warn("Failed to execute launch task [" +
											launchTask.getClass().getName() +
											"]", e);
								}
								else {
									SocializeLogger.e(e.getMessage(), e);
								}
							}
						}
						else {
							if(logger != null) {
								logger.error("Launch task [" +
										launchTask +
										"] specified by no corresponding bean found in the container.");
							}
						}
					}
					
					if(!StringUtils.isEmpty(action)) {
						LaunchManager launchManager = container.getBean("launchManager");
						if(launchManager != null) {
							launcher = launchManager.getLaucher(action);
							if(launcher != null) {
								
								if(launcher.isAsync()) {
									new AsyncLauncher(SocializeLaunchActivity.this, launcher, extras, new LaunchListener() {
										
										@Override
										public void onError(Exception error) {
											handleError(error);
										}
										
										@Override
										public void onAfterLaunch(boolean launched) {
											if(!launched || launcher.shouldFinish(SocializeLaunchActivity.this)) {
												finish();
											}
										}
									}).execute();
								}
								else {
									boolean launched = launcher.launch(SocializeLaunchActivity.this, extras);
									if(!launched || launcher.shouldFinish(SocializeLaunchActivity.this)) {
										finish();
									}
								}
							}
							else {
								finish();
							}
						}
						else {
							finish();
						}
					}
					else {
						finish();
					}
				}	
				else {
					finish();
				}
			}
		};
	}
	
	protected void handleError(Exception error) {
		SocializeLogger.e(error.getMessage(), error);
		if(errorHandler != null) {
			errorHandler.handleError(SocializeLaunchActivity.this, error);
		}		
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(launcher != null) {
			launcher.onResult(this, requestCode, resultCode, data, originalIntent);
		}
		finish();
	}

	protected void superOnCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected IOCContainer getContainer() {
		return ActivityIOCProvider.getInstance().getContainer();
	}
	
	protected void initSocialize() {
		getSocialize().init(this);
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	protected class Initializer extends ManagedAsyncTask<Void, Void, Void>  {

		@Override
		protected Void doInBackground(Void... arg0) {
			doInit();
			return null;
		}

		@Override
		protected void onPostExecuteManaged(Void result) {
			doAuthenticate();
		}
	}
}
