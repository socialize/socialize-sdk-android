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
package com.socialize.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import com.socialize.cache.ICacheable;

/**
 * @author Jason Polites
 *
 */
public class CacheableDrawable extends SafeBitmapDrawable implements ICacheable<String> {

	private String key;
	
	// So we can easily mock
	protected CacheableDrawable() {
		super();
	}

	public CacheableDrawable(Bitmap bitmap, String key) {
		super(bitmap);
		this.key = key;
	}

	public CacheableDrawable(Resources res, Bitmap bitmap, String key) {
		super(res, bitmap);
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see com.socialize.cache.ICacheable#getSizeInBytes(android.content.Context)
	 */
	@Override
	public long getSizeInBytes() {
		// No size restrictions??
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.socialize.cache.ICacheable#onRemove(android.content.Context, boolean)
	 */
	@Override
	public boolean onRemove(boolean destroy) {
		if(destroy && !isRecycled()) {
			recycle();
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.socialize.cache.ICacheable#onPut(android.content.Context, java.lang.Comparable)
	 */
	@Override
	public boolean onPut(String key) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.socialize.cache.ICacheable#onGet(android.content.Context)
	 */
	@Override
	public boolean onGet() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.socialize.cache.ICacheable#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return key;
	}
}