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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeApi.RequestType;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.AbstractSocializeListener;
import com.socialize.listener.SocializeActionListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.HttpUtils;

public class SocializeListenerTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> api;
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
		api = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
	}

	@UsesMocks({SocializeActionListener.class})
	public void testOnResultCalledOnAsyncGetSuccess() throws Throwable {
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final SocializeActionListener listener = AndroidMock.createMock(SocializeActionListener.class);
		
		// Must use matcher here 
		// http://weirdfellow.wordpress.com/2010/07/15/2-matchers-expected-1-recorded/
		listener.onResult(AndroidMock.eq(RequestType.GET), (SocializeResponse) AndroidMock.anyObject());
		
		AndroidMock.replay(listener);
		
		final String id = "foobar";
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				api.getAsync(session, endpoint, id, listener);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		AndroidMock.verify(listener);
	}
	
	@UsesMocks ({SocializeException.class, SocializeActionListener.class})
	public void testOnErrorCalledOnFail() throws Throwable {
		CountDownLatch signal = new CountDownLatch(1); 
		
		HttpUtils utils = new HttpUtils();
		SocializeApiError dummyError = new SocializeApiError(utils, 0);
		
		final SocializeActionListener listener = AndroidMock.createMock(SocializeActionListener.class);
		
		AndroidMock.makeThreadSafe(provider, true);
		AndroidMock.makeThreadSafe(session, true);
		AndroidMock.makeThreadSafe(listener, true);
		
		listener.onError(dummyError);
		
		final String id = "foobar";
		
		AndroidMock.expect(provider.get(session, endpoint,id)).andThrow(dummyError);
		
		AndroidMock.replay(provider);
		AndroidMock.replay(listener);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				api.getAsync(session, endpoint, id, listener);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		AndroidMock.verify(provider);
		AndroidMock.verify(listener);
	}
	
	public void testListenerOnGetCalledOnGET() throws Throwable {
		
		final SocializeActionListener listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeException error) {
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
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final String id = "foobar";
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				api.getAsync(session, endpoint, id, listener);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testListenerOnListCalledOnLIST() throws Throwable {
		
		final SocializeActionListener listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeException error) {
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
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final String key = "foo";
		final String[] ids = {"bar"};
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				api.listAsync(session, endpoint, key, ids, listener);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testListenerOnUpdateCalledOnPOST() throws Throwable {
		final SocializeActionListener listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeException error) {
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
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final SocializeObject obj = new SocializeObject();
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				api.postAsync(session, endpoint, obj, listener);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testListenerOnCreateCalledOnPUT() throws Throwable {
		final SocializeActionListener listener = new AbstractSocializeListener<SocializeObject>() {

			@Override
			public void onError(SocializeException error) {
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
		
		CountDownLatch signal = new CountDownLatch(1); 
		
		final SocializeObject obj = new SocializeObject();
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				api.putAsync(session, endpoint, obj, listener);
			}
		});
		
		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);
		
		Boolean result = getResult();
		
		assertNotNull(result);
		assertTrue(result);
	}
	
}
