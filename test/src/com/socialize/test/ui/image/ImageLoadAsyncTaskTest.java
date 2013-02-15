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

import java.util.Map;
import java.util.Queue;
import android.graphics.Bitmap;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.test.SocializeActivityTest;
import com.socialize.ui.image.ImageLoadAsyncTask;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoadType;
import com.socialize.ui.image.ImageUrlLoader;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.DrawableCache;
import com.socialize.util.SafeBitmapDrawable;

/**
 * @author Jason Polites
 *
 */
@UsesMocks ({
	ImageUrlLoader.class,
	DrawableCache.class,
	Queue.class,
	Map.class,
	CacheableDrawable.class,
	SafeBitmapDrawable.class,
	ImageLoadRequest.class
})
public class ImageLoadAsyncTaskTest extends SocializeActivityTest {


	public void testImageLoadAsyncTaskImageInCache() {
		runTest(true);
	}
	
	public void testImageLoadAsyncTaskImageNotInCache() {
		runTest(false);
	}
	
	@SuppressWarnings("unchecked")
	protected void runTest(boolean imageInCache) {
		// Can't mock, so just create a dummy one
		final Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		
		final Queue<ImageLoadRequest> requests = AndroidMock.createMock(Queue.class);
		final Map<String, ImageLoadRequest> pendingRequests = AndroidMock.createMock(Map.class);
		final ImageLoadRequest request = AndroidMock.createMock(ImageLoadRequest.class);
		final DrawableCache cache = AndroidMock.createMock(DrawableCache.class);
		final CacheableDrawable drawable = AndroidMock.createMock(CacheableDrawable.class, bmp, "foo");
		final String url = "foobar";
		
		PublicImageLoadAsyncTask task = new PublicImageLoadAsyncTask() {

			@Override
			public Queue<ImageLoadRequest> makeRequests() {
				return requests;
			}

			@Override
			public Map<String, ImageLoadRequest> makePendingRequests() {
				return pendingRequests;
			}
			
			@Override
			public CacheableDrawable loadImageFromUrl(String url, int width, int height) throws Exception {
				addResult(0, url); // to verify we were called.
				return drawable;
			}

			@Override
			public void doWait() throws InterruptedException {
				addResult(1, "wait_called"); // To verify we were called
				// Force stop
				finish();
			}
		};
		
		// Run once only
		AndroidMock.expect(requests.isEmpty()).andReturn(false).once();
		AndroidMock.expect(requests.isEmpty()).andReturn(true).once();
		AndroidMock.expect(requests.poll()).andReturn(request).once();
		AndroidMock.expect(request.isCanceled()).andReturn(false).once();
		AndroidMock.expect(request.getUrl()).andReturn(url).once();
		
		if(imageInCache) {
			AndroidMock.expect(cache.get(url)).andReturn(drawable).once();
		}
		else {
			AndroidMock.expect(request.getScaleWidth()).andReturn(-1).once();
			AndroidMock.expect(request.getScaleHeight()).andReturn(-1).once();
			AndroidMock.expect(request.getType()).andReturn(ImageLoadType.URL).once();
			AndroidMock.expect(cache.get(url)).andReturn(null).once();
			AndroidMock.expect(cache.put(url, drawable, false)).andReturn(true).once();
		}
		
		AndroidMock.expect(pendingRequests.remove(url)).andReturn(request);
		AndroidMock.expect(request.notifyListeners(drawable)).andReturn(1);
		
		requests.clear();
		pendingRequests.clear();
		
		AndroidMock.replay(request);
		AndroidMock.replay(requests);
		AndroidMock.replay(pendingRequests);
		AndroidMock.replay(cache);
		
		task.setCache(cache);
		task.onStart();
		task.run();
		
		AndroidMock.verify(request);
		AndroidMock.verify(requests);
		AndroidMock.verify(pendingRequests);
		AndroidMock.verify(cache);
		
		String urlAfter = getResult(0);
		String wait_called = getResult(1);
		
		if(!imageInCache) assertNotNull(urlAfter);
		assertNotNull(wait_called);
		
		if(!imageInCache) assertEquals(url, urlAfter);
		assertEquals("wait_called", wait_called);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({ImageLoadRequest.class, Map.class, Queue.class})
	public void test_enqueue_withCurrentUnCanceledRequest() {
		
		final String url = "foobar";
		
		final Map<String, ImageLoadRequest> requestsInProcess = AndroidMock.createMock(Map.class);
		final Queue<ImageLoadRequest> requests =  AndroidMock.createMock(Queue.class);
		final ImageLoadRequest request = AndroidMock.createMock(ImageLoadRequest.class);
		final ImageLoadRequest current = AndroidMock.createMock(ImageLoadRequest.class);
		
		AndroidMock.expect(request.getUrl()).andReturn(url);
		AndroidMock.expect(requestsInProcess.get(url)).andReturn(current);
		AndroidMock.expect(current.isCanceled()).andReturn(false);
		AndroidMock.expect(current.isListenersNotified()).andReturn(false);
		
		current.merge(request);
		
		AndroidMock.replay(requestsInProcess, request, current);
		
		ImageLoadAsyncTask task = new ImageLoadAsyncTask() {
			@Override
			protected Queue<ImageLoadRequest> makeRequests() {
				return requests;
			}
			@Override
			protected Map<String, ImageLoadRequest> makePendingRequests() {
				return requestsInProcess;
			}
			@Override
			public boolean isRunning() {
				return true;
			}
		};
		
		task.init(); 
		task.enqueue(request);
		
		AndroidMock.verify(requestsInProcess, request, current);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({ImageLoadRequest.class, Map.class, Queue.class})
	public void test_enqueue_withCurrentCanceledRequest() {
		final String url = "foobar";
		
		final Map<String, ImageLoadRequest> requestsInProcess = AndroidMock.createMock(Map.class);
		final Queue<ImageLoadRequest> requests =  AndroidMock.createMock(Queue.class);
		final ImageLoadRequest request = AndroidMock.createMock(ImageLoadRequest.class);
		final ImageLoadRequest current = AndroidMock.createMock(ImageLoadRequest.class);
		
		AndroidMock.expect(request.getUrl()).andReturn(url);
		AndroidMock.expect(requestsInProcess.get(url)).andReturn(current);
		AndroidMock.expect(current.isCanceled()).andReturn(true);
		
		AndroidMock.expect(requests.add(request)).andReturn(true);
		AndroidMock.expect(requestsInProcess.put(url, request)).andReturn(request);
		
		AndroidMock.replay(requestsInProcess, request, current);
		
		ImageLoadAsyncTask task = new ImageLoadAsyncTask() {
			@Override
			protected Queue<ImageLoadRequest> makeRequests() {
				return requests;
			}
			@Override
			protected Map<String, ImageLoadRequest> makePendingRequests() {
				return requestsInProcess;
			}
			@Override
			public boolean isRunning() {
				return true;
			}
		};
		
		task.init(); 
		task.enqueue(request);
		
		AndroidMock.verify(requestsInProcess, request, current);
	}	
	

	@SuppressWarnings("unchecked")
	@UsesMocks ({ImageLoadRequest.class, Map.class, Queue.class})
	public void test_cancel() {
		final String url = "foobar";
		
		final Map<String, ImageLoadRequest> requestsInProcess = AndroidMock.createMock(Map.class);
		final Queue<ImageLoadRequest> requests =  AndroidMock.createMock(Queue.class);
		final ImageLoadRequest request = AndroidMock.createMock(ImageLoadRequest.class);
		
		AndroidMock.expect(requestsInProcess.get(url)).andReturn(request);
		request.setCanceled(true);
		
		AndroidMock.replay(requestsInProcess, request);
		
		ImageLoadAsyncTask task = new ImageLoadAsyncTask() {
			@Override
			protected Queue<ImageLoadRequest> makeRequests() {
				return requests;
			}
			@Override
			protected Map<String, ImageLoadRequest> makePendingRequests() {
				return requestsInProcess;
			}
			@Override
			public boolean isRunning() {
				return true;
			}
		};
		
		task.init(); 
		task.cancel(url);
		
		AndroidMock.verify(requestsInProcess, request);
	}	
	
	
	public class PublicImageLoadAsyncTask extends ImageLoadAsyncTask {
		
		@Override
		public Queue<ImageLoadRequest> makeRequests() {
			return super.makeRequests();
		}

		@Override
		public Map<String, ImageLoadRequest> makePendingRequests() {
			return super.makePendingRequests();
		}

	
		@Override
		public void doWait() throws InterruptedException {
			super.doWait();
		}

		@Override
		public CacheableDrawable loadImageFromUrl(String url, int width, int height) throws Exception {
			return super.loadImageFromUrl(url, width, height);
		}

		@Override
		public void onStart() {
			super.onStart();
		}
	}
}
