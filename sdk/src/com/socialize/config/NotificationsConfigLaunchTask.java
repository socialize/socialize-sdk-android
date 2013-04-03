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
package com.socialize.config;

import android.content.Context;
import android.os.Bundle;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchTask;

/**
 * @author Jason Polites
 *
 */
public class NotificationsConfigLaunchTask implements LaunchTask {

	private SocializeConfig config;
	
	/* (non-Javadoc)
	 * @see com.socialize.launcher.SocializeLaunchTask#execute(android.content.Context)
	 */
	@Override
	public void execute(Context context, Bundle extras) throws SocializeException {
		config.setEntityLoaderCheckEnabled(false);
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
}
