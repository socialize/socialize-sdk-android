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
package com.socialize.ui.image;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.os.AsyncTask;

import com.socialize.log.SocializeLogger;
import com.socialize.util.DrawableCache;
import com.socialize.util.SafeBitmapDrawable;

/**
 * @author Jason Polites
 *
 */
public class ImageLoadAsyncTask extends AsyncTask<Void, Void, Void> {

	private Queue<ImageLoadRequest> requests;
	private Map<String, ImageLoadRequest> requestsInProcess;
	private boolean running = false;

	private SocializeLogger logger;
	private ImageUrlLoader imageUrlLoader;
	private DrawableCache cache;

	public ImageLoadAsyncTask() {
		super();
	}

	@Override
	protected Void doInBackground(Void... args) {
		if(requests != null) {

			while(running) {

				while (!requests.isEmpty() && running) {

					if(logger != null && logger.isInfoEnabled()) {
						logger.info("ImageLoadAsyncTask has [" +
								requests.size() +
						"] images to load");
					}

					ImageLoadRequest request = requests.poll();

					if(!request.isCanceled()) {

						String url = request.getUrl();

						if(logger != null && logger.isInfoEnabled()) {
							logger.info("ImageLoadAsyncTask found image to load at: " + url);
						}

						try {
							SafeBitmapDrawable drawable = null;

							if(cache != null) {
								drawable = cache.get(url);
							}

							if(drawable == null) {
								drawable = loadImage(url);
								if(logger != null && logger.isInfoEnabled()) {
									logger.info("ImageLoadAsyncTask image loaded from: " + url);
								}
							}
							
							requestsInProcess.remove(url);
							request.notifyListeners(drawable);
						}
						catch (Exception e) {
							request.notifyListeners(e);
						}
					}
					else {
						if(logger != null && logger.isInfoEnabled()) {
							logger.info("ImageLoadAsyncTask request canceled for " + request.getUrl());
						}
					}
				}

				synchronized(this) {
					if(running) {
						try {
							wait();
						}
						catch (InterruptedException ignore) {}
					}
				}
			}
		}
		return null;
	}

	protected SafeBitmapDrawable loadImage(String url) throws Exception {
		return imageUrlLoader.loadImageFromUrl(url);
	}

	public void cancel(String url) {
		if(requestsInProcess != null) {
			ImageLoadRequest request = requestsInProcess.get(url);
			if(request != null) {
				request.setCanceled(true);
			}
		}
	}

	public synchronized void enqueue(ImageLoadRequest request) {
		if(running) {
			
			String url = request.getUrl();
			
			ImageLoadRequest current = requestsInProcess.get(url);
			
			if(current != null && !current.isCanceled()) {
				if(logger != null && logger.isInfoEnabled()) {
					logger.info("Image with url [" +
							url +
					"] already being loaded. Adding listener to queue on current request [" +
					current.getUrl() +
					"]");
				}
				
				current.merge(request);
			}
			else {
				requests.add(request);
				requestsInProcess.put(url, request);
				notifyAll();		
			}
		}
		else {
			if(logger != null) {
				logger.warn("Image load task is not running.  Enqeueu request ignored");
			}
		}

	}

	public void start() {
		requests = new ConcurrentLinkedQueue<ImageLoadRequest>();
		requestsInProcess = new ConcurrentHashMap<String, ImageLoadRequest>();
		running = true;
		execute((Void) null);
	}

	public synchronized void stop() {
		running = false;
		if(requests != null) {
			requests.clear();
		}
		if(requests != null) {
			requestsInProcess.clear();
		}
		
		notifyAll();
		cancel(true);
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setImageUrlLoader(ImageUrlLoader imageUrlLoader) {
		this.imageUrlLoader = imageUrlLoader;
	}

	public void setCache(DrawableCache cache) {
		this.cache = cache;
	}
	
	public void destroy() {
		stop();
	}

	public boolean isEmpty() {
		return requestsInProcess.isEmpty();
	}

	public boolean isLoading(String url) {
		return requestsInProcess.containsKey(url);
	}
}
