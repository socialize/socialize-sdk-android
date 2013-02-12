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
import com.socialize.util.CacheableDrawable;
import com.socialize.util.Drawables;

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
		imageLoadAsyncTask.finish();
	}
	
	/**
	 * Cancels the load of a previous request.
	 * @param url
	 */
	public void cancel(String url) {
		imageLoadAsyncTask.cancel(url);
	}
	
	/**
	 * Loads an image based on Base64 encoded bytes.
	 * @param id The id to give the request
	 * @param name The name to give the image in cache.
	 * @param encodedData The encoded image data.
	 * @param listener A listener to handle the callback.
	 */
	public void loadImageByData(final String name, final String encodedData, int width, int height, final ImageLoadListener listener) {		
		ImageLoadRequest request = makeRequest();
		request.setUrl(name);
		request.setEncodedImageData(encodedData);
		request.setType(ImageLoadType.ENCODED);
		loadImage(request, listener);
	}
	
	public void loadImageByUrl(final String url, final ImageLoadListener listener) {
		loadImageByUrl(url, -1, -1, listener);
	}
	
	/**
	 * Asynchronously loads the image at the given url and calls the listener when it is loaded.
	 * @param id The id to give the request
	 * @param url The url to be loaded
	 * @param listener A listener to handle the callback.
	 */
	public void loadImageByUrl(final String url, int width, int height, final ImageLoadListener listener) {
		ImageLoadRequest request = makeRequest();
		request.setUrl(url);
		request.setType(ImageLoadType.URL);
		request.setScaleWidth(width);
		request.setScaleHeight(height);
		loadImage(request, listener);
	}
	
	public void loadImage(final ImageLoadRequest request, final ImageLoadListener listener) {
		// Look in cache
		final String url = request.getUrl();
		
		CacheableDrawable drawable = drawables.getCache().get(url);
		
		if(drawable != null && !drawable.isRecycled()) {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("ImageLoader loading image from cache for " + url);
			}
			
			if(listener != null) {
				listener.onImageLoad(request, drawable);
			}
		}
		else {
			
			if(logger != null && logger.isDebugEnabled()) {
				
				if(drawable != null && drawable.isRecycled()) {
					logger.debug("ImageLoader image was recycled, reloading " + url);
				}
				else {
					logger.debug("ImageLoader enqueuing request for image " + url);
				}
				
			}
			
			if(listener != null) {
				request.addListener(listener);
			}
			
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
