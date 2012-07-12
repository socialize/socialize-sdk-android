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
import android.os.Bundle;
import com.socialize.concurrent.ManagedAsyncTask;


/**
 * @author Jason Polites
 *
 */
public class AsyncLauncher extends ManagedAsyncTask<Void, Void, Boolean> {
	
	private Bundle extras;
	private LaunchListener listener;
	private Activity context;
	private Launcher launcher;
	
	private Exception error;
	

	public AsyncLauncher(Activity context, Launcher launcher, Bundle extras, LaunchListener listener) {
		super();
		this.launcher = launcher;
		this.extras = extras;
		this.listener = listener;
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			return launcher.launch(context, extras);
		}
		catch (Exception e) {
			error = e;
		}
		
		return false;
	}

	@Override
	protected void onPostExecuteManaged(Boolean result) {
		if(listener != null) {
			if(error != null) {
				listener.onError(error);
			}
			else {
				listener.onAfterLaunch(result);
			}
		}
	}
}
