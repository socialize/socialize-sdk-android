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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.SocializeRequestFactory;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.api.WritableSession;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.User;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.net.HttpClientFactory;
import com.socialize.provider.DefaultSocializeProvider;
import com.socialize.test.SocializeActivityTest;
import com.socialize.util.HttpUtils;
import com.socialize.util.JSONParser;

/**
 * @author Jason Polites
 *
 */
@UsesMocks({
	SocializeObjectFactory.class, 
	HttpClientFactory.class, 
	SocializeSessionFactory.class,
	WritableSession.class,
	HttpClient.class,
	JSONParser.class,
	JSONObject.class,
	SocializeRequestFactory.class,
	HttpUriRequest.class,
	HttpResponse.class,
	User.class,
	HttpEntity.class,
	HttpUtils.class
	})
public class SocializeProviderTest extends SocializeActivityTest {

	
	@SuppressWarnings("unchecked")
	public void testProviderAuthenticate() throws Exception {
		
		final String key = "foo";
		final String secret = "bar";
		final String uuid = "uuid";
		final String endpoint = "foobar/";
		final String oauth_token = "oauth_token";
		final String oauth_token_secret = "oauth_token_secret";
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		User user = AndroidMock.createMock(User.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(sessionFactory.create(key, secret)).andReturn(session);
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(requestFactory.getAuthRequest(session, endpoint, uuid)).andReturn(request);
		AndroidMock.expect(jsonParser.parseObject((InputStream)null)).andReturn(json);
		AndroidMock.expect(json.getJSONObject("user")).andReturn(json);
		AndroidMock.expect(json.getString("oauth_token")).andReturn(oauth_token);
		AndroidMock.expect(json.getString("oauth_token_secret")).andReturn(oauth_token_secret);
		AndroidMock.expect(userFactory.fromJSON(json)).andReturn(user);
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		session.setConsumerToken(oauth_token);
		session.setConsumerTokenSecret(oauth_token_secret);
		session.setUser(user);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		provider.authenticate(endpoint, key, secret, uuid);
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(utils);
	}
	
	@SuppressWarnings("unchecked")
	public void testProviderGet() throws Exception {
		
		final String id = "foo";
		final String endpoint = "foobar/";
		final SocializeObject object = new SocializeObject();
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getGetRequest(session, endpoint, id)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseObject((InputStream)null)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object);
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(objectFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		SocializeObject gotten = provider.get(session, endpoint, id);
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(objectFactory);
		AndroidMock.verify(utils);
		
		assertSame(object,gotten);
	}
	

	@UsesMocks({JSONArray.class})
	@SuppressWarnings("unchecked")
	public void testProviderList() throws Exception {
		
		final String key = "foo";
		final String[] ids = {"foo", "bar"};
		final String endpoint = "foobar/";
		final SocializeObject object = new SocializeObject();
		
		final int arrayLength = 3;
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		JSONArray jsonArray = AndroidMock.createMock(JSONArray.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getListRequest(session, endpoint, key, ids)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseArray((InputStream)null)).andReturn(jsonArray);
		AndroidMock.expect(jsonArray.length()).andReturn(arrayLength);
		AndroidMock.expect(jsonArray.getJSONObject(0)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(1)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(2)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object).times(arrayLength);
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(objectFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(jsonArray);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		List<SocializeObject> list = provider.list(session, endpoint, key, ids);
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(jsonArray);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(objectFactory);
		AndroidMock.verify(utils);
		
		for (SocializeObject gotten : list) {
			assertSame(object,gotten);
		}
	}
	
	
	@UsesMocks({JSONArray.class})
	@SuppressWarnings("unchecked")
	public void testProviderPost() throws Exception {
		
		final String endpoint = "foobar/";
		final SocializeObject object = new SocializeObject();
		
		final int arrayLength = 3;
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		JSONArray jsonArray = AndroidMock.createMock(JSONArray.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getPostRequest(session, endpoint, object)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseArray((InputStream)null)).andReturn(jsonArray);
		AndroidMock.expect(jsonArray.length()).andReturn(arrayLength);
		AndroidMock.expect(jsonArray.getJSONObject(0)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(1)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(2)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object).times(arrayLength);
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(objectFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(jsonArray);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		List<SocializeObject> list = provider.post(session, endpoint, object);
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(jsonArray);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(objectFactory);
		AndroidMock.verify(utils);
		
		for (SocializeObject gotten : list) {
			assertSame(object,gotten);
		}
	}
	

	@UsesMocks({JSONArray.class})
	@SuppressWarnings("unchecked")
	public void testProviderPostCollection() throws Exception {
		
		final String endpoint = "foobar/";
		final SocializeObject object0 = new SocializeObject();
		final SocializeObject object1 = new SocializeObject();
		final List<SocializeObject> objects = new ArrayList<SocializeObject>();
		
		objects.add(object0);
		objects.add(object1);
		
		final int arrayLength = objects.size();
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		JSONArray jsonArray = AndroidMock.createMock(JSONArray.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getPostRequest(session, endpoint, objects)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseArray((InputStream)null)).andReturn(jsonArray);
		AndroidMock.expect(jsonArray.length()).andReturn(arrayLength);
		AndroidMock.expect(jsonArray.getJSONObject(0)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(1)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object0).once();
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object1).once();
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(objectFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(jsonArray);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		List<SocializeObject> list = provider.post(session, endpoint, objects);
		
		assertEquals(objects.size(), list.size());
		
		for (SocializeObject gotten : list) {
			assertTrue(objects.contains(gotten));
		}
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(jsonArray);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(objectFactory);
		AndroidMock.verify(utils);
	}
	
	@UsesMocks({JSONArray.class})
	@SuppressWarnings("unchecked")
	public void testProviderPut() throws Exception {
		
		final String endpoint = "foobar/";
		final SocializeObject object = new SocializeObject();
		
		final int arrayLength = 3;
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		JSONArray jsonArray = AndroidMock.createMock(JSONArray.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getPutRequest(session, endpoint, object)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseArray((InputStream)null)).andReturn(jsonArray);
		AndroidMock.expect(jsonArray.length()).andReturn(arrayLength);
		AndroidMock.expect(jsonArray.getJSONObject(0)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(1)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(2)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object).times(arrayLength);
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(objectFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(jsonArray);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		List<SocializeObject> list = provider.put(session, endpoint, object);
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(jsonArray);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(objectFactory);
		AndroidMock.verify(utils);
		
		for (SocializeObject gotten : list) {
			assertSame(object,gotten);
		}
	}
	
	@UsesMocks({JSONArray.class})
	@SuppressWarnings("unchecked")
	public void testProviderPutCollection() throws Exception {
		
		final String endpoint = "foobar/";
		final SocializeObject object0 = new SocializeObject();
		final SocializeObject object1 = new SocializeObject();
		final List<SocializeObject> objects = new ArrayList<SocializeObject>();
		
		objects.add(object0);
		objects.add(object1);
		
		final int arrayLength = objects.size();
		
		SocializeObjectFactory<SocializeObject> objectFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeRequestFactory<SocializeObject> requestFactory = AndroidMock.createMock(SocializeRequestFactory.class);
		SocializeObjectFactory<User> userFactory = AndroidMock.createMock(SocializeObjectFactory.class);
		SocializeSessionFactory sessionFactory = AndroidMock.createMock(SocializeSessionFactory.class);
		HttpClientFactory clientFactory = AndroidMock.createMock(HttpClientFactory.class);
		WritableSession session = AndroidMock.createMock(WritableSession.class);
		HttpClient client = AndroidMock.createMock(HttpClient.class);
		HttpUriRequest request = AndroidMock.createMock(HttpUriRequest.class);
		HttpResponse response = AndroidMock.createMock(HttpResponse.class);
		JSONParser jsonParser = AndroidMock.createMock(JSONParser.class);
		JSONObject json = AndroidMock.createMock(JSONObject.class);
		JSONArray jsonArray = AndroidMock.createMock(JSONArray.class);
		HttpEntity entity = AndroidMock.createMock(HttpEntity.class);
		HttpUtils utils = AndroidMock.createMock(HttpUtils.class);
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getPutRequest(session, endpoint, objects)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseArray((InputStream)null)).andReturn(jsonArray);
		AndroidMock.expect(jsonArray.length()).andReturn(arrayLength);
		AndroidMock.expect(jsonArray.getJSONObject(0)).andReturn(json);
		AndroidMock.expect(jsonArray.getJSONObject(1)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object0).once();
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object1).once();
		AndroidMock.expect(utils.isHttpError(response)).andReturn(false);
		
		entity.consumeContent();
		
		AndroidMock.replay(sessionFactory);
		AndroidMock.replay(clientFactory);
		AndroidMock.replay(objectFactory);
		AndroidMock.replay(requestFactory);
		AndroidMock.replay(jsonParser);
		AndroidMock.replay(client);
		AndroidMock.replay(json);
		AndroidMock.replay(jsonArray);
		AndroidMock.replay(userFactory);
		AndroidMock.replay(entity);
		AndroidMock.replay(response);
		AndroidMock.replay(utils);
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser,
				utils
		);
		
		List<SocializeObject> list = provider.put(session, endpoint, objects);
		
		assertEquals(objects.size(), list.size());
		
		for (SocializeObject gotten : list) {
			assertTrue(objects.contains(gotten));
		}
		
		AndroidMock.verify(sessionFactory);
		AndroidMock.verify(clientFactory);
		AndroidMock.verify(requestFactory);
		AndroidMock.verify(jsonParser);
		AndroidMock.verify(client);
		AndroidMock.verify(json);
		AndroidMock.verify(jsonArray);
		AndroidMock.verify(userFactory);
		AndroidMock.verify(entity);
		AndroidMock.verify(response);
		AndroidMock.verify(objectFactory);
		AndroidMock.verify(utils);
	}
	
}
