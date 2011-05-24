package com.socialize.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeApi.RequestType;
import com.socialize.api.SocializeEntityResponse;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeResponseFactory;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeListener;
import com.socialize.provider.SocializeProvider;

public class SocializeApiAsyncTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> service;
	private SocializeProvider<SocializeObject> provider;
	private SocializeResponseFactory<SocializeObject> responseFactory;
	private SocializeEntityResponse<SocializeObject> mockEntityResponse;
	
	private CountDownLatch signal;
	private SocializeSession mockSession;
	private SocializeListener<SocializeObject> listener;
	

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeProvider.class, SocializeSession.class, SocializeResponseFactory.class, SocializeEntityResponse.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		signal = new CountDownLatch(1); 
		provider = AndroidMock.createMock(SocializeProvider.class);
		responseFactory = AndroidMock.createMock(SocializeResponseFactory.class);
		mockEntityResponse = AndroidMock.createMock(SocializeEntityResponse.class);
		
		service = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
		
		mockSession = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.replay(mockSession);
		
		listener = new SocializeListener<SocializeObject>() {

			@Override
			public void onResult(RequestType type, SocializeResponse response) {
				System.out.println("Service listener onResult fired");
				signal.countDown(); 
			}

			@Override
			public void onError(SocializeException error) {
				System.out.println("Service listener onError fired");
				signal.countDown();
			}
		};
		
	}
	
	public void testServiceAsyncCallsAuthenticateOnProvider() throws Throwable {

		AndroidMock.expect(provider.authenticate("test_key", "test_secret", "test_uuid")).andReturn(mockSession);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.authenticateAsync("test_key", "test_secret", "test_uuid", listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testServiceAsyncCallsGetOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final int[] ids = null;
		
		AndroidMock.expect(provider.get(mockSession, endpoint, ids)).andReturn(new SocializeObject());
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.getAsync(mockSession, endpoint, ids, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	
	public void testServiceAsyncGetCallsAddOnResponse() throws Throwable {

		final String endpoint = "foobar";
		final int[] ids = null;
		
		SocializeObject obj = new SocializeObject();

		service.setResponseFactory(responseFactory);
		
		AndroidMock.expect(responseFactory.newEntityResponse()).andReturn(mockEntityResponse);
		AndroidMock.expect(provider.get(mockSession, endpoint, ids)).andReturn(obj);
		
		mockEntityResponse.addResult(obj);
		
		AndroidMock.replay(responseFactory);
		AndroidMock.replay(provider);
		AndroidMock.replay(mockEntityResponse);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.getAsync(mockSession, endpoint, ids, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(responseFactory);
		AndroidMock.verify(provider);
		AndroidMock.verify(mockEntityResponse);
	}
	
	public void testServiceAsyncListCallsSetResultsOnResponse() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		
		final List<SocializeObject> returned = new LinkedList<SocializeObject>();
		
		service.setResponseFactory(responseFactory);
		
		AndroidMock.expect(responseFactory.newEntityResponse()).andReturn(mockEntityResponse);
		AndroidMock.expect(provider.list(mockSession, endpoint, key)).andReturn(returned);
		mockEntityResponse.setResults(returned);
		
		AndroidMock.replay(responseFactory);
		AndroidMock.replay(provider);
		AndroidMock.replay(mockEntityResponse);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.listAsync(mockSession, endpoint, key, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(responseFactory);
		AndroidMock.verify(provider);
		AndroidMock.verify(mockEntityResponse);
	}
	
	
	
	public void testServiceAsyncCallsListOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		
		final List<SocializeObject> returned = new LinkedList<SocializeObject>();
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key)).andReturn(returned);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.listAsync(mockSession, endpoint, key, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testServiceAsyncCallsPutOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.put(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.putAsync(mockSession, endpoint, object, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
	
	public void testServiceAsyncCallsPostOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.post(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.postAsync(mockSession, endpoint, object, listener);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
}
