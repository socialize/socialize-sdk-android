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

import com.socialize.log.SocializeLogger;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;

/**
 * Loads images from a url.
 * @author Jason Polites
 */
public class ImageLoader {
	
	private Drawables drawables;
	private ImageLoadAsyncTask imageLoadAsyncTask;
	private SocializeLogger logger;
	
	/**
	 * Initializes the loader and starts an AsyncTask thread.
	 */
	public void init() {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("ImageLoader starting image load task");
		}
		imageLoadAsyncTask.start();
	}
	
	/**
	 * Destroys the loader and stops the AsyncTask thread.
	 */
	public void destroy() {
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("ImageLoader stopping image load task");
		}
		imageLoadAsyncTask.destroy();
	}
	
	/**
	 * Cancels the load of a previous request.
	 * @param url
	 */
	public void cancel(String url) {
		imageLoadAsyncTask.cancel(url);
	}
	
	
	/**
	 * Asynchrnously loads the image at the given url and calls the listener when it is loaded.
	 * @param url
	 * @param listener
	 */
	public void loadImage(final long id, final String url, final ImageLoadListener listener) {
		
		ImageLoadRequest request = makeRequest();
		request.setUrl(url);
		request.setItemId(id);
		
		// Look in cache
		CacheableDrawable drawable = drawables.getCache().get(url);
		
		if(drawable != null && !drawable.isRecycled()) {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("ImageLoader loading image from cache for " + url);
			}
			
			listener.onImageLoad(request, drawable, false);
		}
		else {
			
			if(logger != null && logger.isDebugEnabled() && drawable != null && drawable.isRecycled()) {
				logger.debug("ImageLoader image was recycled, reloading " + url);
			}

			request.addListener(new ImageLoadListener() {
				
				@Override
				public void onImageLoadFail(ImageLoadRequest request, Exception error) {
					listener.onImageLoadFail(request, error);
				}
				
				@Override
				public void onImageLoad(ImageLoadRequest request, SafeBitmapDrawable drawable, boolean async) {
					if(drawable instanceof CacheableDrawable) {
						if(!drawables.getCache().exists(url)) {
							if(logger != null && logger.isDebugEnabled()) {
								logger.debug("ImageLoader adding image [" +
										url +
										"] to cache with drawable " + drawable);
							}
							drawables.getCache().put(url, (CacheableDrawable) drawable, false);
						}
					}
					
					listener.onImageLoad(request, drawable, true);
				}
			});
			
			imageLoadAsyncTask.enqueue(request);
		}
	}

	public Drawables getDrawables() {
		return drawables;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	protected ImageLoadRequest makeRequest() {
		return new ImageLoadRequest();
	}

	public void setImageLoadAsyncTask(ImageLoadAsyncTask imageLoadAsyncTask) {
		this.imageLoadAsyncTask = imageLoadAsyncTask;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public boolean isLoading(String url) {
		return imageLoadAsyncTask.isLoading(url);
	}

	public boolean isEmpty() {
		return imageLoadAsyncTask.isEmpty();
	}
}
