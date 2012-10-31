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
package com.socialize.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * @author Jason Polites
 *
 */
public class SocializeSmartAlertUtils implements SmartAlertUtilsProxy {
	
	private C2DMCallback c2dmCallback;

	/* (non-Javadoc)
	 * @see com.socialize.notifications.SmartAlertUtilsProxy#onMessage(android.content.Context, android.os.Bundle)
	 */
	@Override
	public boolean onMessage(Context context, Intent intent) {
		
		Bundle messageData = intent.getExtras();
		
		if(messageData != null) {
			String source = messageData.getString(C2DMCallback.SOURCE_KEY);
			if(source != null && source.trim().equalsIgnoreCase(C2DMCallback.SOURCE_SOCIALIZE)) {
				c2dmCallback.onMessage(context, messageData);
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.SmartAlertUtilsProxy#onRegister(android.content.Context, java.lang.String)
	 */
	@Override
	public void onRegister(Context context, String registrationId) {
		c2dmCallback.onRegister(context, registrationId);
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.SmartAlertUtilsProxy#onError(android.content.Context, java.lang.String)
	 */
	@Override
	public void onError(Context context, String errorId) {
		c2dmCallback.onError(context, errorId);
	}

	/* (non-Javadoc)
	 * @see com.socialize.notifications.SmartAlertUtilsProxy#onUnregister(android.content.Context)
	 */
	@Override
	public void onUnregister(Context context, String registrationId) {
		c2dmCallback.onUnregister(context);
	}
	
	public void setC2dmCallback(C2DMCallback c2dmCallback) {
		this.c2dmCallback = c2dmCallback;
	}
}
