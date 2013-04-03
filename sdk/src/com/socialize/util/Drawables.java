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

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import com.socialize.R;
import com.socialize.log.SocializeLogger;

import java.io.InputStream;

/**
 * Convenience class for getting drawables from raw images.
 * @author Jason Polites
 */
public class Drawables {

	private DisplayMetrics metrics = null;
	private ClassLoaderProvider classLoaderProvider;
	private DrawableCache cache;
	private BitmapUtils bitmapUtils;
	private SocializeLogger logger;
	private Resources resources;

	public Drawables() {
		super();
	}

	public void init(Activity context) {
		metrics = new DisplayMetrics();
		resources = context.getResources();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}
	
	public Drawable getDrawable(String name) {
		return getDrawable(name, false, false, true);
	}
	
	public Drawable getDrawable(String name, boolean tileX, boolean tileY, boolean eternal) {
		String extra = "";
		int hashIndex = name.indexOf('#');
		
		if(hashIndex >= 0) {
			extra = name.substring(hashIndex, name.length());
			name = name.substring(0, hashIndex);
		}
		
		int resourceId = getDrawableResourceId(name);
		
		if(resourceId > 0) {
			return getDrawable(resourceId, extra, tileX, tileY, eternal);
		}
		return null;
	}
	
	public Drawable getDrawable(int resourceId, String extra, boolean tileX, boolean tileY, boolean eternal) {
		return getDrawable(resourceId, extra, tileX, tileY, -1, -1, eternal);
	}
	
	public Drawable getDrawable(int resourceId, String extra) {
		return getDrawable(resourceId, extra, false, false, -1, -1, true);
	}
	
	public Drawable getDrawableFromUrl(String url, byte[] data, int scaleToWidth, int scaleToHeight) {
		
		CacheableDrawable drawable = cache.get(url);
		
		if(drawable == null) {
			Bitmap bitmap = bitmapUtils.getScaledBitmap ( data, scaleToWidth, scaleToHeight );
			drawable = createDrawable(bitmap, url);
			addToCache(url, drawable, false);
		}
		
		return drawable;
	}
	
	private final int getDrawableResourceId(String name) {
		
		if(name.endsWith(".png")) { // Legacy
			name = name.substring(0, name.lastIndexOf("."));
		}
		
		try {
			Class<?> drawable = R.drawable.class;
			Integer id = ReflectionUtils.getStaticField(name, drawable);
			if(id != null) {
				return id;
			}
			
		}
		catch (Exception e) {
			logger.error("Failed to find drawable with name " + name, e);
		}	
		
		return -1;
	}
	
	public Drawable getDrawable(int resourceId, String extra, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
		
		String key = String.valueOf(resourceId) + extra;
		
		CacheableDrawable drawable = cache.get(key);
		
		if(drawable != null && !drawable.isRecycled()) {
			return drawable;
		}

		Bitmap bmp = BitmapFactory.decodeResource(resources, resourceId);
		
		drawable = createDrawable(bmp, key);
		
		if(tileX) {
			drawable.setTileModeX(Shader.TileMode.REPEAT);
		}
		
		if(tileY) {
			drawable.setTileModeY(Shader.TileMode.REPEAT);
		}
		
		addToCache(key, drawable, eternal);
		
		return drawable;			
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public DrawableCache getCache() {
		return cache;
	}

	public void setCache(DrawableCache cache) {
		this.cache = cache;
	}
	
	public void setBitmapUtils(BitmapUtils bitmapUtils) {
		this.bitmapUtils = bitmapUtils;
	}
	
	public void setMetrics(DisplayMetrics metrics) {
		this.metrics = metrics;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	protected CacheableDrawable createDrawable(InputStream in, String name, boolean tileX, boolean tileY, int pixelsX, int pixelsY) {
		
		Bitmap bitmap = bitmapUtils.getScaledBitmap ( in, pixelsX, pixelsY, DisplayMetrics.DENSITY_DEFAULT);

		CacheableDrawable drawable = createDrawable(bitmap, name);
		
		if(tileX) {
			drawable.setTileModeX(Shader.TileMode.REPEAT);
		}
		
		if(tileY) {
			drawable.setTileModeY(Shader.TileMode.REPEAT);
		}
		
		return drawable;
	}
	
	protected CacheableDrawable createDrawable(Bitmap bitmap, String name) {
		return new CacheableDrawable(resources, bitmap, name);
	}
	
	protected void addToCache(String key, CacheableDrawable drawable, boolean eternal) {
		if(drawable != null) {
			cache.put(key, drawable, eternal);
		}
	}
	
	
}