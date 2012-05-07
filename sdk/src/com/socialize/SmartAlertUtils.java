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
package com.socialize;

import android.content.Context;
import android.content.Intent;


/**
 * Utility class to assist in overriding the default behavior of smart alerts.
 * @author Jason Polites
 */
public class SmartAlertUtils {

	/**
	 * Handles a broadcast intent recieved by a Broadcast Receiver on Android.
	 * Call this method if you already have a broadcast reciever defined but want to also utilize Socialize SmartAlerts.
	 * @param context The current context.
	 * @param intent The broadcast intent provided by Android.
	 * @return Returns true if Socialize handled the intent.  This indicates the intent was designed to be handled by Socialize and you do not need to handle it yourself.
	 */
	public static boolean handleBroadcastIntent(Context context, Intent intent) {
		return Socialize.getSocialize().handleBroadcastIntent(context, intent);
	}
}
