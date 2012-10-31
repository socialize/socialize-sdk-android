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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.socialize.log.SocializeLogger;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public abstract class BaseC2DMReceiver extends IntentService {
	
	public static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";
	public static final String REGISTRATION_CALLBACK_INTENT = "com.google.android.c2dm.intent.REGISTRATION";
	public static final String C2DM_INTENT = "com.google.android.c2dm.intent.RECEIVE";
	
	public static final String EXTRA_UNREGISTERED = "unregistered";
	public static final String EXTRA_ERROR = "error";
	public static final String EXTRA_REGISTRATION_ID = "registration_id";

	public static final String ERR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
	public static final String ERR_ACCOUNT_MISSING = "ACCOUNT_MISSING";
	public static final String ERR_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
	public static final String ERR_TOO_MANY_REGISTRATIONS = "TOO_MANY_REGISTRATIONS";
	public static final String ERR_INVALID_PARAMETERS = "INVALID_PARAMETERS";
	public static final String ERR_INVALID_SENDER = "INVALID_SENDER";
	public static final String ERR_PHONE_REGISTRATION_ERROR = "PHONE_REGISTRATION_ERROR";
	
	public BaseC2DMReceiver(String name) {
		super(name);
	}

	/**
	 * Called when a cloud message has been received.
	 */
	protected abstract void onMessage(Context context, Intent intent);

	/**
	 * Called on registration error. Override to provide better error messages.
	 * 
	 * This is called in the context of a Service - no dialog or UI.
	 */
	protected abstract void onError(Context context, String errorId);

	/**
	 * Called when a registration token has been received.
	 */
	protected abstract void onRegistrered(Context context, String registrationId);

	/**
	 * Called when the device has been unregistered.
	 */
	protected abstract void onUnregistered(Context context);
	
	public void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();
		
		if(intent != null) {
			try {
				String action = intent.getAction();
				if(!StringUtils.isEmpty(action)) {
					if (isRegistrationAction(action)) {
						onRegistrationResponse(context, intent);
					} 
					else if (isMessageAction(action)) {
						onMessage(context, intent);
					} 
				}
			} 
			catch (Exception e) {
				// TODO: Handle error
				SocializeLogger.e(e.getMessage(), e);
			}
			finally {
				// Release the power lock, so phone can get back to sleep.
				// The lock is reference counted by default, so multiple
				// messages are ok.

				// If the onMessage() needs to spawn a thread or do something else,
				// it should use it's own lock.
				WakeLock.getInstance().release(context);
			}
		}
	}
	
	protected boolean isRegistrationAction(String action) {
		return action.equals(REGISTRATION_CALLBACK_INTENT);
	}
	
	protected boolean isMessageAction(String action) {
		return action.equals(C2DM_INTENT);
	}	
	
	protected void onRegistrationResponse(final Context context, Intent intent) {
		String registrationId = intent.getStringExtra(EXTRA_REGISTRATION_ID);
		String error = intent.getStringExtra(EXTRA_ERROR);
		String removed = intent.getStringExtra(EXTRA_UNREGISTERED);
		if (!StringUtils.isEmpty(removed)) {
			onUnregistered(context);
		}
		else if(!StringUtils.isEmpty(error)) {
			onError(context, error);
		}
		else if(!StringUtils.isEmpty(registrationId)){
			onRegistrered(context, registrationId);
		}
		else {
			onError(context, "No registration ID in response from GCM");
		}
	}
}