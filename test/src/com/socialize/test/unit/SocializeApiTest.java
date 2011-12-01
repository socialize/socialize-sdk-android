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

import android.content.Context;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeAuthRequest;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeActivityTest;

public class SocializeApiTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> api;
	private SocializeProvider<SocializeObject> provider;
	
	private SocializeSession mockSession;

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeProvider.class, SocializeSession.class})
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		api = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);
		
		mockSession = AndroidMock.createMock(SocializeSession.class);
		
		AndroidMock.replay(mockSession);
		
	}
	
	public void testApiCallsAuthenticateOnProvider() throws Throwable {

		AndroidMock.expect(provider.authenticate("test_endpoint", "test_key", "test_secret", "test_uuid")).andReturn(mockSession);
		AndroidMock.replay(provider);

		api.authenticate("test_endpoint", "test_key", "test_secret", "test_uuid");
		
		AndroidMock.verify(provider);
	}
	
	public void testApiCallsAuthenticateOnProviderWithAuthProviderData() throws Throwable {
		AuthProviderData data = new AuthProviderData();
		AndroidMock.expect(provider.authenticate("test_endpoint", "test_key", "test_secret", data,  "test_uuid")).andReturn(mockSession);
		AndroidMock.replay(provider);
		api.authenticate("test_endpoint", "test_key", "test_secret", data,  "test_uuid");
		AndroidMock.verify(provider);
	}
	
	public void testClearSessionCallsClearSessionOnProvider() throws Throwable {
		provider.clearSession();
		AndroidMock.replay(provider);
		api.clearSession();
		AndroidMock.verify(provider);
	}
	
	public void testLoadSessionCallsLoadSessionOnProviderWithAuthProviderData() throws Throwable {
		AuthProviderData data = new AuthProviderData();
		AndroidMock.expect(provider.loadSession("test_endpoint", "test_key", "test_secret", data)).andReturn(mockSession);
		AndroidMock.replay(provider);
		api.loadSession("test_endpoint", "test_key", "test_secret", data);
		AndroidMock.verify(provider);
	}
	
	public void testApiCallsGetOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String ids = null;
		
		AndroidMock.expect(provider.get(mockSession, endpoint, ids)).andReturn(new SocializeObject());
		AndroidMock.replay(provider);

		api.get(mockSession, endpoint, ids);
		
		AndroidMock.verify(provider);
	}
	
	public void testApiCallsListOnProviderPaginated() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String[] ids = null;
		
		final int start = 0, end = 10;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, start, end)).andReturn(returned);
		AndroidMock.replay(provider);

		api.list(mockSession, endpoint, key, ids, start, end);

		AndroidMock.verify(provider);
	}
	
	public void testApiCallsListOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String[] ids = null;
		
		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>( new LinkedList<SocializeObject>() );
		
		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS)).andReturn(returned);
		AndroidMock.replay(provider);

		api.list(mockSession, endpoint, key, ids);

		AndroidMock.verify(provider);
	}
	
	public void testApiCallsPutOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.put(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		api.put(mockSession, endpoint, object);

		AndroidMock.verify(provider);
	}
	
	public void testApiCallsPutOnProviderWithList() throws Throwable {

		final String endpoint = "foobar";
		final List<SocializeObject> objects = new ArrayList<SocializeObject>();
		
		AndroidMock.expect(provider.put(mockSession, endpoint, objects)).andReturn(null);
		AndroidMock.replay(provider);

		api.put(mockSession, endpoint, objects);

		AndroidMock.verify(provider);
	}
	
	public void testApiCallsPostOnProviderWithList() throws Throwable {

		final String endpoint = "foobar";
		final List<SocializeObject> objects = new ArrayList<SocializeObject>();
		
		AndroidMock.expect(provider.post(mockSession, endpoint, objects)).andReturn(null);
		AndroidMock.replay(provider);

		api.post(mockSession, endpoint, objects);

		AndroidMock.verify(provider);
	}
	
	
	public void testApiCallsPostOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;
		
		AndroidMock.expect(provider.post(mockSession, endpoint, object)).andReturn(null);
		AndroidMock.replay(provider);

		api.post(mockSession, endpoint, object);
		
		AndroidMock.verify(provider);
	}
	
	@UsesMocks ({
		SocializeProvider.class, 
		SocializeAuthRequest.class,
		AuthProviderResponse.class,
		AuthProviders.class,
		SocializeAuthListener.class,
		SocializeActionListener.class,
		IBeanFactory.class,
		AuthProviderData.class})
	@SuppressWarnings("unchecked")
	public void testHandle3rdPartyAuthSuccess() {
		
		String appId3rdParty = "foobar_appId3rdParty";
		String key = "foobar_key";
		String secret = "foobar_secret";
		
		String token = "foobar_token";
		String user = "foobar_user";
		String endpoint = "foobar_endpoint";
		
		AuthProviderType authProviderType = AuthProviderType.FACEBOOK;
		
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		AuthProviders authProviders = AndroidMock.createMock(AuthProviders.class);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		SocializeAuthRequest request = AndroidMock.createMock(SocializeAuthRequest.class);
		SocializeActionListener actionListener = AndroidMock.createMock(SocializeActionListener.class);
		AuthProviderResponse response = AndroidMock.createMock(AuthProviderResponse.class);
		
		IBeanFactory<AuthProviderData> authProviderDataFactory = AndroidMock.createMock(IBeanFactory.class);
		AuthProviderData authProviderData = AndroidMock.createMock(AuthProviderData.class);
		
		MockSocializeApi api = new MockSocializeApi(provider);
		
		AuthProvider authProvider = new AuthProvider() {
			@Override
			public void authenticate(SocializeAuthRequest authRequest, String appId, AuthProviderListener listener) {
				addResult(listener);
			}

			@Override
			public void clearCache(Context context, String appId) {
				fail();
			}
		};
		
		AndroidMock.expect(request.getEndpoint()).andReturn(endpoint);
		AndroidMock.expect(request.getAuthProviderData()).andReturn(authProviderData);
		AndroidMock.expect(authProviderData.getAppId3rdParty()).andReturn(appId3rdParty);
		AndroidMock.expect(authProviderData.getAuthProviderType()).andReturn(authProviderType);
		AndroidMock.expect(authProviders.getProvider(authProviderType)).andReturn(authProvider);
		AndroidMock.expect(response.getToken()).andReturn(token);
		AndroidMock.expect(response.getUserId()).andReturn(user);
		
		authProviderData.setUserId3rdParty(user);
		authProviderData.setToken3rdParty(token);
		
		api.setAuthProviders(authProviders);
		api.setAuthProviderDataFactory(authProviderDataFactory);
		
		AndroidMock.replay(authProviderData);
		AndroidMock.replay(authProviders);
		AndroidMock.replay(request);
		AndroidMock.replay(response);
		
		api.handle3rdPartyAuth(request, actionListener, listener, key, secret);
		
		AuthProviderListener authProviderListener = getNextResult();
		String loadSession = getNextResult();
		
		assertNotNull(loadSession);
		assertNotNull(authProviderListener);
		
		assertEquals("loadSession", loadSession);
		
		// Call success on the listener
		authProviderListener.onAuthSuccess(response);
		
		String handleRegularAuth = getNextResult();
		assertNotNull(handleRegularAuth);
		
		assertEquals("handleRegularAuth", handleRegularAuth);
		
		AndroidMock.verify(authProviderData);
		AndroidMock.verify(authProviders);
		AndroidMock.verify(request);
		AndroidMock.verify(response);
	}
	
	
	@UsesMocks ({
		SocializeProvider.class, 
		SocializeAuthRequest.class,
		AuthProviders.class,
		SocializeAuthListener.class,
		SocializeActionListener.class,
		SocializeException.class,
		IBeanFactory.class,
		AuthProviderData.class})
	@SuppressWarnings("unchecked")
	public void testHandle3rdPartyAuthFail() {
		
//		String authUserId3rdParty = "foobar_authUserId3rdParty";
//		String authToken3rdParty = "foobar_authToken3rdParty";
		String appId3rdParty = "foobar_appId3rdParty";
		String key = "foobar_key";
		String secret = "foobar_secret";
		String endpoint = "foobar_endpoint";
		
		AuthProviderType authProviderType = AuthProviderType.FACEBOOK;
		
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		AuthProviders authProviders = AndroidMock.createMock(AuthProviders.class);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		SocializeAuthRequest request = AndroidMock.createMock(SocializeAuthRequest.class);
		SocializeActionListener actionListener = AndroidMock.createMock(SocializeActionListener.class);
		SocializeException error = AndroidMock.createMock(SocializeException.class);
		
		IBeanFactory<AuthProviderData> authProviderDataFactory = AndroidMock.createMock(IBeanFactory.class);
		AuthProviderData authProviderData = AndroidMock.createMock(AuthProviderData.class);
		
		MockSocializeApi api = new MockSocializeApi(provider);
		
		AuthProvider authProvider = new AuthProvider() {
			@Override
			public void authenticate(SocializeAuthRequest authRequest, String appId, AuthProviderListener listener) {
				addResult(listener);
			}
			@Override
			public void clearCache(Context context, String appId) {
				fail();
			}
		};

		listener.onError(error);
		listener.onError(error);
		
		AndroidMock.expect(request.getEndpoint()).andReturn(endpoint);
		AndroidMock.expect(authProviderData.getAuthProviderType()).andReturn(authProviderType);
		AndroidMock.expect(authProviderData.getAppId3rdParty()).andReturn(appId3rdParty);
		AndroidMock.expect(authProviders.getProvider(authProviderType)).andReturn(authProvider);
		AndroidMock.expect(request.getAuthProviderData()).andReturn(authProviderData);
		
		api.setAuthProviders(authProviders);
		api.setAuthProviderDataFactory(authProviderDataFactory);

		AndroidMock.replay(authProviderData);
		AndroidMock.replay(authProviders);
		AndroidMock.replay(request);
		
		api.handle3rdPartyAuth(request, actionListener, listener, key, secret);
		
		AuthProviderListener authProviderListener = getNextResult();
		String loadSession = getNextResult();
		
		assertNotNull(loadSession);
		assertNotNull(authProviderListener);
		
		assertEquals("loadSession", loadSession);
		
		// Call fail on the listener
		authProviderListener.onAuthFail(error);
		authProviderListener.onError(error);
		
		String handleRegularAuth = getNextResult();
		assertNull(handleRegularAuth);
		
		AndroidMock.verify(authProviderData);
		AndroidMock.verify(authProviders);
		AndroidMock.verify(request);
	}
	
	public class MockSocializeApi extends SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> {

		public MockSocializeApi(SocializeProvider<SocializeObject> provider) {
			super(provider);
		}
		
		@Override
		protected void handleRegularAuth(SocializeAuthRequest request, SocializeActionListener wrapper) {
			addResult("handleRegularAuth");
		}
		
		@Override
		public void handle3rdPartyAuth(SocializeAuthRequest request, SocializeActionListener fWrapper, SocializeAuthListener listener, String key, String secret) {
			super.handle3rdPartyAuth(request, fWrapper, listener, key, secret);
		}

		@Override
		public SocializeSession loadSession(String endpoint, String key, String secret, AuthProviderData data) throws SocializeException {
			addResult("loadSession");
			return null;
		}
	}
}
