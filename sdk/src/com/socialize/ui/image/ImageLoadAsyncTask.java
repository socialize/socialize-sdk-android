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
package com.socialize.ui.image;

import com.socialize.log.SocializeLogger;
import com.socialize.util.*;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Jason Polites
 *
 */
public class ImageLoadAsyncTask extends Thread {

	private Queue<ImageLoadRequest> requests;
	private Map<String, ImageLoadRequest> requestsInProcess;
	private boolean running = false;

	private SocializeLogger logger;
	private ImageUrlLoader imageUrlLoader;
	private DrawableCache cache;
	private Drawables drawables;
	private Base64Utils base64Utils;

	public ImageLoadAsyncTask() {
		super("ImageLoadAsyncTask");
	}

	@Override
	public void run() {
		if(requests != null) {

			while(running) {

				while (!requests.isEmpty() && running) {

					if(logger != null && logger.isDebugEnabled()) {
						logger.debug("ImageLoadAsyncTask has [" +
								requests.size() +
						"] images to load");
					}

					ImageLoadRequest request = requests.poll();
					String url = request.getUrl();

					if(!request.isCanceled()) {
				
						if(logger != null && logger.isDebugEnabled()) {
							logger.debug("ImageLoadAsyncTask found image to load at: " + url);
						}

						try {
							SafeBitmapDrawable drawable = null;

							if(cache != null) {
								drawable = cache.get(url);
							}

							if(drawable == null || drawable.isRecycled()) {
								
								switch (request.getType()) {
									case ENCODED:
										
										if(logger != null && logger.isDebugEnabled()) {
											logger.debug("ImageLoadAsyncTask image loading from encoded data for: " + url);
										}
										
										drawable = (SafeBitmapDrawable) drawables.getDrawableFromUrl(url, base64Utils.decode(request.getEncodedImageData()), request.getScaleWidth(), request.getScaleHeight());
										break;
	
									default:
										if(logger != null && logger.isDebugEnabled()) {
											logger.debug("ImageLoadAsyncTask image loading from remote url for: " + url);
										}
										
										drawable = loadImageFromUrl(url, request.getScaleWidth(), request.getScaleHeight());
										break;
								}
								
								if(drawable != null) {
									cache.put(url, (CacheableDrawable) drawable, false);
								}
							}
							
							
							int notified = request.notifyListeners(drawable);
							
							if(logger != null && logger.isDebugEnabled()) {
								logger.debug("Notified [" +
										notified +
										"] listeners for image load of url [" +
										url +
										"]");
							}	
							
						}
						catch (Exception e) {
							request.notifyListeners(e);
						}
						finally {
							requestsInProcess.remove(url);
						}
					}
					else {
						requestsInProcess.remove(url);
						if(logger != null && logger.isDebugEnabled()) {
							logger.debug("ImageLoadAsyncTask request canceled for " + request.getUrl());
						}
					}
				}

				synchronized(this) {
					if(running) {
						try {
							doWait();
						}
						catch (InterruptedException ignore) {}
					}
				}
			}
		}
	}
	
	protected CacheableDrawable loadImageFromUrl(String url, int width, int height) throws Exception {
		return imageUrlLoader.loadImageFromUrl(url, width, height);
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
		if(isRunning()) {
			
			String url = request.getUrl();
			
			ImageLoadRequest current = requestsInProcess.get(url);
			
			if(current != null && !current.isCanceled() && !current.isListenersNotified()) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Image with url [" +
							url +
					"] already being loaded. Adding listener to queue on current request [" +
					current.getUrl() +
					"]");
				}
				
				current.merge(request);
				notifyAll();
			}
			else {
				requests.add(request);
				requestsInProcess.put(url, request);
				notifyAll();		
			}
		}
		else {
			if(logger != null) {
				logger.warn("Image load task is not running.  Enqueue request ignored");
			}
		}
	}

	public void init() {
		requests = makeRequests();
		requestsInProcess = makePendingRequests();
	}
	
	protected void onStart() {
		init();
		running = true;
		setDaemon(true);
	}
	
	public void start() {
		onStart();
		super.start();
	}
	
	// So we can mock
	protected Queue<ImageLoadRequest> makeRequests() {
		return new ConcurrentLinkedQueue<ImageLoadRequest>();
	}
	
	// So we can mock
	protected Map<String, ImageLoadRequest> makePendingRequests() {
		return new ConcurrentHashMap<String, ImageLoadRequest>();
	}
	
	// So we can mock
	protected void doWait() throws InterruptedException {
		wait();
	}

	public synchronized void finish() {
		running = false;
		if(requests != null) {
			requests.clear();
		}
		if(requests != null) {
			requestsInProcess.clear();
		}
		
		notifyAll();
	}
	
	public boolean isRunning() {
		return running;
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
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setBase64Utils(Base64Utils base64Utils) {
		this.base64Utils = base64Utils;
	}

	public boolean isEmpty() {
		return requestsInProcess.isEmpty();
	}

	public boolean isLoading(String url) {
		return requestsInProcess.containsKey(url);
	}
}
