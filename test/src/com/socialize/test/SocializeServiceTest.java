package com.socialize.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApiError;
import com.socialize.api.SocializeResponse;
import com.socialize.api.SocializeService;
import com.socialize.api.SocializeSessionImpl;
import com.socialize.api.SocializeService.RequestType;
import com.socialize.entity.SocializeObject;
import com.socialize.listener.SocializeListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.sample.Main;

public class SocializeServiceTest extends ActivityInstrumentationTestCase2<Main> {


	private SocializeService<SocializeObject, SocializeProvider<SocializeObject>> service;
	private SocializeProvider<SocializeObject> provider;

	public SocializeServiceTest() {
		super("com.socialize.sample", Main.class);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks(SocializeProvider.class)
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		provider = AndroidMock.createMock(SocializeProvider.class);
		service = new SocializeService<SocializeObject, SocializeProvider<SocializeObject>>(provider);
	}

	public void testAuthenticateAsync() throws Throwable {

		final CountDownLatch signal = new CountDownLatch(1); 
		
		AndroidMock.expect(provider.authenticate("test_key", "test_secret", "test_uuid")).andReturn(new SocializeSessionImpl());
		AndroidMock.replay(provider);

		service.setListener(new SocializeListener<SocializeObject>() {

			@Override
			public void onResult(RequestType type, SocializeResponse response) {
				signal.countDown(); 
			}

			@Override
			public void onError(SocializeApiError error) {
				signal.countDown();
			}
		});

		runTestOnUiThread(new Runnable() { 
			@Override 
			public void run() { 
				service.authenticateAsync("test_key", "test_secret", "test_uuid");
			} 
		});

		signal.await(30, TimeUnit.SECONDS); 
		
		
		AndroidMock.verify(provider);
	}
}
