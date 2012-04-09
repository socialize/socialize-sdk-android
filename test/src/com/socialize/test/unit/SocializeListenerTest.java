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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeApi.RequestType;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeSession;
import com.socialize.entity.ActionError;
import com.socialize.entity.ListResult;
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
	// private SocializeResponse response;
	private SocializeSession session;

	private final String endpoint = "foobar";

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeProvider.class, SocializeSession.class })
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createNiceMock(SocializeProvider.class);
		session = AndroidMock.createNiceMock(SocializeSession.class);
		api = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
	}

	@UsesMocks({ SocializeActionListener.class })
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

	@UsesMocks({ SocializeException.class, SocializeActionListener.class })
	public void testOnErrorCalledOnFail() throws Throwable {
		CountDownLatch signal = new CountDownLatch(1);

		HttpUtils utils = new HttpUtils();
		SocializeApiError dummyError = new SocializeApiError(utils, 0, "foobar");

		final SocializeActionListener listener = AndroidMock.createMock(SocializeActionListener.class);

		AndroidMock.makeThreadSafe(provider, true);
		AndroidMock.makeThreadSafe(session, true);
		AndroidMock.makeThreadSafe(listener, true);

		listener.onError(dummyError);

		final String id = "foobar";

		AndroidMock.expect(provider.get(session, endpoint, id)).andThrow(dummyError);

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

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeException.class, SocializeActionListener.class, ListResult.class })
	public void testOnErrorCalledOnPOSTFailWithNullItems() throws Throwable {
		final String errorMessage = "foobar error";
		final String id = "foobar";

		CountDownLatch signal = new CountDownLatch(1);

		final SocializeActionListener listener = AndroidMock.createMock(SocializeActionListener.class);
		final SocializeObject object = new SocializeObject();

		ListResult<SocializeObject> result = (ListResult<SocializeObject>) AndroidMock.createMock(ListResult.class);

		ActionError error = new ActionError(errorMessage);
		List<ActionError> errors = new ArrayList<ActionError>(1);
		errors.add(error);

		AndroidMock.makeThreadSafe(provider, true);
		AndroidMock.makeThreadSafe(session, true);
		AndroidMock.makeThreadSafe(listener, true);

		listener.onError((SocializeException) AndroidMock.anyObject());

		AndroidMock.expect(provider.post(session, id, object, true)).andReturn(result);

		AndroidMock.expect(result.getItems()).andReturn(null);
		AndroidMock.expect(result.getErrors()).andReturn(errors);

		AndroidMock.replay(provider);
		AndroidMock.replay(listener);
		AndroidMock.replay(result);

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.postAsync(session, endpoint, object, listener);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		AndroidMock.verify(provider);
		AndroidMock.verify(listener);
		AndroidMock.verify(result);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeException.class, SocializeActionListener.class, ListResult.class })
	public void testOnErrorCalledOnPOSTFailWithEmptyItems() throws Throwable {
		final String errorMessage = "foobar error";
		final String id = "foobar";

		CountDownLatch signal = new CountDownLatch(1);

		final SocializeActionListener listener = AndroidMock.createMock(SocializeActionListener.class);
		final SocializeObject object = new SocializeObject();

		ListResult<SocializeObject> result = (ListResult<SocializeObject>) AndroidMock.createMock(ListResult.class);

		ActionError error = new ActionError(errorMessage);
		List<ActionError> errors = new ArrayList<ActionError>(1);
		errors.add(error);

		List<SocializeObject> items = new ArrayList<SocializeObject>();

		AndroidMock.makeThreadSafe(provider, true);
		AndroidMock.makeThreadSafe(session, true);
		AndroidMock.makeThreadSafe(listener, true);

		listener.onError((SocializeException) AndroidMock.anyObject());

		AndroidMock.expect(provider.post(session, id, object, true)).andReturn(result);

		AndroidMock.expect(result.getItems()).andReturn(items);
		AndroidMock.expect(result.getErrors()).andReturn(errors);

		AndroidMock.replay(provider);
		AndroidMock.replay(listener);
		AndroidMock.replay(result);

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.postAsync(session, endpoint, object, listener);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		AndroidMock.verify(provider);
		AndroidMock.verify(listener);
		AndroidMock.verify(result);
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
			public void onList(ListResult<SocializeObject> entities) {
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

			@Override
			public void onDelete() {
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

		Boolean result = getNextResult();

		assertNotNull(result);
		assertTrue(result);
	}

	public void testListenerOnDeleteCalledOnDELETE() throws Throwable {

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
			public void onList(ListResult<SocializeObject> entities) {
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

			@Override
			public void onDelete() {
				addResult(true);
			}
		};

		CountDownLatch signal = new CountDownLatch(1);

		final String id = "foobar";

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.deleteAsync(session, endpoint, id, listener);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		Boolean result = getNextResult();

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
			public void onList(ListResult<SocializeObject> result) {
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

			@Override
			public void onDelete() {
				fail();
			}
		};

		CountDownLatch signal = new CountDownLatch(1);

		final String key = "foo";
		final String[] ids = { "bar" };

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.listAsync(session, endpoint, key, listener, ids);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		Boolean result = getNextResult();

		assertNotNull(result);
		assertTrue(result);
	}

	public void testListenerOnListCalledOnLISTPaginated() throws Throwable {

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
			public void onList(ListResult<SocializeObject> result) {
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

			@Override
			public void onDelete() {
				fail();
			}
		};

		CountDownLatch signal = new CountDownLatch(1);

		final String key = "foo";
		final String[] ids = { "bar" };
		final int start = 0, end = 10;

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.listAsync(session, key, key, start, end, listener, ids);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		Boolean result = getNextResult();

		assertNotNull(result);
		assertTrue(result);
	}

	public void testListenerOnCreateCalledOnPOST() throws Throwable {

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
			public void onList(ListResult<SocializeObject> result) {
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

			@Override
			public void onDelete() {
				fail();
			}
		};

		CountDownLatch signal = new CountDownLatch(1);

		final SocializeObject obj = new SocializeObject();

		List<SocializeObject> items = new ArrayList<SocializeObject>(1);
		items.add(obj);

		ListResult<SocializeObject> listResult = new ListResult<SocializeObject>();
		listResult.setItems(items);

		AndroidMock.expect(provider.post(session, endpoint, obj, true)).andReturn(listResult);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.postAsync(session, endpoint, obj, listener);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		AndroidMock.verify(provider);

		Boolean result = getNextResult();

		assertNotNull(result);
		assertTrue(result);
	}

	public void testListenerOnUpdateCalledOnPUT() throws Throwable {
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
			public void onList(ListResult<SocializeObject> result) {
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

			@Override
			public void onDelete() {
				fail();
			}
		};

		CountDownLatch signal = new CountDownLatch(1);

		final SocializeObject obj = new SocializeObject();

		List<SocializeObject> items = new ArrayList<SocializeObject>(1);
		items.add(obj);

		ListResult<SocializeObject> listResult = new ListResult<SocializeObject>();
		listResult.setItems(items);

		AndroidMock.expect(provider.put(session, endpoint, obj)).andReturn(listResult);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() {
			public void run() {
				api.putAsync(session, endpoint, obj, listener);
			}
		});

		// Just wait for async process to finish
		signal.await(500, TimeUnit.MILLISECONDS);

		AndroidMock.verify(provider);

		Boolean result = getNextResult();

		assertNotNull(result);
		assertTrue(result);
	}

}
