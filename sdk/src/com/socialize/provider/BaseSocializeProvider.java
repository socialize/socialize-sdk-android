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
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeRequestFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionFactory;
import com.socialize.api.SocializeSessionPersister;
import com.socialize.api.WritableSession;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderInfoFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.auth.facebook.FacebookAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.ActionError;
import com.socialize.entity.ErrorFactory;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.SocializeObjectFactory;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.net.HttpClientFactory;
import com.socialize.util.HttpUtils;
import com.socialize.util.IOUtils;
import com.socialize.util.JSONParser;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 * 
 * @param <T>
 */
public abstract class BaseSocializeProvider<T extends SocializeObject> implements SocializeProvider<T> {
	
	public static final String JSON_ATTR_ERRORS = "errors";
	public static final String JSON_ATTR_ITEMS = "items";
	public static final String JSON_ATTR_COUNT = "total_count";
	
	private SocializeObjectFactory<User> userFactory;
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	private ErrorFactory errorFactory;
	private HttpClientFactory clientFactory;
	private SocializeSessionFactory sessionFactory;
	private SocializeRequestFactory<T> requestFactory;
	private JSONParser jsonParser;
	private SocializeLogger logger;
	private HttpUtils httpUtils;
	private IOUtils ioUtils;
	private SocializeSessionPersister sessionPersister;
	private SocializeConfig config;
	private Context context;
	
	public BaseSocializeProvider() {
		super();
	}
	
	public void init(Context context) {
		this.context = context;
	}

	public void setUserFactory(SocializeObjectFactory<User> userFactory) {
		this.userFactory = userFactory;
	}

	public void setClientFactory(HttpClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	public void setRequestFactory(SocializeRequestFactory<T> requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void setJsonParser(JSONParser jsonParser) {
		this.jsonParser = jsonParser;
	}

	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	public void setIoUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setErrorFactory(ErrorFactory errorFactory) {
		this.errorFactory = errorFactory;
	}
	
	public void setSessionFactory(SocializeSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {
		AuthProviderData data = authProviderDataFactory.getBean();
		data.setAuthProviderInfo(authProviderInfoBuilder.getFactory(AuthProviderType.SOCIALIZE).getInstance());
		return authenticate(endpoint, key, secret, data, uuid);
	}

	@Override
	public WritableSession loadSession(String endpoint, String key, String secret) throws SocializeException {
		
		if(sessionPersister != null) {
			WritableSession loaded = sessionPersister.load(context);
			
			// Verify that the key/secret matches
			if(loaded != null) {
				
				String loadedKey = loaded.getConsumerKey();
				String loadedSecret = loaded.getConsumerSecret();
				String loadedHost = loaded.getHost();
				
				String host = config.getProperty(SocializeConfig.API_HOST);
				
				if(loadedKey != null && 
						loadedKey.equals(key) &&
						loadedSecret != null && 
						loadedSecret.equals(secret) &&
						loadedHost != null && 
						loadedHost.equals(host)) {
					
					return loaded;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean validateSession(SocializeSession session, AuthProviderData data) {
		AuthProviderInfo info = data.getAuthProviderInfo();
		if(info != null) {
			if(info.getType().equals(AuthProviderType.SOCIALIZE)) {
				return true;
			}
			return validateSessionAuthData(session, info);
		}
		else {	
			return validateSessionAuthDataLegacy(session, data);
		}		
	}

	public boolean validateSessionAuthData(SocializeSession loaded, AuthProviderInfo info) {
		UserProviderCredentialsMap userProviderCredentialsMap = loaded.getUserProviderCredentials();
		if(userProviderCredentialsMap != null) {
			UserProviderCredentials userProviderCredentials = userProviderCredentialsMap.get(info.getType());
			if(userProviderCredentials != null && userProviderCredentials.getAuthProviderInfo().matches(info)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean validateSessionAuthDataLegacy(SocializeSession loaded, AuthProviderData data) {
		
		if(data.getAuthProviderType().equals(AuthProviderType.SOCIALIZE)) {
			return true;
		}
		
		if(data.getAuthProviderType() != null && !StringUtils.isEmpty(data.getAppId3rdParty())) {
			AuthProviderType loadedAuthProviderType = loaded.getAuthProviderType();
			String loadedAppId3rdParty = loaded.get3rdPartyAppId();
			
			if(loadedAuthProviderType != null && 
					!StringUtils.isEmpty(loadedAppId3rdParty) && 
					loadedAuthProviderType.equals(data.getAuthProviderType()) && 
					loadedAppId3rdParty.equals(data.getAppId3rdParty())) {
				return true;
			}
		}
		
		return false;
	}

	@Deprecated
	@Override
	public SocializeSession loadSession(String endpoint, String key, String secret, AuthProviderType authProviderType, String appId3rdParty) throws SocializeException {
		if(authProviderType.equals(AuthProviderType.FACEBOOK)) {
			AuthProviderData data = newAuthProviderData();
			AuthProviderInfoFactory<FacebookAuthProviderInfo> factory = authProviderInfoBuilder.getFactory(authProviderType);
			FacebookAuthProviderInfo info = factory.getInstance();
			info.setAppId(appId3rdParty);
			data.setAuthProviderInfo(info);
			
			WritableSession session = loadSession(endpoint, key, secret);
			
			if(validateSession(session, data)) {
				return session;
			}
		}
		else {
			throw new UnsupportedOperationException("Method not supported for auth provider [" +
					authProviderType +
					"].");
		}
		
		return null;
	}
	
	@Deprecated
	protected FacebookAuthProviderInfo newFacebookAuthProviderInfo() {
		return new FacebookAuthProviderInfo();
	}
	
	// Mockable
	protected AuthProviderData newAuthProviderData() {
		return new AuthProviderData();
	}
	
	@Override
	public void clearSession(AuthProviderType type) {
		if(sessionPersister != null) {
			sessionPersister.delete(context, type);
		}
	}

	@Override
	public void clearSession() {
		if(sessionPersister != null) {
			sessionPersister.delete(context);
		}
	}

	public void saveSession(SocializeSession session) {
		if(sessionPersister != null) {
			sessionPersister.save(context, session);
		}
	}

	@Override
	public SocializeSession authenticate(String endpoint, String key, String secret, AuthProviderData data, String uuid) throws SocializeException {
		
		WritableSession session = loadSession(endpoint, key, secret);
		
		if(session != null) {
			
			if(validateSession(session, data)) {
				return session;
			}
			else {
				AuthProviderInfo info = data.getAuthProviderInfo();
				
				if(info != null) {
					DefaultUserProviderCredentials userProviderCredentials = new DefaultUserProviderCredentials();
					userProviderCredentials.setAccessToken(data.getToken3rdParty());
					userProviderCredentials.setTokenSecret(data.getSecret3rdParty());
					userProviderCredentials.setUserId(data.getUserId3rdParty());
					userProviderCredentials.setAuthProviderInfo(data.getAuthProviderInfo());
					
					session.getUserProviderCredentials().put(info.getType(), userProviderCredentials);
				}
				else {
					// Legacy
					session = null;
				}
			}
		}
		
		if(session == null) {
			session = sessionFactory.create(key, secret, data);
		}
		
		endpoint = prepareEndpoint(session, endpoint, true);
		
		if(!clientFactory.isDestroyed()) {

			HttpClient client = clientFactory.getClient();
			
			HttpEntity entity = null;
			
			try {
				HttpUriRequest request = requestFactory.getAuthRequest(session, endpoint, uuid, data);
				
				HttpResponse response = client.execute(request);
				
				entity = response.getEntity();
				
				if(httpUtils.isHttpError(response)) {
					
					if(sessionPersister != null && httpUtils.isAuthError(response)) {
						sessionPersister.delete(context);
					}
					
					String msg = ioUtils.readSafe(entity.getContent());
					
					throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode(), msg);
				}
				else {
					
					JSONObject json = jsonParser.parseObject(entity.getContent());
					
					User user = userFactory.fromJSON(json.getJSONObject("user"));

					session.setConsumerToken(json.getString("oauth_token"));
					session.setConsumerTokenSecret(json.getString("oauth_token_secret"));
					session.setUser(user);
					
					saveSession(session);
				}
			}
			catch (Exception e) {
				throw SocializeException.wrap(e);
			}
			finally {
				closeEntity(entity);
			}
		}
		else {
			if(logger != null) {
				logger.warn("Attempt to access HttpClientFactory that was already destroyed");
			}
		}
		
		
		return session;
	}

	@Override
	public T get(SocializeSession session, String endpoint, String id, ActionType type) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest get = requestFactory.getGetRequest(session, endpoint, id);
		return doGetTypeRequest(get, type);
	}
	
	@Override
	public T get(SocializeSession session, String endpoint, String id) throws SocializeException {
		return get(session, endpoint, id, ActionType.UNKNOWN);
	}

	@Override
	public void delete(SocializeSession session, String endpoint, String id) throws SocializeException {
		HttpEntity entity = null;
		
		if(!clientFactory.isDestroyed()) {	
			try {
				endpoint = prepareEndpoint(session, endpoint);
				
				HttpClient client = clientFactory.getClient();
				
				HttpUriRequest del = requestFactory.getDeleteRequest(session, endpoint, id);
				
				HttpResponse response = client.execute(del);
				
				entity = response.getEntity();
				
				if(httpUtils.isHttpError(response)) {
					
					if(sessionPersister != null && httpUtils.isAuthError(response)) {
						sessionPersister.delete(context);
					}
					
					String msg = ioUtils.readSafe(entity.getContent());
					throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode(), msg);
				}
			}
			catch (Exception e) {
				throw SocializeException.wrap(e);
			}
			finally {
				closeEntity(entity);
			}
		}
		else {
			if(logger != null) {
				logger.warn("Attempt to access HttpClientFactory that was already destroyed");
			}
		}
	}
	
	@Override
	public ListResult<T> list(SocializeSession session, String endpoint, int startIndex, int endIndex) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getListRequest(session, endpoint, startIndex, endIndex);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}

	@Override
	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, String idKey, int startIndex, int endIndex) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getListRequest(session, endpoint, key, ids, idKey, startIndex, endIndex);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}

	@Override
	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getListRequest(session, endpoint, key, ids, startIndex, endIndex);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}

	@Override
	public ListResult<T> put(SocializeSession session, String endpoint, T object) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getPutRequest(session, endpoint, object);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}

	@Override
	public ListResult<T> put(SocializeSession session, String endpoint, Collection<T> objects) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getPutRequest(session, endpoint, objects);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}
	
	@Override
	public ListResult<T> post(SocializeSession session, String endpoint, T object) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getPostRequest(session, endpoint, object);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}

	@Override
	public T putAsPost(SocializeSession session, String endpoint, T object) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getPostRequest(session, endpoint, object);
		return doGetTypeRequest(request, ActionType.UNKNOWN);
	}

	@Override
	public ListResult<T> post(SocializeSession session, String endpoint, Collection<T> objects) throws SocializeException {
		endpoint = prepareEndpoint(session, endpoint);
		HttpUriRequest request = requestFactory.getPostRequest(session, endpoint, objects);
		return doListTypeRequest(request, ActionType.UNKNOWN);
	}
	
	private T doGetTypeRequest(HttpUriRequest request, ActionType actionType) throws SocializeException {
		HttpEntity entity = null;
		
		if(!clientFactory.isDestroyed()) {	
			
			try {
				
				HttpClient client = clientFactory.getClient();
				
				HttpResponse response = client.execute(request);
				
				entity = response.getEntity();
				
				if(httpUtils.isHttpError(response)) {
					
					if(sessionPersister != null && httpUtils.isAuthError(response)) {
						sessionPersister.delete(context);
					}
					
					String msg = ioUtils.readSafe(entity.getContent());
					throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode(), msg);
				}
				else {
					JSONObject json = jsonParser.parseObject(entity.getContent());
					
					return fromJSON(json, actionType);
				}
			}
			catch (Exception e) {
				throw SocializeException.wrap(e);
			}
			finally {
				closeEntity(entity);
			}
		}
		else {
			if(logger != null) {
				logger.warn("Attempt to access HttpClientFactory that was already destroyed");
			}
			
			return null;
		}
	}

	private ListResult<T> doListTypeRequest(HttpUriRequest request, ActionType type) throws SocializeException {
		List<T> results = null;
		List<ActionError> errors = null;
		HttpEntity entity = null;
		
		ListResult<T> result = null;
		
		if(!clientFactory.isDestroyed()) {	

			try {
				HttpClient client = clientFactory.getClient();
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Request: " + request.getMethod() + " " + request.getRequestLine().getUri());
				}

				HttpResponse response = client.execute(request);
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Response: " + response.getStatusLine().getStatusCode());
				}
				
				entity = response.getEntity();
				
				if(httpUtils.isHttpError(response)) {
					
					if(sessionPersister != null && httpUtils.isAuthError(response)) {
						sessionPersister.delete(context);
					}
					
					String msg = ioUtils.readSafe(entity.getContent());
					throw new SocializeApiError(httpUtils, response.getStatusLine().getStatusCode(), msg);
				}
				else {
					
					result = new ListResult<T>();
					
					// Read the json just for logging
					String json = ioUtils.readSafe(entity.getContent());
					
					if(logger != null && logger.isDebugEnabled()) {
						logger.debug("JSON Response: " + json);
					}
					
					JSONObject object = jsonParser.parseObject(json);
					
					if(object.has(JSON_ATTR_ERRORS) && !object.isNull(JSON_ATTR_ERRORS)) {
						
						JSONArray errorList = object.getJSONArray(JSON_ATTR_ERRORS);
						
						int length = errorList.length();
						
						errors = new ArrayList<ActionError>(length);
						
						for (int i = 0; i < length; i++) {
							JSONObject jsonObject = errorList.getJSONObject(i);
							ActionError error = errorFactory.fromJSON(jsonObject);
							errors.add(error);
						}
						
						result.setErrors(errors);
					}
					
					if(object.has(JSON_ATTR_ITEMS) && !object.isNull(JSON_ATTR_ITEMS)) {
						JSONArray list = object.getJSONArray(JSON_ATTR_ITEMS);
						
						int length = list.length();
						
						results = new ArrayList<T>(length);
						
						for (int i = 0; i < length; i++) {
							results.add(fromJSON(list.getJSONObject(i), type));
						}
						
						result.setItems(results);
					}
					
					if(object.has(JSON_ATTR_COUNT) && !object.isNull(JSON_ATTR_COUNT)) {
						result.setTotalCount(object.getInt(JSON_ATTR_COUNT));
					}
				}
			}
			catch (Throwable e) {
				throw SocializeException.wrap(e);
			}
			finally {
				closeEntity(entity);
			}
			
			return result;
		}
		else {
			if(logger != null) {
				logger.warn("Attempt to access HttpClientFactory that was already destroyed");
			}
			
			return null;
		}		
	}


	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setSessionPersister(SocializeSessionPersister sessionPersister) {
		this.sessionPersister = sessionPersister;
	}
	
	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}

	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}

	private final String prepareEndpoint(SocializeSession session, String endpoint) {
		return prepareEndpoint(session, endpoint, false);
	}

	private final String prepareEndpoint(SocializeSession session, String endpoint, boolean secure) {
		return prepareEndpoint(session.getHost(), endpoint, secure);
	}
	
	private final String prepareEndpoint(String host, String endpoint, boolean secure) {
		endpoint = endpoint.trim();
		
		if(host == null) {
			logger.warn("The session did not have an endpoint configured, using the config");
			host = config.getProperty(SocializeConfig.API_HOST);
		}
		
		if(host != null) {
			
			if(!host.startsWith("http")) {
				if(secure) {
					host = "https://" + host;
				}
				else {
					host = "http://" + host;
				}
			}
			
			if(!host.endsWith("/")) {
				if(!endpoint.startsWith("/")) {
					host += "/";	
				}
			}
			else if (endpoint.startsWith("/")) {
				endpoint = endpoint.substring(1, endpoint.length());
			}
				
			endpoint = host + endpoint;
			
		}
		else {
			logger.error("Could not locate host property in session or config!");
		}

		
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

	public SocializeSessionPersister getSessionPersister() {
		return sessionPersister;
	}
	
	
	public abstract T fromJSON(JSONObject json, ActionType type) throws JSONException;
}
