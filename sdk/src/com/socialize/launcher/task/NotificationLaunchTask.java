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
package com.socialize.launcher.task;

import android.content.Context;
import android.os.Bundle;
import com.socialize.error.SocializeException;
import com.socialize.launcher.LaunchTask;
import com.socialize.log.SocializeLogger;

import java.util.List;


/**
 * @author Jason Polites
 *
 */
public class NotificationLaunchTask implements LaunchTask {
	
	private List<LaunchTask> tasks;
	private SocializeLogger logger;

	/* (non-Javadoc)
	 * @see com.socialize.launcher.LaunchTask#execute(android.content.Context, android.os.Bundle)
	 */
	@Override
	public void execute(Context context, Bundle extras) throws SocializeException {
		if(tasks != null) {
			for (LaunchTask task : tasks) {
				try {
					task.execute(context, extras);
				}
				catch (SocializeException e) {
					if(logger != null) {
						logger.error("Error executing launcher task", e);
					}
					else {
						SocializeLogger.e(e.getMessage(), e);
					}
				}
			}
		}
	}
	
	public void setTasks(List<LaunchTask> tasks) {
		this.tasks = tasks;
	}


	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	
	
}
