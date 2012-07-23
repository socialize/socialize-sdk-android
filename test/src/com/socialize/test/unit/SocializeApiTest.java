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
package com.socialize.test.unit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeAuthRequest;
import com.socialize.api.SocializeSession;
import com.socialize.api.WritableSession;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.Propagation;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.provider.SocializeProvider;
import com.socialize.test.SocializeActivityTest;

public class SocializeApiTest extends SocializeActivityTest {

	private SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> api;
	private SocializeProvider<SocializeObject> provider;

	private WritableSession mockSession;

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeProvider.class, WritableSession.class })
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		provider = AndroidMock.createMock(SocializeProvider.class);
		api = new SocializeApi<SocializeObject, SocializeProvider<SocializeObject>>(provider);

		mockSession = AndroidMock.createMock(WritableSession.class);

		AndroidMock.replay(mockSession);

	}

	public void testClearSessionCallsClearSessionOnProvider() throws Throwable {
		provider.clearSession();
		AndroidMock.replay(provider);
		api.clearSession();
		AndroidMock.verify(provider);
	}

	public void testLoadSessionCallsLoadSessionOnProviderWithAuthProviderData() throws Throwable {
		AndroidMock.expect(provider.loadSession("test_endpoint", "test_key", "test_secret")).andReturn(mockSession);
		AndroidMock.replay(provider);
		api.loadSession("test_endpoint", "test_key", "test_secret");
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

		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>(new LinkedList<SocializeObject>());

		AndroidMock.expect(provider.list(mockSession, endpoint, key, ids, start, end)).andReturn(returned);
		AndroidMock.replay(provider);

		api.list(mockSession, endpoint, key, start, end, ids);

		AndroidMock.verify(provider);
	}

	public void testApiCallsListOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final String key = "foobar_key";
		final String[] ids = null;

		final ListResult<SocializeObject> returned = new ListResult<SocializeObject>(new LinkedList<SocializeObject>());

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

		AndroidMock.expect(provider.post(mockSession, endpoint, objects, true)).andReturn(null);
		AndroidMock.replay(provider);

		api.post(mockSession, endpoint, objects);

		AndroidMock.verify(provider);
	}

	public void testApiCallsPostOnProvider() throws Throwable {

		final String endpoint = "foobar";
		final SocializeObject object = null;

		AndroidMock.expect(provider.post(mockSession, endpoint, object, true)).andReturn(null);
		AndroidMock.replay(provider);

		api.post(mockSession, endpoint, object, true);

		AndroidMock.verify(provider);
	}
	
	public void testSetPropagationDataForOGLike() {
		
		MockSocializeApi api = new MockSocializeApi();
		
		PublicShareOptions options = new PublicShareOptions();
		options.setSelfManaged(true);
		
		Like action = new Like();
		SocializeConfig config = new SocializeConfig();
		config.setProperty(SocializeConfig.FACEBOOK_OG_USE_INBUILT_LIKE, "true");
		
		api.setConfig(config);
		api.setPropagationData(action, options, SocialNetwork.TWITTER);
		
		Propagation propagationInfoRequest = action.getPropagationInfoRequest();
		Propagation propagation = action.getPropagation();
		
		assertNull(propagation);
		assertNotNull(propagationInfoRequest);
		
		Map<String, String> extraParams = propagationInfoRequest.getExtraParams();
		
		assertNotNull(extraParams);
		
		String og_action = (String) extraParams.get("og_action");
		
		assertNotNull(og_action);
		assertEquals("like", og_action);
		
	}
	
	public void testSetPropagationDataLocal() {
		
		MockSocializeApi api = new MockSocializeApi();
		
		PublicShareOptions options = new PublicShareOptions();
		
		Like action = new Like();
		api.setPropagationData(action, options, SocialNetwork.TWITTER);
		
		Propagation propagationInfoRequest = action.getPropagationInfoRequest();
		Propagation propagation = action.getPropagation();
		
		assertNull(propagation);
		assertNotNull(propagationInfoRequest);
		
		List<ShareType> thirdParties = propagationInfoRequest.getThirdParties();
		
		assertNotNull(thirdParties);
		assertTrue(thirdParties.contains(ShareType.TWITTER));
	}	

	@UsesMocks({ 
		SocializeProvider.class, 
		SocializeAuthRequest.class, 
		AuthProviderResponse.class, 
		AuthProviders.class, 
		SocializeAuthListener.class, 
		SocializeActionListener.class, 
		IBeanFactory.class, 
		AuthProviderData.class,
		AuthProviderInfo.class})
	@SuppressWarnings("unchecked")
	public void testHandle3rdPartyAuthSuccess() throws SocializeException {

		String key = "foobar_key";
		String secret = "foobar_secret";

		String token = "foobar_token";
		String user = "foobar_user";
		String token_secret = "foobar_token_secret";
		String endpoint = "foobar_endpoint";

		AuthProviderType authProviderType = AuthProviderType.FACEBOOK;

		AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		AuthProviders authProviders = AndroidMock.createMock(AuthProviders.class);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		SocializeAuthRequest request = AndroidMock.createMock(SocializeAuthRequest.class);
		SocializeActionListener actionListener = AndroidMock.createMock(SocializeActionListener.class);
		AuthProviderResponse response = AndroidMock.createMock(AuthProviderResponse.class);

		AuthProviderData authProviderData = AndroidMock.createMock(AuthProviderData.class);

		MockSocializeApi api = new MockSocializeApi(provider);

		AuthProvider<AuthProviderInfo> authProvider = new AuthProvider<AuthProviderInfo>() {
			
			@Override
			public void authenticate(AuthProviderInfo info, AuthProviderListener listener) {
				addResult(1, listener);
			}

			@Override
			public void clearCache(Context context, AuthProviderInfo info) {
				fail();
			}

			@Override
			public boolean validate(AuthProviderInfo info) {
				return true;
			}
		};

		
		AndroidMock.expect(request.getEndpoint()).andReturn(endpoint);
		AndroidMock.expect(authProviders.getProvider(authProviderType)).andReturn(authProvider);
		AndroidMock.expect(request.getAuthProviderData()).andReturn(authProviderData);
		AndroidMock.expect(authProviderData.getAuthProviderInfo()).andReturn(authProviderInfo).anyTimes();
		AndroidMock.expect(authProviderInfo.getType()).andReturn(authProviderType);

		authProviderInfo.validate();
		
		AndroidMock.expect(response.getToken()).andReturn(token);
		AndroidMock.expect(response.getUserId()).andReturn(user);
		AndroidMock.expect(response.getSecret()).andReturn(token_secret);
		

		authProviderData.setUserId3rdParty(user);
		authProviderData.setToken3rdParty(token);
		authProviderData.setSecret3rdParty(token_secret);

		api.setAuthProviders(authProviders);
		// api.setAuthProviderDataFactory(authProviderDataFactory);
		
		AndroidMock.replay(authProviderData, authProviderInfo, authProviders, request, response);

		api.handle3rdPartyAuth(getContext(), request, actionListener, listener, key, secret);

		AuthProviderListener authProviderListener = getResult(1);
		String loadSession = getResult(0);

		assertNotNull(loadSession);
		assertNotNull(authProviderListener);

		assertEquals("loadSession", loadSession);

		// Call success on the listener
		authProviderListener.onAuthSuccess(response);

		String handleRegularAuth = getResult(3);
		assertNotNull(handleRegularAuth);

		assertEquals("handleRegularAuth", handleRegularAuth);

		AndroidMock.verify(authProviderData, authProviderInfo, authProviders, request, response);
	}

	@UsesMocks({ 
		SocializeProvider.class, 
		SocializeAuthRequest.class, 
		AuthProviders.class, 
		SocializeAuthListener.class, 
		SocializeActionListener.class, 
		SocializeException.class, 
		IBeanFactory.class, 
		AuthProviderData.class,
		AuthProviderInfo.class})
	@SuppressWarnings("unchecked")
	public void testHandle3rdPartyAuthFail() throws SocializeException {

		String key = "foobar_key";
		String secret = "foobar_secret";
		String endpoint = "foobar_endpoint";

		AuthProviderType authProviderType = AuthProviderType.FACEBOOK;

		AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		SocializeProvider<SocializeObject> provider = AndroidMock.createMock(SocializeProvider.class);
		AuthProviders authProviders = AndroidMock.createMock(AuthProviders.class);
		SocializeAuthListener listener = AndroidMock.createMock(SocializeAuthListener.class);
		SocializeAuthRequest request = AndroidMock.createMock(SocializeAuthRequest.class);
		SocializeActionListener actionListener = AndroidMock.createMock(SocializeActionListener.class);
		SocializeException error = AndroidMock.createMock(SocializeException.class);

		AuthProviderData authProviderData = AndroidMock.createMock(AuthProviderData.class);

		MockSocializeApi api = new MockSocializeApi(provider);

		AuthProvider<AuthProviderInfo> authProvider = new AuthProvider<AuthProviderInfo>() {
			@Override
			public void authenticate(AuthProviderInfo info, AuthProviderListener listener) {
				addResult(1, listener);
			}

			@Override
			public void clearCache(Context context, AuthProviderInfo info) {
				fail();
			}
			
			@Override
			public boolean validate(AuthProviderInfo info) {
				return true;
			}
		};

		listener.onError(error);
		listener.onError(error);

		AndroidMock.expect(request.getEndpoint()).andReturn(endpoint);
		AndroidMock.expect(authProviders.getProvider(authProviderType)).andReturn(authProvider);
		AndroidMock.expect(request.getAuthProviderData()).andReturn(authProviderData);
		AndroidMock.expect(authProviderData.getAuthProviderInfo()).andReturn(authProviderInfo).anyTimes();
		AndroidMock.expect(authProviderInfo.getType()).andReturn(authProviderType);

		authProviderInfo.validate();
		
		api.setAuthProviders(authProviders);

		AndroidMock.replay(authProviderData, authProviderInfo, authProviders, request);

		api.handle3rdPartyAuth(getContext(), request, actionListener, listener, key, secret);

		AuthProviderListener authProviderListener = getResult(1);
		String loadSession = getResult(0);

		assertNotNull(loadSession);
		assertNotNull(authProviderListener);

		assertEquals("loadSession", loadSession);

		// Call fail on the listener
		authProviderListener.onAuthFail(error);
		authProviderListener.onError(error);

		String handleRegularAuth = getResult(3);
		assertNull(handleRegularAuth);

		AndroidMock.verify(authProviderData, authProviderInfo, authProviders, request);

	}
	
	public class PublicShareOptions extends ShareOptions {
		public PublicShareOptions() {
			super();
		}
	}

	public class MockSocializeApi extends SocializeApi<SocializeObject, SocializeProvider<SocializeObject>> {

		public MockSocializeApi(SocializeProvider<SocializeObject> provider) {
			super(provider);
		}

		public MockSocializeApi() {
			super(provider);
		}
		
		@Override
		protected void handleRegularAuth(Context context, SocializeAuthRequest request, SocializeActionListener wrapper, SocializeAuthListener listener, String key, String secret) {
			addResult(3, "handleRegularAuth");
		}

		@Override
		public void handle3rdPartyAuth(Context context, SocializeAuthRequest request, SocializeActionListener fWrapper, SocializeAuthListener listener, String key, String secret) {
			super.handle3rdPartyAuth(context, request, fWrapper, listener, key, secret);
		}

		@Override
		public SocializeSession loadSession(String endpoint, String key, String secret) throws SocializeException {
			addResult(0, "loadSession");
			return null;
		}

		@Override
		public void setPropagationData(SocializeAction action, ActionOptions shareOptions, SocialNetwork... networks) {
			super.setPropagationData(action, shareOptions, networks);
		}

		@Override
		public void setPropagationData(SocializeAction action, ShareType shareType) {
			super.setPropagationData(action, shareType);
		}
	}
}
