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

import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.error.SocializeException;
import com.socialize.oauth.OAuthRequestSigner;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.util.JsonAssert;

/**
 * @author Jason Polites
 */
@UsesMocks({SocializeSession.class, OAuthRequestSigner.class})
public class SocializeRequestFactoryTest extends SocializeActivityTest {
	
	public void testAuthRequestCreate() throws Exception {
		
		final String endpoint = "foobar/";
		final String id = "testid";
		
		OAuthRequestSigner signer = new OAuthRequestSigner() {
			
			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				assertTrue(request instanceof HttpPost);
				HttpPost post = (HttpPost) request;
				assertEquals(post.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}
		};
		
		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);
		
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest req = factory.getAuthRequest(session, endpoint, id);
		
		assertTrue(req instanceof HttpPost);
		
		HttpPost post = (HttpPost) req;
		
		HttpEntity entity = post.getEntity();
		
		assertNotNull(entity);
		
		assertTrue(entity instanceof UrlEncodedFormEntity);
		
		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);
		
		assertEquals(1, parsed.size());
		
		NameValuePair nvp = parsed.get(0);
		
		assertEquals("payload", nvp.getName());
		assertEquals( "{'udid':" + id + "}", nvp.getValue());
		
		assertTrue((Boolean)getResult());
	}
	
	public void testGetRequestCreate() throws Exception {
		
		final String endpoint = "foobar/";
		final String id = "testid";
		
		OAuthRequestSigner signer = new OAuthRequestSigner() {
			
			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				assertTrue(request instanceof HttpGet);
				HttpGet get = (HttpGet) request;
				assertEquals(get.getURI().toString(), endpoint + id);
				
				addResult(true);
				return request;
			}
		};
		
		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);
		
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest getRequest = factory.getGetRequest(session, endpoint, id);
		
		assertTrue((Boolean)getResult());
		assertTrue(getRequest instanceof HttpGet);
	}
	
	
	public void testListRequestCreate() throws Exception {
		
		final String endpoint = "foobar/";
		final String key = "testid";
		final String[] ids = {"foo", "bar"};
		
		OAuthRequestSigner signer = new OAuthRequestSigner() {
			
			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				assertTrue(request instanceof HttpPost);
				HttpPost list = (HttpPost) request;
				assertEquals(list.getURI().toString(), endpoint);
				
				addResult(true);
				return request;
			}
		};
		
		SocializeRequestFactory<SocializeObject> factory = new DefaultSocializeRequestFactory<SocializeObject>(signer, null);
		
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);

		HttpUriRequest req = factory.getListRequest(session, endpoint, key, ids);
		
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
		JSONArray array = new JSONArray(Arrays.asList(ids));
		jsonExpected.put("ids", array);
		jsonExpected.put("key", key);
		
		JSONObject jsonActual = new JSONObject(nvp.getValue());
		
		JsonAssert.assertJsonObjectEquals(jsonExpected, jsonActual);
		
		assertTrue((Boolean)getResult());
	}
	
	
	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeObjectFactory.class, JSONObject.class, SocializeSession.class})
	public void testPostRequestCreate() throws Exception {
		
		SocializeObjectFactory<SocializeObject> factory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
		SocializeObject object = new SocializeObject();
		final String jsonData = "foobar";
		final String endpoint = "foobar/";
		
		
		/**
		 * The toString() method can't be mocked by EasyMock (no idea why!)
		 * so we can't use a mock for the JSON object.  We'll have to do it manually.
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
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				HttpPut.class.isInstance(request);
				assertEquals(request.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}
		};
		
		SocializeRequestFactory<SocializeObject> reqFactory = new DefaultSocializeRequestFactory<SocializeObject>(signer, factory);
		
		AndroidMock.replay(factory);
		
		HttpUriRequest req = reqFactory.getPutRequest(session, endpoint, object);
		
		assertTrue((Boolean)getResult());
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
		assertEquals( jsonData, nvp.getValue());
		
		AndroidMock.verify(factory);
	}

	@SuppressWarnings("unchecked")
	@UsesMocks({SocializeObjectFactory.class, JSONObject.class, SocializeSession.class})
	public void testPutRequestCreate() throws Exception {
		
		SocializeObjectFactory<SocializeObject> factory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		
		SocializeObject object = new SocializeObject();
		final String jsonData = "foobar";
		final String endpoint = "foobar/";
		
		
		/**
		 * The toString() method can't be mocked by EasyMock (no idea why!)
		 * so we can't use a mock for the JSON object.  We'll have to do it manually.
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
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				HttpPost.class.isInstance(request);
				assertEquals(request.getURI().toString(), endpoint);
				addResult(true);
				return request;
			}
		};
		
		SocializeRequestFactory<SocializeObject> reqFactory = new DefaultSocializeRequestFactory<SocializeObject>(signer, factory);
		
		AndroidMock.replay(factory);
		
		HttpUriRequest req = reqFactory.getPostRequest(session, endpoint, object);
		
		assertTrue((Boolean)getResult());
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
		assertEquals( jsonData, nvp.getValue());
		
		AndroidMock.verify(factory);
	}
	
}
