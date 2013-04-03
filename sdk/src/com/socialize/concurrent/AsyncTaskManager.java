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
package com.socialize.concurrent;

import android.os.AsyncTask;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Jason Polites
 *
 */
public class AsyncTaskManager {

	private static boolean managed = false;
	private static AtomicInteger ids = new AtomicInteger(0);
	private static Map<Integer, AsyncTask<?, ?, ?>> tasks = new ConcurrentHashMap<Integer, AsyncTask<?,?,?>>();
	
	public static int register(AsyncTask<?, ?, ?> task) {
		int id = ids.getAndIncrement();
		tasks.put(id, task);
		return id;
	}
	
	public static boolean isManaged() {
		return managed;
	}
	
	public static void setManaged(boolean managed) {
		AsyncTaskManager.managed = managed;
	}

	public static void unregister(int id) {
		tasks.remove(id);
	}
	
	public static synchronized boolean terminateAll(long timeout, TimeUnit unit) {
		
		Set<Entry<Integer, AsyncTask<?, ?, ?>>> entrySet = tasks.entrySet();
		
		if(!entrySet.isEmpty()) {
			int count = entrySet.size();
			
			int cancelled = 0;
			
			if(count > 0) {
				
				long incrementalTime = timeout / count;
				
				for (Entry<Integer, AsyncTask<?, ?, ?>> entry : entrySet) {
					AsyncTask<?, ?, ?> task = entry.getValue();
					
					try {
						task.get(incrementalTime, unit);
						cancelled++;
					}
					catch (Exception e) {
						if(task.cancel(true)) {
							cancelled++;
						}
					}
				}
				
				tasks.clear();
			}
			
			return cancelled == count;
		}
		else {
			return true;
		}
	}
}
