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
package com.socialize.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionImpl;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.User;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.error.SocializeException;
import com.socialize.net.HttpClientFactory;
import com.socialize.util.IOUtils;

/**
 * @author Jason Polites
 * 
 * @param <T>
 */
public class DefaultSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {

	private SocializeObjectFactory<T> objectFactory;
	private SocializeObjectFactory<User> userFactory;
	private HttpClientFactory clientFactory;
	private IOUtils ioUtils;
	
	private final AuthorizationHeaderSigningStrategy strategy = new AuthorizationHeaderSigningStrategy();

	public DefaultSocializeProvider() {
		super();
	}

	public DefaultSocializeProvider(SocializeObjectFactory<T> factory, HttpClientFactory clientFactory) {
		super();
		this.objectFactory = factory;
		this.clientFactory = clientFactory;
	}

	protected void sign(SocializeSession session, HttpUriRequest request) throws SocializeException {
		try {
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer(session.getConsumerKey(), session.getConsumerSecret());
			consumer.setSigningStrategy(strategy);
			consumer.setTokenWithSecret(session.getConsumerToken(), session.getConsumerTokenSecret());
			consumer.sign(request);
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
	}
	
	@Override
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {

		SocializeSessionImpl session = null;
		
		endpoint = prepareEndpoint(endpoint);
		
		HttpClient client = clientFactory.getClient();
		HttpPost post = new HttpPost(endpoint);
		
		List<NameValuePair> data = new ArrayList<NameValuePair>(2);
		
		data.add(new BasicNameValuePair("payload", "{'udid':" + uuid + "}"));
		data.add(new BasicNameValuePair("udid", uuid)); // Legacy
		
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
			
			OAuthConsumer consumer = new CommonsHttpOAuthConsumer(key, secret);

			// sign the request
			consumer.setSigningStrategy(strategy);
			
			consumer.sign(post);
			
			HttpResponse response = client.execute(post);
			
			String result = ioUtils.read(response.getEntity().getContent());
			
			JSONObject json = new JSONObject(result);
			
			// Parse the response.
			session = new SocializeSessionImpl();
			
			session.setConsumerKey(key);
			session.setConsumerSecret(secret);
			session.setConsumerToken(json.getString("oauth_token"));
			session.setConsumerTokenSecret(json.getString("oauth_token_secret"));
			
			User user = userFactory.fromJSON(json.getJSONObject("user"));
			
			session.setUser(user);
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
		
		return session;
	}

	@Override
	public T get(SocializeSession session, String endpoint, String id) throws SocializeException {
		
		try {
			endpoint = prepareEndpoint(endpoint);
			endpoint += id;
			
			HttpClient client = clientFactory.getClient();
			HttpGet get = new HttpGet(endpoint);
			
			sign(session, get);
			
			HttpResponse response = client.execute(get);

			String result = ioUtils.read(response.getEntity().getContent());
			
			JSONObject json = new JSONObject(result);
			
			return objectFactory.fromJSON(json);
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
	}

	@Override
	public List<T> list(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {

		List<T> results = null;
		
		try {
			endpoint = prepareEndpoint(endpoint);
			
			HttpClient client = clientFactory.getClient();
			HttpPost post = new HttpPost(endpoint);
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			
			JSONArray array = new JSONArray(Arrays.asList(ids));
			
			JSONObject json = new JSONObject();
			json.put("ids", array);
			json.put("key", key);
			
			data.add(new BasicNameValuePair("payload", json.toString()));
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
			
			sign(session, post);
			
			HttpResponse response = client.execute(post);
			
			String result = ioUtils.read(response.getEntity().getContent());
			
			JSONArray list = new JSONArray(result);
			
			int length = list.length();
			
			results = new ArrayList<T>(length);
			
			for (int i = 0; i < length; i++) {
				results.add(objectFactory.fromJSON(list.getJSONObject(i)));
			}
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}

		return results;
	}

	@Override
	public List<T> put(SocializeSession session, String endpoint, T object) {
		// TODO: Complete
//		endpoint = prepareEndpoint(endpoint);
		
		
		
		return null;
	}

	@Override
	public List<T> post(SocializeSession session, String endpoint, T object) {
		// TODO: Complete
//		endpoint = prepareEndpoint(endpoint);
		
		return null;
	}

	public SocializeObjectFactory<T> getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(SocializeObjectFactory<T> factory) {
		this.objectFactory = factory;
	}

	public HttpClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(HttpClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public IOUtils getIoUtils() {
		return ioUtils;
	}

	public void setIoUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}
	
	public SocializeObjectFactory<User> getUserFactory() {
		return userFactory;
	}
	
	public void setUserFactory(SocializeObjectFactory<User> userFactory) {
		this.userFactory = userFactory;
	}
	
	private final String prepareEndpoint(String endpoint) {
		endpoint = endpoint.trim();
		if(!endpoint.endsWith("/")) {
			endpoint += "/";
		}
		return endpoint;
	}
	
	
}
