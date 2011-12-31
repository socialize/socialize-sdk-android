/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.socialize.notifications;

import com.socialize.util.StringUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Base class for C2D message receiver. Includes constants for the strings used
 * in the protocol.
 */
public abstract class BaseC2DMReceiver extends IntentService {
	public static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";
	public static final String REGISTRATION_CALLBACK_INTENT = "com.google.android.c2dm.intent.REGISTRATION";
	public static final String C2DM_INTENT = "com.google.android.c2dm.intent.RECEIVE";

	// Extras in the registration callback intents.
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

	// wakelock
	private static final String WAKELOCK_KEY = "C2DM_LIB";

	private static PowerManager.WakeLock mWakeLock;

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

	@Override
	public final void onHandleIntent(Intent intent) {
		try {
			Context context = getApplicationContext();
			String action = intent.getAction();
			
			if(!StringUtils.isEmpty(action)) {
				if (action.equals(REGISTRATION_CALLBACK_INTENT)) {
					onRegistrationResponse(context, intent);
				} 
				else if (action.equals(C2DM_INTENT)) {
					onMessage(context, intent);
				} 
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			// Release the power lock, so phone can get back to sleep.
			// The lock is reference counted by default, so multiple
			// messages are ok.

			// If the onMessage() needs to spawn a thread or do something else,
			// it should use it's own lock.
			releaseWaitLock();
		}
	}

	static void acquireWaitLock(Context context) {
		if (mWakeLock == null) {
			// This is called from BroadcastReceiver, there is no init.
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_KEY);
		}
		
		if(!mWakeLock.isHeld()) {
			mWakeLock.acquire();
		}
	}
	
	static void releaseWaitLock() {
		if(mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
	}
	
	/**
	 * Called from the broadcast receiver. Will process the received intent,
	 * call handleMessage(), registered(), etc. in background threads, with a
	 * wake lock, while keeping the service alive.
	 */
	static void onBroadcastReceive(Context context, Intent intent) {
		acquireWaitLock(context);
		// Use a naming convention, similar with how permissions and intents are
		// used. Alternatives are introspection or an ugly use of statics.
		// String receiver = context.getPackageName() + ".C2DMReceiver";
		String receiver = SocializeC2DMReceiver.class.getName();
		intent.setClassName(context, receiver);
		context.startService(intent);
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
			onError(context, "No registration ID in response from C2DM");
		}
	}
}