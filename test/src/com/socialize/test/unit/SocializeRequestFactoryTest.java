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
import java.util.Collection;
import java.util.List;
import oauth.socialize.OAuthSignListener;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.DefaultSocializeRequestFactory;
import com.socialize.api.SocializeRequestFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.SocializeObjectFactory;
import com.socialize.error.SocializeException;
import com.socialize.oauth.OAuthRequestSigner;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.UrlBuilder;

/**
 * @author Jason Polites
 */
@UsesMocks({ SocializeSession.class, OAuthRequestSigner.class })
public class SocializeRequestFactoryTest extends SocializeActivityTest {

	@UsesMocks ({UserProviderCredentialsMap.class, AuthProviderData.class, AuthProviderInfo.class, UserProviderCredentials.class})
	public void testAuthRequestCreate() throws Exception {
		
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		UserProviderCredentialsMap userProviderCredentialsMap = AndroidMock.createMock(UserProviderCredentialsMap.class);
		AuthProviderData data = AndroidMock.createMock(AuthProviderData.class);
		AuthProviderInfo authProviderInfo = AndroidMock.createMock(AuthProviderInfo.class);
		UserProviderCredentials userProviderCredentials = AndroidMock.createMock(UserProviderCredentials.class);
		
		AuthProviderType type = AuthProviderType.SOCIALIZE;

		final String endpoint = "foobar/";
		final String udid = "testid";
		
		final String accessToken = "foobar_accessToken";
		final String tokenSecret = "foobar_tokenSecret";
		final String userId = "foobar_userId";
		
		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				assertTrue(request instanceof HttpPost);
				HttpPost post = (HttpPost) request;
				assertEquals(post.getURI().toString(), endpoint);
				assertNotNull(post.getEntity());
				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};
		

		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);

		AndroidMock.expect(session.getUserProviderCredentials()).andReturn(userProviderCredentialsMap);
		AndroidMock.expect(data.getAuthProviderInfo()).andReturn(authProviderInfo);
		AndroidMock.expect(authProviderInfo.getType()).andReturn(type).anyTimes();
		AndroidMock.expect(userProviderCredentialsMap.get(type)).andReturn(userProviderCredentials);
		AndroidMock.expect(userProviderCredentials.getAuthProviderInfo()).andReturn(authProviderInfo).anyTimes();
		AndroidMock.expect(userProviderCredentials.getAccessToken()).andReturn(accessToken).anyTimes();
		AndroidMock.expect(userProviderCredentials.getTokenSecret()).andReturn(tokenSecret).anyTimes();
		AndroidMock.expect(userProviderCredentials.getUserId()).andReturn(userId).anyTimes();
		
		AndroidMock.replay(session, data, authProviderInfo, userProviderCredentialsMap, userProviderCredentials);

		HttpUriRequest req = factory.getAuthRequest(session, endpoint, udid, data);

		assertTrue(req instanceof HttpPost);

		HttpPost post = (HttpPost) req;

		HttpEntity entity = post.getEntity();

		assertNotNull(entity);

		assertTrue(entity instanceof UrlEncodedFormEntity);

		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);

		assertEquals(1, parsed.size());

		NameValuePair nvp = parsed.get(0);

		assertEquals("payload", nvp.getName());

		JSONObject jsonExpected = new JSONObject();
		jsonExpected.put("udid", udid);
		jsonExpected.put("auth_type", type.getId());
		jsonExpected.put("auth_token", accessToken);
		jsonExpected.put("auth_token_secret", tokenSecret);
		jsonExpected.put("auth_id", userId);

		assertEquals(jsonExpected.toString(), nvp.getValue());

		assertTrue((Boolean) getNextResult());

		AndroidMock.verify(session);
	}

	public void testGetRequestCreate() throws Exception {

		final String endpoint = "foobar/";
		final String id = "testid";

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				assertTrue(request instanceof HttpGet);
				HttpGet get = (HttpGet) request;
				assertEquals(get.getURI().toString(), endpoint + id + "/");

				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);

		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest getRequest = factory.getGetRequest(session, endpoint, id);

		assertTrue((Boolean) getNextResult());
		assertTrue(getRequest instanceof HttpGet);
	}

	public void testDeleteRequestCreate() throws Exception {

		final String endpoint = "foobar/";
		final String id = "testid";

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				assertTrue(request instanceof HttpDelete);
				HttpDelete get = (HttpDelete) request;
				assertEquals(get.getURI().toString(), endpoint + id + "/");
				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}

		};

		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);

		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest getRequest = factory.getDeleteRequest(session, endpoint, id);

		assertTrue((Boolean) getNextResult());
		assertTrue(getRequest instanceof HttpDelete);
	}

	public void testListRequestCreate() throws Exception {

		final String endpoint = "foobar/";
		final String key = "testid";
		final String[] ids = { "foo", "bar" };

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				assertTrue(request instanceof HttpGet);
				HttpGet list = (HttpGet) request;

				addResult(list.getURI().toString());
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);

		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest req = factory.getListRequest(session, endpoint, key, ids);

		assertTrue(req instanceof HttpGet);

		String actual = getNextResult();

		UrlBuilder builder = new UrlBuilder();
		builder.start(endpoint);
		builder.addParam("entity_key", key);
		builder.addParams("id", ids);

		builder.addParam("first", "0");
		builder.addParam("last", String.valueOf(SocializeConfig.MAX_LIST_RESULTS));

		String expected = builder.toString();

		assertEquals(expected, actual);
	}

	public void testListRequestCreatePaginated() throws Exception {

		final String endpoint = "foobar/";
		final String key = "testid";
		final String[] ids = { "foo", "bar" };

		int start = 0, end = 10;

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				assertTrue(request instanceof HttpGet);
				HttpGet list = (HttpGet) request;

				addResult(list.getURI().toString());
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);

		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest req = factory.getListRequest(session, endpoint, key, ids, start, end);

		assertTrue(req instanceof HttpGet);

		String actual = getNextResult();

		UrlBuilder builder = new UrlBuilder();
		builder.start(endpoint);
		builder.addParam("entity_key", key);
		builder.addParams("id", ids);

		builder.addParam("first", String.valueOf(start));
		builder.addParam("last", String.valueOf(end));

		String expected = builder.toString();

		assertEquals(expected, actual);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeObjectFactory.class, JSONObject.class, SocializeSession.class })
	public void testPostRequestCreate() throws Exception {

		SocializeObjectFactory<SocializeObject> factory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		SocializeObject object = new SocializeObject();
		final String jsonData = "{ 'entity': 'http://www.example.com/interesting-story/', 'text': 'this was a great story' }";
		final String endpoint = "foobar/";

		/**
		 * The toString() method can't be mocked by EasyMock (no idea why!) so
		 * we can't use a mock for the JSON object. We'll have to do it
		 * manually.
		 */
		JSONObject json = new JSONObject() {
			@Override
			public String toString() {
				return jsonData;
			}
		};

		AndroidMock.expect(factory.toJSON(object)).andReturn(json);

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				HttpPost.class.isInstance(request);
				assertEquals(request.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> reqFactory = new DefaultSocializeRequestFactory<SocializeObject>(signer, factory);

		AndroidMock.replay(factory);

		HttpUriRequest req = reqFactory.getPostRequest(session, endpoint, object);

		assertTrue((Boolean) getNextResult());
		assertTrue(HttpPost.class.isInstance(req));
		assertTrue(HttpEntityEnclosingRequestBase.class.isAssignableFrom(req.getClass()));

		HttpEntityEnclosingRequestBase post = (HttpEntityEnclosingRequestBase) req;

		HttpEntity entity = post.getEntity();

		assertNotNull(entity);

		assertTrue(entity instanceof UrlEncodedFormEntity);

		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);

		assertEquals(1, parsed.size());

		NameValuePair nvp = parsed.get(0);

		assertEquals("payload", nvp.getName());
		assertEquals(jsonData, nvp.getValue());

		AndroidMock.verify(factory);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeObjectFactory.class, SocializeSession.class })
	public void testPostRequestCreateCollection() throws Exception {

		SocializeObjectFactory<SocializeObject> factory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		SocializeObject object0 = new SocializeObject();
		SocializeObject object1 = new SocializeObject();
		final String jsonData = "foo";
		final String endpoint = "foobar/";

		/**
		 * The toString() method can't be mocked by EasyMock (no idea why!) so
		 * we can't use a mock for the JSON object. We'll have to do it
		 * manually.
		 */
		JSONArray jsonArray = new JSONArray() {
			@Override
			public String toString() {
				return jsonData;
			}
		};

		Collection<SocializeObject> objects = new ArrayList<SocializeObject>(1);
		objects.add(object0);
		objects.add(object1);

		AndroidMock.expect(factory.toJSON(objects)).andReturn(jsonArray);
		AndroidMock.replay(factory);

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				HttpPost.class.isInstance(request);
				assertEquals(request.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> reqFactory = new DefaultSocializeRequestFactory<SocializeObject>(signer, factory);

		HttpUriRequest req = reqFactory.getPostRequest(session, endpoint, objects);

		assertTrue((Boolean) getNextResult());
		assertTrue(HttpPost.class.isInstance(req));
		assertTrue(HttpEntityEnclosingRequestBase.class.isAssignableFrom(req.getClass()));

		HttpEntityEnclosingRequestBase post = (HttpEntityEnclosingRequestBase) req;

		HttpEntity entity = post.getEntity();

		assertNotNull(entity);

		assertTrue(entity instanceof UrlEncodedFormEntity);

		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);

		assertEquals(1, parsed.size());

		NameValuePair nvp = parsed.get(0);

		assertEquals("payload", nvp.getName());
		assertEquals(jsonData, nvp.getValue());

		AndroidMock.verify(factory);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeObjectFactory.class, SocializeSession.class })
	public void testPutRequestCreateCollection() throws Exception {

		SocializeObjectFactory<SocializeObject> factory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		SocializeObject object0 = new SocializeObject();
		SocializeObject object1 = new SocializeObject();
		final String jsonData = "foo";
		final String endpoint = "foobar/";

		/**
		 * The toString() method can't be mocked by EasyMock (no idea why!) so
		 * we can't use a mock for the JSON object. We'll have to do it
		 * manually.
		 */
		JSONArray jsonArray = new JSONArray() {
			@Override
			public String toString() {
				return jsonData;
			}
		};

		Collection<SocializeObject> objects = new ArrayList<SocializeObject>(1);
		objects.add(object0);
		objects.add(object1);

		AndroidMock.expect(factory.toJSON(objects)).andReturn(jsonArray);
		AndroidMock.replay(factory);

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				HttpPut.class.isInstance(request);
				assertEquals(request.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> reqFactory = new DefaultSocializeRequestFactory<SocializeObject>(signer, factory);

		HttpUriRequest req = reqFactory.getPutRequest(session, endpoint, objects);

		assertTrue((Boolean) getNextResult());
		assertTrue(HttpPut.class.isInstance(req));
		assertTrue(HttpEntityEnclosingRequestBase.class.isAssignableFrom(req.getClass()));

		HttpEntityEnclosingRequestBase post = (HttpEntityEnclosingRequestBase) req;

		HttpEntity entity = post.getEntity();

		assertNotNull(entity);

		assertTrue(entity instanceof UrlEncodedFormEntity);

		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);

		assertEquals(1, parsed.size());

		NameValuePair nvp = parsed.get(0);

		assertEquals("payload", nvp.getName());
		assertEquals(jsonData, nvp.getValue());

		AndroidMock.verify(factory);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({ SocializeObjectFactory.class, JSONObject.class, SocializeSession.class })
	public void testPutRequestCreate() throws Exception {

		SocializeObjectFactory<SocializeObject> factory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		SocializeObject object = new SocializeObject();
		final String jsonData = "foobar";
		final String endpoint = "foobar/";

		/**
		 * The toString() method can't be mocked by EasyMock (no idea why!) so
		 * we can't use a mock for the JSON object. We'll have to do it
		 * manually.
		 */
		JSONObject json = new JSONObject() {
			@Override
			public String toString() {
				return jsonData;
			}
		};

		AndroidMock.expect(factory.toJSON(object)).andReturn(json);

		OAuthRequestSigner signer = new OAuthRequestSigner() {

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				HttpPut.class.isInstance(request);
				assertEquals(request.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return sign(session, request, null);
			}
		};

		SocializeRequestFactory<SocializeObject> reqFactory = new DefaultSocializeRequestFactory<SocializeObject>(signer, factory);

		AndroidMock.replay(factory);

		HttpUriRequest req = reqFactory.getPutRequest(session, endpoint, object);

		assertTrue((Boolean) getNextResult());
		assertTrue(HttpPut.class.isInstance(req));
		assertTrue(HttpEntityEnclosingRequestBase.class.isAssignableFrom(req.getClass()));

		HttpEntityEnclosingRequestBase post = (HttpEntityEnclosingRequestBase) req;

		HttpEntity entity = post.getEntity();

		assertNotNull(entity);

		assertTrue(entity instanceof UrlEncodedFormEntity);

		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);

		assertEquals(1, parsed.size());

		NameValuePair nvp = parsed.get(0);

		assertEquals("payload", nvp.getName());
		assertEquals(jsonData, nvp.getValue());

		AndroidMock.verify(factory);
	}
}
