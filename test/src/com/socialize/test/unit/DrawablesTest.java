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
package com.socialize.test.unit;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.BitmapBuilder;
import com.socialize.util.BitmapUtils;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.ClassLoaderProvider;
import com.socialize.util.DrawableCache;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	ClassLoaderProvider.class,
	ClassLoader.class,
	InputStream.class,
	CacheableDrawable.class,
	DrawableCache.class})
public class DrawablesTest extends SocializeActivityTest {
	
	public void testGetDrawable() throws IOException {
		
		final String drawable_name = "foobar";

		ClassLoaderProvider provider = AndroidMock.createMock(ClassLoaderProvider.class);
		DrawableCache cache = AndroidMock.createMock(DrawableCache.class, getActivity());
		ClassLoader loader = AndroidMock.createMock(ClassLoader.class);
		
		// Can't mock, so just create a dummy one
		final Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		
		final InputStream in = AndroidMock.createNiceMock(InputStream.class);
		final CacheableDrawable drawable = AndroidMock.createMock(CacheableDrawable.class, bitmap, "foobar");
		
		AndroidMock.expect(cache.get("res/drawable/ldpi/" + drawable_name)).andReturn(null);
		AndroidMock.expect(cache.get("res/drawable/mdpi/" + drawable_name)).andReturn(null);
		AndroidMock.expect(cache.get("res/drawable/hdpi/" + drawable_name)).andReturn(null);
		AndroidMock.expect(cache.put("res/drawable/ldpi/" + drawable_name, drawable, false)).andReturn(true);
		AndroidMock.expect(cache.put("res/drawable/mdpi/" + drawable_name, drawable, false)).andReturn(true);
		AndroidMock.expect(cache.put("res/drawable/hdpi/" + drawable_name, drawable, false)).andReturn(true);
		AndroidMock.expect(cache.get("res/drawable/" + drawable_name)).andReturn(null).times(3);
		
		AndroidMock.expect(provider.getClassLoader()).andReturn(loader).times(3);
		AndroidMock.expect(loader.getResourceAsStream("res/drawable/ldpi/" + drawable_name)).andReturn(in);
		AndroidMock.expect(loader.getResourceAsStream("res/drawable/mdpi/" + drawable_name)).andReturn(in);
		AndroidMock.expect(loader.getResourceAsStream("res/drawable/hdpi/" + drawable_name)).andReturn(in);
		
		in.close();
		in.close();
		in.close();
		
		AndroidMock.replay(cache);
		AndroidMock.replay(provider);
		AndroidMock.replay(loader);
		AndroidMock.replay(in);
		
		Drawables drawables = new Drawables(getActivity()) {
			@Override
			protected CacheableDrawable createDrawable(InputStream stream, String name, boolean tileX, boolean tileY, int pixelsX, int pixelsY) {
				assertSame(in, stream);
				assertFalse(tileX);
				assertFalse(tileY);
				return drawable;
			}
		};
		
		drawables.setClassLoaderProvider(provider);
		drawables.setCache(cache);
		
		assertSame(drawable, drawables.getDrawable(drawable_name, DisplayMetrics.DENSITY_LOW, false, false, false));
		assertSame(drawable, drawables.getDrawable(drawable_name, DisplayMetrics.DENSITY_MEDIUM, false, false, false));
		assertSame(drawable, drawables.getDrawable(drawable_name, DisplayMetrics.DENSITY_HIGH, false, false, false));
		
		AndroidMock.verify(cache);
		AndroidMock.verify(provider);
		AndroidMock.verify(loader);
		AndroidMock.verify(in);
		
		bitmap.recycle();
	}
	
	public void testCreateDrawableRepeatXY() {
		doTestCreateDrawableRepeatXY(true, true);
	}
	
	public void testCreateDrawableRepeatXOnly() {
		doTestCreateDrawableRepeatXY(true, false);
	}
	
	public void testCreateDrawableRepeatYOnly() {
		doTestCreateDrawableRepeatXY(false, true);
	}
	
	public void testCreateDrawableNoRepeat() {
		doTestCreateDrawableRepeatXY(false, false);
	}
	
	@UsesMocks ({CacheableDrawable.class, BitmapUtils.class, BitmapBuilder.class, InputStream.class})
	protected void doTestCreateDrawableRepeatXY(boolean repeatX, boolean repeatY) {
		
		final String key = "foobar";
		final int pixelsX = 10;
		final int pixelsY = 20;
		
		// Can't mock, so just create a dummy one
		final Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		
		try {
			final CacheableDrawable drawable = AndroidMock.createMock(CacheableDrawable.class, bitmap, key);
			BitmapBuilder builder = AndroidMock.createMock(BitmapBuilder.class);
			BitmapUtils bitmapUtils = AndroidMock.createMock(BitmapUtils.class, builder);
			InputStream in = AndroidMock.createMock(InputStream.class);
			
			AndroidMock.expect(bitmapUtils.getScaledBitmap ( in , pixelsX, pixelsY )).andReturn(bitmap);
			
			if(repeatX) {
				drawable.setTileModeX(Shader.TileMode.REPEAT);
			}
			
			if(repeatY) {
				drawable.setTileModeY(Shader.TileMode.REPEAT);
			}
			
			PublicDrawables drawables = new PublicDrawables() {
				@Override
				protected CacheableDrawable createDrawable(Bitmap bmp, String name) {
					assertSame(bitmap, bmp);
					assertEquals(key, name);
					return drawable;
				}
			};
			
			drawables.setBitmapUtils(bitmapUtils);
			
			AndroidMock.replay(bitmapUtils);
			AndroidMock.replay(drawable);
			
			assertSame(drawable, drawables.createDrawable(in, key, repeatX, repeatY, pixelsX, pixelsY));
			
			AndroidMock.verify(bitmapUtils);
			AndroidMock.verify(drawable);
		}
		finally {
			bitmap.recycle();
		}
	}

	
	@UsesMocks ({DrawableCache.class, CacheableDrawable.class, BitmapUtils.class, BitmapBuilder.class})
	public void testGetDrawableFromBytes() {
		
		final String key = "foobar";
		final byte[] data = {};
		final int scaleToWidth = 10;
		final int scaleToHeight = 20;
		
		// Can't mock, so just create a dummy one
		final Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		
		try {
			
			assertNotNull(bitmap);
			
			DrawableCache cache = AndroidMock.createMock(DrawableCache.class, getActivity());
			BitmapBuilder builder = AndroidMock.createMock(BitmapBuilder.class);
			BitmapUtils bitmapUtils = AndroidMock.createMock(BitmapUtils.class, builder);
			
			final CacheableDrawable drawable = AndroidMock.createMock(CacheableDrawable.class, bitmap, key);
			
			AndroidMock.expect(bitmapUtils.getScaledBitmap ( data, scaleToWidth, scaleToHeight )).andReturn(bitmap);
			AndroidMock.expect(cache.put(key, drawable, false)).andReturn(true);
			AndroidMock.expect(cache.get(key)).andReturn(null);
			
			PublicDrawables drawables = new PublicDrawables() {
				@Override
				protected CacheableDrawable createDrawable(Bitmap bmp, String name) {
					assertSame(bitmap, bmp);
					assertEquals(key, name);
					return drawable;
				}
			};
			
			drawables.setBitmapUtils(bitmapUtils);
			drawables.setCache(cache);
			
			AndroidMock.replay(bitmapUtils);
			AndroidMock.replay(cache);
			
			drawables.getDrawable(key, data, scaleToWidth, scaleToHeight);
			
			AndroidMock.verify(bitmapUtils);
			AndroidMock.verify(cache);
			
		}
		finally {
			bitmap.recycle();
		}
	}
	
	@UsesMocks(DisplayMetrics.class)
	public void testCallFlow0() {
		
		String name = "foobar";
		boolean tileX = true;
		boolean tileY = false;
		boolean eternal = true;
		
		DisplayMetrics metrics = new DisplayMetrics();
		metrics.densityDpi = 69;
		
		PublicDrawables drawables = new PublicDrawables() {
			@Override
			public Drawable getDrawable(String name, int density, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
				addResult(name);
				addResult(density);
				addResult(tileX);
				addResult(tileY);
				addResult(scaleToWidth);
				addResult(scaleToHeight);
				addResult(eternal);
				return null;
			}
		};
		
		drawables.setMetrics(metrics);
		drawables.getDrawable(name, tileX, tileY, eternal);
		
		// reverse order for asserts
		assertEquals(eternal, getNextResult());
		assertEquals(-1, getNextResult());
		assertEquals(-1, getNextResult());
		assertEquals(tileY, getNextResult());
		assertEquals(tileX, getNextResult());
		assertEquals(metrics.densityDpi, getNextResult());
		assertEquals(name, getNextResult());
	}
	
	public void testCallFlow1() {
		
		String name = "foobar";
		
		PublicDrawables drawables = new PublicDrawables() {
			@Override
			public Drawable getDrawable(String name, int scaleToWidth, int scaleToHeight, boolean eternal) {
				addResult(name);
				addResult(scaleToWidth);
				addResult(scaleToHeight);
				addResult(eternal);
				return null;
			}
		};
		
		drawables.getDrawable(name);
		
		// reverse order for asserts
		assertEquals(false, getNextResult());
		assertEquals(-1, getNextResult());
		assertEquals(-1, getNextResult());
		assertEquals(name, getNextResult());
	}
	
	public void testCallFlow2() {
		
		String name = "foobar";
		boolean eternal = true;
		
		PublicDrawables drawables = new PublicDrawables() {
			@Override
			public Drawable getDrawable(String name, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
				addResult(name);
				addResult(tileX);
				addResult(tileY);
				addResult(scaleToWidth);
				addResult(scaleToHeight);
				addResult(eternal);
				return null;
			}
		};
		
		drawables.getDrawable(name, eternal);
		
		// reverse order for asserts
		assertEquals(eternal, getNextResult());
		assertEquals(-1, getNextResult());
		assertEquals(-1, getNextResult());
		assertEquals(false, getNextResult());
		assertEquals(false, getNextResult());
		assertEquals(name, getNextResult());
	}
	
	
	public void testCallFlow3() {
		
		String name = "foobar";
		boolean eternal = true;
		
		int scaleToHeight = 20;
		int scaleToWidth = 40;
		boolean tileY = true;
		boolean tileX = false;
		
		DisplayMetrics metrics = new DisplayMetrics();
		metrics.densityDpi = 69;
		
		PublicDrawables drawables = new PublicDrawables() {
			@Override
			public Drawable getDrawable(String name, int density, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
				addResult(name);
				addResult(density);
				addResult(tileX);
				addResult(tileY);
				addResult(scaleToWidth);
				addResult(scaleToHeight);
				addResult(eternal);
				return null;
			}
		};
		
		drawables.setMetrics(metrics);
		
		drawables.getDrawable(name, tileX, tileY, scaleToWidth, scaleToHeight, eternal);
		
		// reverse order for asserts
		assertEquals(eternal, getNextResult());
		assertEquals(scaleToHeight, getNextResult());
		assertEquals(scaleToWidth, getNextResult());
		assertEquals(tileY, getNextResult());
		assertEquals(tileX, getNextResult());
		assertEquals(metrics.densityDpi, getNextResult());
		assertEquals(name, getNextResult());
	}
	
	public void testCallFlow4() {
		
		String name = "foobar";
		boolean eternal = false;
		
		int scaleToHeight = 20;
		int scaleToWidth = 40;
		
		PublicDrawables drawables = new PublicDrawables() {
			@Override
			public Drawable getDrawable(String name, int scaleToWidth, int scaleToHeight, boolean eternal) {
				addResult(name);
				addResult(scaleToWidth);
				addResult(scaleToHeight);
				addResult(eternal);
				return null;
			}
		};
		
		drawables.getDrawable(name, scaleToWidth, scaleToHeight);
		
		// reverse order for asserts
		assertEquals(eternal, getNextResult());
		assertEquals(scaleToHeight, getNextResult());
		assertEquals(scaleToWidth, getNextResult());
		assertEquals(name, getNextResult());
	}	
	
	public void testCallFlow5() {
		
		String name = "foobar";
		boolean eternal = false;
		
		int scaleToHeight = 20;
		int scaleToWidth = 40;
		
		PublicDrawables drawables = new PublicDrawables() {
			@Override
			public Drawable getDrawable(String name, boolean tileX, boolean tileY, int scaleToWidth, int scaleToHeight, boolean eternal) {
				addResult(name);
				addResult(tileX);
				addResult(tileY);
				addResult(scaleToWidth);
				addResult(scaleToHeight);
				addResult(eternal);
				return null;
			}
		};
		
		drawables.getDrawable(name, scaleToWidth, scaleToHeight, eternal);
		
		// reverse order for asserts
		assertEquals(eternal, getNextResult());
		assertEquals(scaleToHeight, getNextResult());
		assertEquals(scaleToWidth, getNextResult());
		assertEquals(false, getNextResult());
		assertEquals(false, getNextResult());
		assertEquals(name, getNextResult());
	}		
	
	
	class PublicDrawables extends Drawables {
		@Override
		public CacheableDrawable createDrawable(InputStream in, String name, boolean tileX, boolean tileY, int pixelsX, int pixelsY) {
			return super.createDrawable(in, name, tileX, tileY, pixelsX, pixelsY);
		}
	}
	
	
}
