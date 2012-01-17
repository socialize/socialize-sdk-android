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
package com.socialize.test.ui.image;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeUnitTest;
import com.socialize.test.ui.PublicCacheableDrawable;
import com.socialize.ui.image.ImageLoadAsyncTask;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.DrawableCache;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	ImageLoadRequest.class, 
	CacheableDrawable.class,
	Drawables.class, 
	DrawableCache.class, 
	PublicCacheableDrawable.class,
	ImageLoadListener.class})
public class ImageLoaderTest extends SocializeUnitTest {

	public void test_loadImageInCache() {
		
		final long id = 69L;
		final String url = "foobar";
		
		final Drawables drawables = AndroidMock.createMock(Drawables.class);
		final ImageLoadRequest request = AndroidMock.createMock(ImageLoadRequest.class);
		final DrawableCache cache = AndroidMock.createMock(DrawableCache.class);
		final PublicCacheableDrawable drawable = AndroidMock.createMock(PublicCacheableDrawable.class);
		final ImageLoadListener listener = AndroidMock.createMock(ImageLoadListener.class);
		
		AndroidMock.expect(drawables.getCache()).andReturn(cache);
		AndroidMock.expect(cache.get(url)).andReturn(drawable);
		AndroidMock.expect(drawable.isRecycled()).andReturn(false);
		
		listener.onImageLoad(request, drawable, false);
		
        request.setUrl(url);
        request.setItemId(id);
        
        AndroidMock.replay(drawables, cache, drawable, listener, request);
		
		ImageLoader loader = new ImageLoader() {
			@Override
			protected ImageLoadRequest makeRequest() {
				return request;
			}
		};
		
		loader.setDrawables(drawables);
		loader.loadImage(id, url, listener);
		
		AndroidMock.verify(drawables, cache, drawable, listener, request);
		
	}
	
	@UsesMocks ({ImageLoadAsyncTask.class})
	public void test_loadImageNotInCache() {
		
		final long id = 69L;
		final String url = "foobar";
		
		final Drawables drawables = AndroidMock.createMock(Drawables.class);
		final DrawableCache cache = AndroidMock.createMock(DrawableCache.class);
		final PublicCacheableDrawable drawable = AndroidMock.createMock(PublicCacheableDrawable.class);
		final ImageLoadListener listener = AndroidMock.createMock(ImageLoadListener.class);
		final ImageLoadAsyncTask imageLoadAsyncTask = AndroidMock.createMock(ImageLoadAsyncTask.class);
		
		final ImageLoadRequest request = new ImageLoadRequest() {
			@Override
			public synchronized void addListener(ImageLoadListener listener) {
				addResult(0, listener);
			}
		};
				
		AndroidMock.expect(drawables.getCache()).andReturn(cache).anyTimes();
		AndroidMock.expect(cache.get(url)).andReturn(null);
		AndroidMock.expect(cache.exists(url)).andReturn(false);
		AndroidMock.expect(cache.put(url, drawable, false)).andReturn(true);
		
		imageLoadAsyncTask.enqueue(request);

		listener.onImageLoad(request, drawable, true);
		
        AndroidMock.replay(drawables, cache, drawable, listener);
		
		ImageLoader loader = new ImageLoader() {
			@Override
			protected ImageLoadRequest makeRequest() {
				return request;
			}
		};
		
		loader.setDrawables(drawables);
		loader.setImageLoadAsyncTask(imageLoadAsyncTask);
		
		loader.loadImage(id, url, listener);
		
		ImageLoadListener nested = getResult(0);
		
		assertNotNull(nested);
		
		// Call the nested listener
		nested.onImageLoad(request, drawable, false);
		
		AndroidMock.verify(drawables, cache, drawable, listener);
	}	
	
}
