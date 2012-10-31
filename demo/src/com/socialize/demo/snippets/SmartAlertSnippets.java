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
package com.socialize.demo.snippets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.gcm.GCMBaseIntentService;
import com.socialize.SmartAlertUtils;


/**
 * @author Jason Polites
 *
 */
public class SmartAlertSnippets extends BroadcastReceiver {
// begin-snippet-0
/*
 * (non-Javadoc)
 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
 */
@Override
public void onReceive(Context context, Intent intent) {
	if(!SmartAlertUtils.handleBroadcastIntent(context, intent)) {
		// Message was NOT handled by Socialize.
	}
}
// end-snippet-0
// begin-snippet-1

public class MyGCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onMessage(Context context, Intent intent) {
		
		if(!SmartAlertUtils.onMessage(context, intent)) {
			// Your code here
		}
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		
		SmartAlertUtils.onRegister(context, registrationId);
		
		// Your code here
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		
		SmartAlertUtils.onUnregister(context, registrationId);
		
		// Your code here
	}
	
	@Override
	protected void onError(Context context, String errorId) {
		
		SmartAlertUtils.onError(context, errorId);
		
		// Your code here
	}
}
//end-snippet-1
}




