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
package com.socialize.util;

import com.socialize.error.SocializeException;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Jason Polites
 *
 */
public class ServiceStartWaitTask extends AsyncTask<Void, Void, Boolean> {

	private ServiceStartListener listener;
	private long timeout;
	private Context context;
	private Class<?> serviceClass;
	private DeviceUtils deviceUtils;
	private SocializeException error;
	
	public ServiceStartWaitTask(Context context, Class<?> serviceClass, DeviceUtils deviceUtils, ServiceStartListener listener, long timeout) {
		super();
		this.context = context;
		this.serviceClass = serviceClass;
		this.listener = listener;
		this.timeout = timeout;
		this.deviceUtils = deviceUtils;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		
		long time = System.currentTimeMillis();
		long endTime = time + timeout;
		long sleep = timeout / 10;
		
		while(time < endTime) {
			if(deviceUtils.isServiceRunning(context, serviceClass)) {
				return true;
			}
			else {
				try {
					Thread.sleep(sleep);
				} 
				catch (InterruptedException ignore) {
					return false;
				}
			}
			
			time = System.currentTimeMillis();
		}
		
		return false;
	}

	@Override
	protected void onPostExecute(Boolean ok) {
		if(ok) {
			listener.onStarted();
		}
		else if(error != null) {
			listener.onError(error);
		}
		else {
			listener.onError(new SocializeException("Timeout waiting for service start"));
		}
	}
}
