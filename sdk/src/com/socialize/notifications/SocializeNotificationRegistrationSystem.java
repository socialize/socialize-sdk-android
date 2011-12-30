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
package com.socialize.notifications;

import android.content.Context;

import com.google.android.c2dm.C2DMessaging;
import com.socialize.config.SocializeConfig;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class SocializeNotificationRegistrationSystem implements NotificationRegistrationSystem {
	
	private SocializeConfig config;
	private SocializeLogger logger;

	/* (non-Javadoc)
	 * @see com.socialize.notifications.NotificationRegistrationSystem#register()
	 */
	@Override
	public void register(Context context) {
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("Registering with C2DM");
		}
		
		String registrationId = C2DMessaging.getRegistrationId(context);
		
		if(StringUtils.isEmpty(registrationId)) {
			C2DMessaging.register(context, config.getProperty(SocializeConfig.SOCIALIZE_C2DM_SENDER_ID));
		}
		else {
			
		}
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.NotificationRegistrationSystem#unregister()
	 */
	@Override
	public void unregister(Context context) {
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("Unregistering from C2DM");
		}
		C2DMessaging.unregister(context);
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
