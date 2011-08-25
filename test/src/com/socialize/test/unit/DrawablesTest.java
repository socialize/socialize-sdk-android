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

import android.util.DisplayMetrics;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeActivityTest;
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
		
		final InputStream in = AndroidMock.createNiceMock(InputStream.class);
		final CacheableDrawable drawable = AndroidMock.createMock(CacheableDrawable.class, "foobar");
		
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
	}
}
