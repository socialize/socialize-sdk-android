package com.socialize.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApiError;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeService;
import com.socialize.api.SocializeService.RequestType;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.listener.SocializeListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.sample.Main;

public class SocializeServiceTest extends ActivityInstrumentationTestCase2<Main> {


	private SocializeService<SocializeObject, SocializeProvider<SocializeObject>> service;
	private SocializeProvider<SocializeObject> provider;
	
	private CountDownLatch signal;
	private SocializeSession mockSession;

	public SocializeServiceTest() {
		super("com.socialize.sample", Main.class);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeProvider.class, SocializeSession.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		signal = new CountDownLatch(1); 
		provider = AndroidMock.createMock(SocializeProvider.class);
		service = new SocializeService<SocializeObject, SocializeProvider<SocializeObject>>(provider);
		
		mockSession = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.replay(mockSession);
		
		service.setListener(new SocializeListener<SocializeObject>() {

			@Override
			public void onResult(RequestType type, SocializeResponse response) {
				System.out.println("Service listener onResult fired");
				signal.countDown(); 
			}

			@Override
			public void onError(SocializeApiError error) {
				System.out.println("Service listener onError fired");
				signal.countDown();
			}
		});
	}
	
	public void testServiceAsyncCallsAuthenticateOnProvider() throws Throwable {

		AndroidMock.expect(provider.authenticate("test_key", "test_secret", "test_uuid")).andReturn(mockSession);
		AndroidMock.replay(provider);

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.authenticateAsync("test_key", "test_secret", "test_uuid");
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
				service.getAsync(mockSession, endpoint, ids);
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
				service.putAsync(mockSession, endpoint, object);
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		AndroidMock.verify(provider);
	}
}
