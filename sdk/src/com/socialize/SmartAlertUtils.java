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
package com.socialize;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.C2DMCallback;
import com.socialize.notifications.SocializeC2DMReceiverHandler;


/**
 * Utility class to assist in overriding the default behavior of smart alerts.
 * @author Jason Polites
 */
public class SmartAlertUtils {
	
	// Does not use the SocializeActionProxy because we need to manually init notification beans
	static SocializeC2DMReceiverHandler handler = null;
	static final Object syncLock = new Object();

	/**
	 * Handles a GCM message.  Returns true ONLY if this message was intended for Socialize.
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean onMessage(Context context, Intent intent) {
		assertInitialized(context);
		
		Bundle messageData = intent.getExtras();
		
		if(messageData != null) {
			String source = messageData.getString(C2DMCallback.SOURCE_KEY);
			if(source != null && source.trim().equalsIgnoreCase(C2DMCallback.SOURCE_SOCIALIZE)) {
				handler.onMessage(context, intent);
				return true;
			}
		}

		return false;		
	}
	
	public static void onRegister(Context context, String registrationId) {
		assertInitialized(context);
		handler.onRegistered(context, registrationId);
	}
	
	public static void onError(Context context, String errorId) {
		assertInitialized(context);
		handler.onError(context, errorId);
	}
	
	public static void onUnregister(Context context, String registrationId) {
		assertInitialized(context);
		handler.onUnregistered(context);	
	}
	
	static void assertInitialized(Context context) {
		if(handler == null) {
			synchronized (syncLock) {
				if(handler == null) {
					handler = new SocializeC2DMReceiverHandler();
					handler.onCreate(context);
				}
			}
		}
	}
	
	static void logNotInitializedError() {
		Log.e(SocializeLogger.LOG_TAG, "GCMUtils was not initialized.  Make sure you are calling onCreate in your receiver");
	}	
	
	
	/**
	 * Handles a broadcast intent received by a Broadcast Receiver on Android.
	 * Call this method if you already have a broadcast receiver defined but want to also utilize Socialize SmartAlerts.
	 * @param context The current context.
	 * @param intent The broadcast intent provided by Android.
	 * @return Returns true if Socialize handled the intent.  This indicates the intent was designed to be handled by Socialize and you do not need to handle it yourself.
	 */
	public static boolean handleBroadcastIntent(Context context, Intent intent) {
		return Socialize.getSocialize().handleBroadcastIntent(context, intent);
	}
}
