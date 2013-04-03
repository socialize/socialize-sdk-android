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

import com.socialize.util.SafeBitmapDrawable;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Represents a request for a single image.
 * @author Jason Polites
 *
 */
public class ImageLoadRequest {

	private String encodedImageData;
	private String url;
	private ConcurrentLinkedQueue<ImageLoadListener> listeners;
	private boolean canceled;
	private boolean listenersNotified = false;
	private ImageLoadType type = ImageLoadType.URL;
	
	private int scaleWidth = -1;
	private int scaleHeight = -1;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isCanceled() {
		return canceled;
	}

	public boolean isListenersNotified() {
		return listenersNotified;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
	
	public int notifyListeners(SafeBitmapDrawable drawable) {
		int notified = 0;
		listenersNotified = true;
		if(listeners != null) {
			while(!listeners.isEmpty()) {
				if(isCanceled()) {
					listeners.clear();
					break;
				}
				ImageLoadListener listener = listeners.poll();
				listener.onImageLoad(this, drawable);
				notified++;
			}
		}
		return notified;
	}
	
	public void notifyListeners(Exception error) {
		listenersNotified = true;
		if(listeners != null) {
			while(!listeners.isEmpty()) {
				if(isCanceled()) {
					listeners.clear();
					break;
				}
				ImageLoadListener listener = listeners.poll();
				listener.onImageLoadFail(this, error);
			}
		}
	}
	
	public void merge(ImageLoadRequest request) {
		addListeners(request.listeners);
	}
	
	public synchronized void addListeners(Collection<ImageLoadListener> listener) {
		if(listener != null && listener.size() > 0) {
			if(listeners == null) {
				listeners = new ConcurrentLinkedQueue<ImageLoadListener>();
			}
			
			listeners.addAll(listener);
		}
	}
	
	public synchronized void addListener(ImageLoadListener listener) {
		if(listeners == null) {
			listeners = new ConcurrentLinkedQueue<ImageLoadListener>();
		}
		
		listeners.add(listener);
	}

	public String getEncodedImageData() {
		return encodedImageData;
	}
	
	public void setEncodedImageData(String encodedImageData) {
		this.encodedImageData = encodedImageData;
	}
	
	public ImageLoadType getType() {
		return type;
	}
	
	public void setType(ImageLoadType type) {
		this.type = type;
	}
	
	public int getScaleWidth() {
		return scaleWidth;
	}
	
	public void setScaleWidth(int scaleWidth) {
		this.scaleWidth = scaleWidth;
	}
	
	public int getScaleHeight() {
		return scaleHeight;
	}

	public void setScaleHeight(int scaleHeight) {
		this.scaleHeight = scaleHeight;
	}
	
	public Queue<ImageLoadListener> getListeners() {
		return listeners;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageLoadRequest other = (ImageLoadRequest) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;
		return true;
	}
}
