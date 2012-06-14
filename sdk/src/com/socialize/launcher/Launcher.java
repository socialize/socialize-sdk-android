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
package com.socialize.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Defines the logic for launching a launch action.
 * @author Jason Polites
 */
public interface Launcher {

	/**
	 * Launches an activity (or anything really) based on the bundle of data provided.
	 * @param context
	 * @param data
	 */
	public boolean launch(Activity context, Bundle data);

	/**
	 * Handles the result from the launch in the activity callback
	 * @param context
	 * @param requestCode
	 * @param resultCode
	 * @param returnedIntent The intent returned from the activity.
	 * @param originalIntent The original intent that launched the activity.
	 */
	public void onResult(Activity context, int requestCode, int resultCode, Intent returnedIntent, Intent originalIntent);

	/**
	 * Returns true if the launcher activity should call finish after launch.
	 * @param context The current context.
	 * @return
	 */
	public boolean shouldFinish(Activity context);
	
	public boolean isAsync();
	
}
