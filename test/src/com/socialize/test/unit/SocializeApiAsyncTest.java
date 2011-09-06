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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeApi.RequestType;
import com.socialize.api.SocializeEntityResponse;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeResponseFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionConsumer;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeActivityTest;

public class SocializeApiAsyncTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> api;
	private SocializeProvider<SocializeObject> provider;
	private SocializeResponseFactory<SocializeObject> responseFactory;
	private SocializeEntityResponse<SocializeObject> mockEntityResponse;
	
	private CountDownLatch signal;
	private SocializeSession mockSession;
	private SocializeSessionConsumer mockSessionConsumer;
	private SocializeActionListener listener;
	private SocializeConfig config;

	@SuppressWarnings("unchecked")
	@UsesMocks({
			SocializeProvider.class, 
			SocializeSession.class, 
			SocializeResponseFactory.class, 
			SocializeEntityResponse.class,
			SocializeConfig.class,
			Properties.class,
			SocializeSessionConsumer.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		signal = new CountDownLatch(1); 
		provider = AndroidMock.createMock(SocializeProvider.class);
		responseFactory = AndroidMock.createMock(SocializeResponseFactory.class);
		mockEntityResponse = AndroidMock.createMock(SocializeEntityResponse.class);
		
		mockSessionConsumer = AndroidMock.createMock(SocializeSessionConsumer.class);
		
		config = AndroidMock.createMock(SocializeConfig.class);
		
		api = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
		
		api.setConfig(config);
		
		mockSession = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.replay(mockSession);
		
		listener = new SocializeActionListener() {

			@Override
			public void onResult(RequestType type, SocializeResponse response) {
				System.out.println("Api listener onResult fired");
				signal.countDown(); 
			}

			@Override
			public void onError(SocializeException error) {
				System.out.println("Api listener onError fired");
				signal.countDown();
			}
		};
	}
	
	public void testDialogDismissWorksOnError() throws Throwable {
		AndroidMock.expect(provider.authenticate("/authenticate/", "test_key", "test_secret", "test_uuid")).andThrow(new SocializeException("TEST ERROR IGNORE ME!"));
		AndroidMock.replay(provider);
		
		final ProgressDialog authProgress = ProgressDialog.show(getActivity(), "Authenticating", "Please wait...");
		

		final SocializeAuthListener alistener = new SocializeAuthListener() {
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				authProgress.dismiss();
				signal.countDown(); 
				fail();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				authProgress.dismiss();
				signal.countDown();
				fail();
			}


			@Override
			public void onError(SocializeException error) {
				authProgress.dismiss();
				signal.countDown();
			}
		};
		

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.authenticateAsync("test_key", "test_secret", "test_uuid", alistener, mockSessionConsumer);
			} 
		});
		
		signal.await(30, TimeUnit.SECONDS); 
	}
	
	public void testApiAsyncCallsAuthenticateOnProvider() throws Throwable {
		
		AndroidMock.expect(provider.authenticate("/authenticate/", "test_key", "test_secret", "test_uuid")).andReturn(mockSession);
		
		mockSessionConsumer.setSession(mockSession);
		
		AndroidMock.replay(provider);
		AndroidMock.replay(mockSessionConsumer);
		
		final SocializeAuthListener alistener = new SocializeAuthListener() {
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				System.out.println("Api listener onResult fired");
				signal.countDown(); 
				
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				System.out.println("Api listener onAuthFail fired");
				signal.countDown();
			}


			@Override
			public void onError(SocializeException error) {
				System.out.println("Api listener onError fired");
				signal.countDown();
			}
		};
		

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.authenticateAsync("test_key", "test_secret", "test_uuid", alistener, mockSessionConsumer);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
		AndroidMock.verify(mockSessionConsumer);
	}
	
	public void testApiAsyncCallsGetOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String id = null;
		
		AndroidMock.expect(provider.get(mockSession, endpoint, id)).andReturn(new SocializeObject());
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.getAsync(mockSession, endpoint, id, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testApiAsyncGetCallsAddOnResponse() throws Throwable {

		final String endpoint = "foobar";
		final String id = null;
		
		SocializeObject obj = new SocializeObject();

		api.setResponseFactory(responseFactory);
		
		AndroidMock.expect(responseFactory.newEntityResponse()).andReturn(mockEntityResponse);
		AndroidMock.expect(provider.get(mockSession, endpoint, id)).andReturn(obj);
		
		mockEntityResponse.addResult(obj);
		
		AndroidMock.replay(responseFactory);
		AndroidMock.replay(provider);
		AndroidMock.replay(mockEntityResponse);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.getAsync(mockSession, endpoint, id, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(responseFactory);
		AndroidMock.verify(provider);
		AndroidMock.verify(mockEntityResponse);
	}
	
	public void testApiAsyncListCallsSetResultsOnResponse() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String[] ids = null;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		api.setResponseFactory(responseFactory);
		
		AndroidMock.expect(responseFactory.newEntityResponse()).andReturn(mockEntityResponse);
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS)).andReturn(returned);
		mockEntityResponse.setResults(returned);
		
		AndroidMock.replay(responseFactory);
		AndroidMock.replay(provider);
		AndroidMock.replay(mockEntityResponse);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.listAsync(mockSession, endpoint, key, ids, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(responseFactory);
		AndroidMock.verify(provider);
		AndroidMock.verify(mockEntityResponse);
	}
	
	public void testApiAsyncListCallsSetResultsOnResponsePaginated() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String[] ids = null;
		
		final int start = 0, end = 10;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		api.setResponseFactory(responseFactory);
		
		AndroidMock.expect(responseFactory.newEntityResponse()).andReturn(mockEntityResponse);
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, start, end)).andReturn(returned);
		mockEntityResponse.setResults(returned);
		
		AndroidMock.replay(responseFactory);
		AndroidMock.replay(provider);
		AndroidMock.replay(mockEntityResponse);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.listAsync(mockSession, endpoint, key, ids, start, end, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(responseFactory);
		AndroidMock.verify(provider);
		AndroidMock.verify(mockEntityResponse);
	}
	
	public void testApiAsyncCallsListOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String ids[] = null;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS)).andReturn(returned);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.listAsync(mockSession, endpoint, key, ids, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	
	public void testApiAsyncCallsListOnProviderPaginated() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String ids[] = null;
		
		final int start = 0, end = 10;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, start, end)).andReturn(returned);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.listAsync(mockSession, endpoint, key, ids, start, end, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testApiAsyncCallsPutOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = new SocializeObject();
		
		AndroidMock.expect(provider.put(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.putAsync(mockSession, endpoint, object, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testApiAsyncCallsPostOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = new SocializeObject();
		
		AndroidMock.expect(provider.post(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.postAsync(mockSession, endpoint, object, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	
	public void testApiAsyncCallsPostOnProviderWithList() throws Throwable {

		final String endpoint = "foobar";
		final List<SocializeObject> objects = new ArrayList<SocializeObject>();
		
		AndroidMock.expect(provider.post(mockSession, endpoint, objects)).andReturn(null);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.postAsync(mockSession, endpoint, objects, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testApiAsyncCallsPutOnProviderWithList() throws Throwable {

		final String endpoint = "foobar";
		final List<SocializeObject> objects = new ArrayList<SocializeObject>();
		
		AndroidMock.expect(provider.put(mockSession, endpoint, objects)).andReturn(null);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				api.putAsync(mockSession, endpoint, objects, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
}
