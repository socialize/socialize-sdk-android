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
package com.socialize.image;

import java.util.List;
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
	private Map<Integer, ImageLoadRequest> requestsInProcess;
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
				int size = requests.size();
				
				while (size > 0) {
					
					if(logger != null && logger.isInfoEnabled()) {
						logger.info("ImageLoadAsyncTask has [" +
								size +
								"] images to load");
					}
					
					ImageLoadRequest request = requests.poll();
					
					String url = request.getUrl();
					
					if(!request.isCanceled()) {
						
							if(logger != null && logger.isInfoEnabled()) {
								logger.info("ImageLoadAsyncTask found image to load at: " + url);
							}
							
							List<ImageLoadListener> listeners = request.getListeners();
							
							if(listeners != null) {
								// Get the image
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
									
									for (ImageLoadListener listener : listeners) {
										
										if(request.isCanceled()) {
											break;
										}
										
										if(logger != null && logger.isInfoEnabled()) {
											logger.info("ImageLoadAsyncTask notifying listener for image: " + url);
										}
										
										listener.onImageLoad(drawable);
									}
								}
								catch (Exception e) {
									for (ImageLoadListener listener : listeners) {
										listener.onImageLoadFail(e);
									}
								}
								finally {
									requestsInProcess.remove(url);
								}
						}
					}
					else {
						if(logger != null && logger.isInfoEnabled()) {
							logger.info("ImageLoadAsyncTask request canceled for id " + request.getId());
						}
					}

					size = requests.size();
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
	
	public void cancel(int id) {
		ImageLoadRequest request = requestsInProcess.get(id);
		if(request != null) {
			request.setCanceled(true);
		}
	}
	
	public synchronized void enqueue(ImageLoadRequest request) {
		if(running) {
			String url = request.getUrl();
					
			if(logger != null && logger.isInfoEnabled()) {
				logger.info("Image with url [" +
						url +
						"] is NOT being loaded.. adding listener to queue");
			}
			
			requests.add(request);
			requestsInProcess.put(request.getId(), request);
			notifyAll();	
		}
		else {
			if(logger != null) {
				logger.warn("Image load task is not running.  Enqeueu request ignored");
			}
		}
		
	}
	
	public void start() {
		requests = new ConcurrentLinkedQueue<ImageLoadRequest>();
		requestsInProcess = new ConcurrentHashMap<Integer, ImageLoadRequest>();
		running = true;
		execute((Void) null);
	}
	
	public synchronized void stop() {
		running = false;
		notifyAll();
		requests.clear();
		requestsInProcess.clear();
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
	
	
}
