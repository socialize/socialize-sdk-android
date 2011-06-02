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
package com.socialize.test;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
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
	HttpEntity.class
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
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser
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
		
		AndroidMock.expect(clientFactory.getClient()).andReturn(client);
		AndroidMock.expect(requestFactory.getGetRequest(session, endpoint, id)).andReturn(request);
		AndroidMock.expect(client.execute(request)).andReturn(response);
		AndroidMock.expect(response.getEntity()).andReturn(entity);
		AndroidMock.expect(entity.getContent()).andReturn(null);
		AndroidMock.expect(jsonParser.parseObject((InputStream)null)).andReturn(json);
		AndroidMock.expect(objectFactory.fromJSON(json)).andReturn(object);
		
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
		
		DefaultSocializeProvider<SocializeObject> provider = new DefaultSocializeProvider<SocializeObject>(
				objectFactory,
				userFactory,
				clientFactory,
				sessionFactory,
				requestFactory,
				jsonParser
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
		
		assertSame(object,gotten);
	}
	
}
