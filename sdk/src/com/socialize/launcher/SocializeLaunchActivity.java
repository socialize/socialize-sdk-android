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

import android.os.Bundle;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.SocializeActivity;

/**
 * Generic launcher activity.
 * @author Jason Polites
 */
public class SocializeLaunchActivity extends SocializeActivity {

	public static final String LAUNCH_ACTION = "socialize.launch.action";
	
	@Override
	protected void onPostSocializeInit(IOCContainer container) {
		super.onPostSocializeInit(container);
		
		// Authenticate the user
		// TODO: this should all be asynchronous (including socialize init) and a loading screen shown.
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			String action = extras.getString(LAUNCH_ACTION);
			
			if(action != null) {
				LaunchManager launchManager = container.getBean("launchManager");
				
				if(launchManager != null) {
					Launcher launcher = launchManager.getLaucher(action);
					
					if(launcher != null) {
						launcher.launch(this, extras);
					}
				}
			}
		}
		
		finish();
	}
	
	protected String getConsumerKey(IOCContainer container) {
		return getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY);
	}
	
	protected String getConsumerSecret(IOCContainer container) {
		return getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
	}
	
	protected String getFacebookAppId(IOCContainer container) {
		return getSocialize().getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID);
	}
}
