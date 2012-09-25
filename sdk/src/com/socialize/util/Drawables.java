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

import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import com.socialize.log.SocializeLogger;

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
	
	public Drawables() {
		super();
	}

	public void init(Activity context) {
		metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}
	
	public Drawable getDrawable(String name, boolean tileX, boolean tileY, boolean eternal) {
		return getDrawable(name, metrics.densityDpi, tileX, tileY, -1, -1, eternal);
	}
	
	public Drawable getDrawable(String name) {
		return getDrawable(name, -1, -1, true);
	}
	
	public Drawable getDrawable(String name, boolean eternal) {
		return getDrawable(name, false, false, -1, -1, eternal);
	}
	
	public Drawable getDrawable(String name, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
		return getDrawable(name, metrics.densityDpi, tileX, tileY, scaleToWidth, scaleToHeight, eternal);
	}
	
	public Drawable getDrawable(String name, int scaleToWidth, int scaleToHeight) {
		return getDrawable(name, scaleToWidth, scaleToHeight, false);
	}
	
	public Drawable getDrawable(String name, int scaleToWidth, int scaleToHeight, boolean eternal) {
		return getDrawable(name, false, false, scaleToWidth, scaleToHeight, eternal);
	}
	
	public Drawable getDrawable(String key, byte[] data) {
		return getDrawable(key, data, -1, -1);
	}
	
	public Drawable getDrawable(String key, byte[] data, int scaleToWidth, int scaleToHeight) {
		
		CacheableDrawable drawable = cache.get(key);
		
		if(drawable == null) {
			Bitmap bitmap = bitmapUtils.getScaledBitmap ( data, scaleToWidth, scaleToHeight );
			drawable = createDrawable(bitmap, key);
			addToCache(key, drawable, false);
		}
		
		return drawable;
	}

	public Drawable getDrawable(String name, int density, boolean tileX, boolean tileY, boolean eternal) {
		return getDrawable(name, density, tileX, tileY, -1, -1, eternal);
	}
	
	public Drawable getDrawable(String name, int density, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
		
		String densityPath = getPath(name, density);
		String commonPath = getPath(name);
		
		CacheableDrawable drawable = cache.get(densityPath + name);
		
		if(drawable == null) {
			// try default
			drawable = cache.get(commonPath + name);
		}
		
		if(drawable != null && !drawable.isRecycled()) {
			return drawable;
		}

		InputStream in = null;
			
		try {
			
			ClassLoader loader = null;
			
			if(classLoaderProvider != null) {
				loader = classLoaderProvider.getClassLoader();
			}
			else {
				loader = Drawables.class.getClassLoader();
			}
			
			String path = densityPath;
			
			in = loader.getResourceAsStream(path);
			
			if(in == null) {
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("No drawable found in path [" +
							path +
							"].  Trying common path");
				}
				
				// try default
				path = commonPath;
				in = loader.getResourceAsStream(path);
			}
			
			if(in != null) {
				drawable = createDrawable(in, path + name, tileX, tileY, scaleToWidth, scaleToHeight);
				addToCache(path + name, drawable, eternal);
			}
			else {
				if(logger != null && logger.isWarnEnabled()) {
					logger.warn("No drawable found in path [" +
							path +
							"], returning null");
				}
			}
			
			return drawable;
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
					SocializeLogger.w(e.getMessage(), e);
				}
			}
		}
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

	protected String getPath(String name) {
		int indexOf = name.indexOf('#');
		if(indexOf >= 0) {
			name = name.substring(0, indexOf);
		}
		return "res/drawable/" + name;
	}
	
	protected String getPath(String name, int density) {
		String densityPath = null;
		
		if (density > DisplayMetrics.DENSITY_LOW) {
			if (density > DisplayMetrics.DENSITY_MEDIUM) {
				if (density > DisplayMetrics.DENSITY_HIGH) {
					densityPath = "xhdpi";
				}
				else {
					densityPath = "hdpi";
				}
			}
			else {
				densityPath = "mdpi";
			}
		}
		else {
			densityPath = "ldpi";
		}

		int indexOf = name.indexOf('#');
		
		if(indexOf >= 0) {
			name = name.substring(0, indexOf);
		}
		
		return "res/drawable/" + densityPath + "/" + name;
	}
	
	protected CacheableDrawable createDrawable(InputStream in, String name, boolean tileX, boolean tileY, int pixelsX, int pixelsY) {
		
		Bitmap bitmap = bitmapUtils.getScaledBitmap ( in , pixelsX, pixelsY , DisplayMetrics.DENSITY_DEFAULT);

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
		return new CacheableDrawable(bitmap, name);
	}
	
	protected void addToCache(String key, CacheableDrawable drawable, boolean eternal) {
		if(drawable != null) {
			cache.put(key, drawable, eternal);
		}
	}
	
	
}