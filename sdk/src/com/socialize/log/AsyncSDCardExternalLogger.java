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
package com.socialize.log;

import android.content.Context;
import com.socialize.log.SocializeLogger.LogLevel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author Jason Polites
 *
 */
public class AsyncSDCardExternalLogger extends SDCardExternalLogger implements Runnable {
	
	private Thread logger = null;
	private Queue<LogJob> jobs = new ConcurrentLinkedQueue<LogJob>();
	private boolean running = false;
	
	final class LogJob {
		LogLevel level;
		long time;
		String tag;
		String message;
	}

	@Override
	public void run() {
		
		running = true;
		
		while(running) {
			LogJob job = jobs.poll();
			while(job != null) {
				super.log(job.level, job.time, job.tag, job.message);
				job = jobs.poll();
			}
			
			synchronized (this) {
				if(running) {
					try {
						wait();
					}
					catch (InterruptedException ignore) {}
				}
			}
		}
	}
	
	@Override
	public void log(LogLevel level, long time, String tag, String message) {
		LogJob job = new LogJob();
		job.level = level;
		job.time = time;
		job.tag = tag;
		job.message = message;
		jobs.add(job);
		
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void init(Context context) {
		destroy();
		super.init(context);
		logger = new Thread(this, "AsyncSDCardExternalLogger");
		logger.setDaemon(true);
		logger.start();
	}

	@Override
	public void destroy() {
		synchronized (this) {
			running = false;
			notify();
		}		
		
		super.destroy();
	}
}
