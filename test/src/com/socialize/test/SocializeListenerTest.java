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
package com.socialize.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeService;
import com.socialize.api.SocializeService.RequestType;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeApiError;
import com.socialize.listener.AbstractSocializeListener;
import com.socialize.listener.SocializeListener;
import com.socialize.provider.SocializeProvider;

public class SocializeListenerTest extends SocializeActivityTest {

	private SocializeService<SocializeObject, SocializeProvider<SocializeObject>> service;
	private SocializeProvider<SocializeObject> provider;
//	private SocializeResponse response;
	private SocializeSession session;
	
	private final String endpoint = "foobar";
	
	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeProvider.class, SocializeSession.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createNiceMock(SocializeProvider.class);
		session = AndroidMock.createNiceMock(SocializeSession.class);
		service = new SocializeService<SocializeObject, SocializeProvider<SocializeObject>>(provider);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeListener.class})
	public void testOnResultCalledOnAsyncGetSuccess() throws Throwable {
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		SocializeListener<SocializeObject> listener = AndroidMock.createMock(SocializeListener.class);
		service.setListener(listener);
		
		// Must use matcher here 
		// http://weirdfellow.wordpress.com/2010/07/15/2-matchers-expected-1-recorded/
		listener.onResult(AndroidMock.eq(RequestType.GET), (SocializeResponse) AndroidMock.anyObject());
		
		AndroidMock.replay(listener);
		
		final int[] ids = {0};
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				service.getAsync(session, endpoint, ids);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		AndroidMock.verify(listener);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({SocializeApiError.class, SocializeListener.class})
	public void testOnErrorCalledOnFail() throws Throwable {
		CountDownLatch signal = new CountDownLatch(1); 
		
		SocializeApiError dummyError = new SocializeApiError(0);
		
		SocializeListener<SocializeObject> listener = AndroidMock.createMock(SocializeListener.class);
		service.setListener(listener);
		
		AndroidMock.makeThreadSafe(provider, true);
		AndroidMock.makeThreadSafe(session, true);
		AndroidMock.makeThreadSafe(listener, true);
		
		listener.onError(dummyError);
		
		final int[] ids = {0};
		
		AndroidMock.expect(provider.get(session, endpoint,ids)).andThrow(dummyError);
		
		AndroidMock.replay(provider);
		AndroidMock.replay(listener);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				service.getAsync(session, endpoint, ids);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		AndroidMock.verify(provider);
		AndroidMock.verify(listener);
	}
	
	public void testListenerOnGetCalledOnGET() throws Throwable {
		
		SocializeListener<SocializeObject> listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeApiError error) {
				fail();
			}

			@Override
			public void onGet(SocializeObject entity) {
				addResult(true);
			}

			@Override
			public void onList(List<SocializeObject> entities) {
				fail();
			}

			@Override
			public void onUpdate(SocializeObject entity) {
				fail();
			}

			@Override
			public void onCreate(SocializeObject entity) {
				fail();
			}
		};
		
		service.setListener(listener);
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final int[] ids = {0};
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				service.getAsync(session, endpoint, ids);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testListenerOnListCalledOnLIST() throws Throwable {
		
		SocializeListener<SocializeObject> listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeApiError error) {
				fail();
			}

			@Override
			public void onGet(SocializeObject entity) {
				fail();
			}

			@Override
			public void onList(List<SocializeObject> entities) {
				addResult(true);
			}

			@Override
			public void onUpdate(SocializeObject entity) {
				fail();
			}

			@Override
			public void onCreate(SocializeObject entity) {
				fail();
			}
		};
		
		service.setListener(listener);
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final String key = "foobar";
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				service.listAsync(session, endpoint, key);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testListenerOnUpdateCalledOnPOST() throws Throwable {
		SocializeListener<SocializeObject> listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeApiError error) {
				fail();
			}

			@Override
			public void onGet(SocializeObject entity) {
				fail();
			}

			@Override
			public void onList(List<SocializeObject> entities) {
				fail();
			}

			@Override
			public void onUpdate(SocializeObject entity) {
				addResult(true);
			}

			@Override
			public void onCreate(SocializeObject entity) {
				fail();
			}
		};
		
		service.setListener(listener);
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final SocializeObject obj = new SocializeObject();
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				service.postAsync(session, endpoint, obj);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testListenerOnCreateCalledOnPUT() throws Throwable {
		SocializeListener<SocializeObject> listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeApiError error) {
				fail();
			}

			@Override
			public void onGet(SocializeObject entity) {
				fail();
			}

			@Override
			public void onList(List<SocializeObject> entities) {
				fail();
			}

			@Override
			public void onUpdate(SocializeObject entity) {
				fail();
			}

			@Override
			public void onCreate(SocializeObject entity) {
				addResult(true);
			}
		};
		
		service.setListener(listener);
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final SocializeObject obj = new SocializeObject();
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				service.putAsync(session, endpoint, obj);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
}
