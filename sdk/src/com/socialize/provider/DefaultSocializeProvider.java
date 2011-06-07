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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import com.socialize.api.SocializeRequestFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.api.WritableSession;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.User;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.net.HttpClientFactory;
import com.socialize.util.HttpUtils;
import com.socialize.util.JSONParser;

/**
 * @author Jason Polites
 * 
 * @param <T>
 */
public class DefaultSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {

	private SocializeObjectFactory<T> objectFactory;
	private SocializeObjectFactory<User> userFactory;
	private HttpClientFactory clientFactory;
	private SocializeSessionFactory sessionFactory;
	private SocializeRequestFactory<T> requestFactory;
	private JSONParser jsonParser;
	private SocializeLogger logger;
	private HttpUtils httpUtils;

	public DefaultSocializeProvider(
			SocializeObjectFactory<T> objectFactory, 
			SocializeObjectFactory<User> userFactory,
			HttpClientFactory clientFactory,
			SocializeSessionFactory sessionFactory,
			SocializeRequestFactory<T> requestFactory,
			JSONParser jsonParser,
			HttpUtils httpUtils) {
		
		super();
		this.objectFactory = objectFactory;
		this.clientFactory = clientFactory;
		this.userFactory = userFactory;
		this.sessionFactory = sessionFactory;
		this.requestFactory = requestFactory;
		this.jsonParser = jsonParser;
		this.httpUtils = httpUtils;
	}
	
	@Override
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {

		WritableSession session = sessionFactory.create(key, secret);
		
		endpoint = prepareEndpoint(endpoint);
		
		HttpClient client = clientFactory.getClient();
		
		HttpEntity entity = null;
		
		try {
			HttpUriRequest request = requestFactory.getAuthRequest(session, endpoint, uuid);
			
			HttpResponse response = client.execute(request);
			
			if(httpUtils.isHttpError(response)) {
				throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode());
			}
			else {
				entity = response.getEntity();
				
				JSONObject json = jsonParser.parseObject(entity.getContent());
				
				User user = userFactory.fromJSON(json.getJSONObject("user"));
				
				session.setConsumerToken(json.getString("oauth_token"));
				session.setConsumerTokenSecret(json.getString("oauth_token_secret"));
				session.setUser(user);
			}
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
		finally {
			closeEntity(entity);
		}
		
		return session;
	}

	@Override
	public T get(SocializeSession session, String endpoint, String id) throws SocializeException {
		
		HttpEntity entity = null;
		
		try {
			endpoint = prepareEndpoint(endpoint);
			
			HttpClient client = clientFactory.getClient();
			
			HttpUriRequest get = requestFactory.getGetRequest(session, endpoint, id);
			
			HttpResponse response = client.execute(get);
			
			if(httpUtils.isHttpError(response)) {
				throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode());
			}
			else {
				entity = response.getEntity();

				JSONObject json = jsonParser.parseObject(entity.getContent());
				
				return objectFactory.fromJSON(json);
			}
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
		finally {
			closeEntity(entity);
		}
	}

	@Override
	public List<T> list(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {
		endpoint = prepareEndpoint(endpoint);
		HttpUriRequest request = requestFactory.getListRequest(session, endpoint, key, ids);
		return doListTypeRequest(request);
	}

	@Override
	public List<T> put(SocializeSession session, String endpoint, T object) throws SocializeException {
		endpoint = prepareEndpoint(endpoint);
		HttpUriRequest request = requestFactory.getPutRequest(session, endpoint, object);
		return doListTypeRequest(request);
	}

	@Override
	public List<T> post(SocializeSession session, String endpoint, T object) throws SocializeException {
		endpoint = prepareEndpoint(endpoint);
		HttpUriRequest request = requestFactory.getPostRequest(session, endpoint, object);
		return doListTypeRequest(request);
	}
	
	private List<T> doListTypeRequest(HttpUriRequest request) throws SocializeException {
		List<T> results = null;
		HttpEntity entity = null;
		
		try {
			HttpClient client = clientFactory.getClient();

			HttpResponse response = client.execute(request);
			
			if(httpUtils.isHttpError(response)) {
				throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode());
			}
			else {
				entity = response.getEntity();
				
				JSONArray list = jsonParser.parseArray(entity.getContent());
				
				int length = list.length();
				
				results = new ArrayList<T>(length);
				
				for (int i = 0; i < length; i++) {
					results.add(objectFactory.fromJSON(list.getJSONObject(i)));
				}
			}
		}
		catch (Exception e) {
			throw new SocializeException(e);
		}
		finally {
			closeEntity(entity);
		}
		
		return results;
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
	
	public SocializeObjectFactory<User> getUserFactory() {
		return userFactory;
	}
	
	public void setUserFactory(SocializeObjectFactory<User> userFactory) {
		this.userFactory = userFactory;
	}

	public SocializeSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SocializeSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public JSONParser getJsonParser() {
		return jsonParser;
	}

	public void setJsonParser(JSONParser jsonParser) {
		this.jsonParser = jsonParser;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public HttpUtils getHttpUtils() {
		return httpUtils;
	}

	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	private final String prepareEndpoint(String endpoint) {
		endpoint = endpoint.trim();
		if(!endpoint.endsWith("/")) {
			endpoint += "/";
		}
		return endpoint;
	}


	private final void closeEntity(HttpEntity entity) {
		if(entity != null) {
			try {
				entity.consumeContent();
			}
			catch (IOException e) {
				if(logger != null) {
					logger.warn("Failed to fully consume http response content", e);
				}
			}
		}
	}
}
