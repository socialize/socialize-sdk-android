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
package com.socialize.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/**
 * Convenience class for getting drawables form raw images
 * 
 * @author Jason Polites
 */
public class Drawables {

	DisplayMetrics metrics = null;
	private ClassLoaderProvider classLoaderProvider;
	
	public Drawables(Activity context) {
		super();
		metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}
	
	public Drawable getDrawable(String name, boolean tileX, boolean tileY) {
		return getDrawable(name, metrics.densityDpi, tileX, tileY);
	}
	
	public Drawable getDrawable(String name) {
		return getDrawable(name, false, false);
	}
	
	public Drawable getDrawable(byte[] data) {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		return new BitmapDrawable(bin);
	}

	public Drawable getDrawable(String name, int density, boolean tileX, boolean tileY) {

		String densityPath = "mdpi";

		if (density == DisplayMetrics.DENSITY_HIGH) {
			densityPath = "hdpi";
		}
		else if (density == DisplayMetrics.DENSITY_LOW) {
			densityPath = "ldpi";
		}

		InputStream in = null;
			
		try {
			
			ClassLoader loader = null;
			
			if(classLoaderProvider != null) {
				loader =  classLoaderProvider.getClassLoader();
			}
			else {
				loader = Drawables.class.getClassLoader();
			}
			
			in = loader.getResourceAsStream("res/drawable/" + densityPath + "/" + name);
			
			if(in == null) {
				// try default
				in = loader.getResourceAsStream("res/drawable/" + name);
			}
			
			if(in != null) {
				return createDrawable(in, name, tileX, tileY);
			}
			
			return null;
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
		}
	}
	
	protected Drawable createDrawable(InputStream in, String name, boolean tileX, boolean tileY) {
		
		BitmapDrawable drawable = new BitmapDrawable(in);
		
		if(tileX || tileY) {
		
			TileMode x = Shader.TileMode.CLAMP;
			TileMode y = Shader.TileMode.CLAMP;
			
			if(tileX) {
				x = Shader.TileMode.REPEAT;
			}
			
			if(tileY) {
				y = Shader.TileMode.REPEAT;
			}
			
			drawable.setTileModeXY( x, y );
		}
		
		return drawable;
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}
}
