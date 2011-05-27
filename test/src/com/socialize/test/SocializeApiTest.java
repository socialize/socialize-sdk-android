package com.socialize.test;

import java.util.LinkedList;
import java.util.List;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.SocializeObject;
import com.socialize.provider.SocializeProvider;

public class SocializeApiTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> service;
	private SocializeProvider<SocializeObject> provider;
	
	private SocializeSession mockSession;

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeProvider.class, SocializeSession.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		service = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
		
		mockSession = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.replay(mockSession);
		
	}
	
	public void testServiceCallsAuthenticateOnProvider() throws Throwable {

		AndroidMock.expect(provider.authenticate("test_endpoint", "test_key", "test_secret", "test_uuid")).andReturn(mockSession);
		AndroidMock.replay(provider);

		service.authenticate("test_endpoint", "test_key", "test_secret", "test_uuid");
		
		AndroidMock.verify(provider);
	}
	
	public void testServiceCallsGetOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final int[] ids = null;
		
		AndroidMock.expect(provider.get(mockSession, endpoint, ids)).andReturn(new SocializeObject());
		AndroidMock.replay(provider);

		service.get(mockSession, endpoint, ids);
		
		AndroidMock.verify(provider);
	}
	
	public void testServiceCallsListOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		
		final List<SocializeObject> returned = new LinkedList<SocializeObject>();
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key)).andReturn(returned);
		AndroidMock.replay(provider);

		service.list(mockSession, endpoint, key);

		AndroidMock.verify(provider);
	}
	
	public void testServiceCallsPutOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.put(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		service.put(mockSession, endpoint, object);

		AndroidMock.verify(provider);
	}
	
	public void testServiceCallsPostOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.post(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		service.post(mockSession, endpoint, object);
		
		AndroidMock.verify(provider);
	}
}
