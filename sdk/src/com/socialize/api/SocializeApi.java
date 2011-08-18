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
package com.socialize.api;

import java.util.List;

import android.os.AsyncTask;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.ActionError;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.provider.SocializeProvider;
import com.socialize.util.HttpUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 * @param <T>
 * @param <P>
 */
public class SocializeApi<T extends SocializeObject, P extends SocializeProvider<T>> {

	private P provider;
	private SocializeResponseFactory<T> responseFactory;
	private IBeanFactory<AuthProviderData> authProviderDataFactory;
	private SocializeConfig config;
	private AuthProviders authProviders;
	private SocializeLogger logger;
	private HttpUtils httpUtils;
	
	public static enum RequestType {AUTH,PUT,POST,GET,LIST,DELETE};
	
	public SocializeApi(P provider) {
		super();
		this.provider = provider;
	}
	
	public void clearSession() {
		provider.clearSession();
	}
	
	public SocializeSession loadSession(String endpoint, String key, String secret, AuthProviderData data) throws SocializeException {
		return provider.loadSession(endpoint, key, secret, data);
	}
	
	@Deprecated
	public SocializeSession loadSession(String endpoint, String key, String secret, AuthProviderType authProviderType, String appId3rdParty) throws SocializeException {
		return provider.loadSession(endpoint, key, secret, authProviderType, appId3rdParty);
	}
	
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {
		return provider.authenticate(endpoint, key, secret, uuid);
	}
	
	public SocializeSession authenticate(String endpoint, String key, String secret, AuthProviderData data, String uuid) throws SocializeException {
		return provider.authenticate(endpoint, key, secret, data, uuid);
	}
	
	@Deprecated
	public SocializeSession authenticate(String endpoint, String key, String secret, String userId3rdParty, String token3rdParty, String appId3rdParty, AuthProviderType authProviderType, String uuid) throws SocializeException {
		return provider.authenticate(endpoint, key, secret, userId3rdParty, token3rdParty, appId3rdParty, authProviderType, uuid);
	}

	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {
		return provider.list(session, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS);
	}
	
	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex) throws SocializeException {
		return provider.list(session, endpoint, key, ids, startIndex, endIndex);
	}
	
	public T get(SocializeSession session, String endpoint, String id) throws SocializeException {
		return provider.get(session, endpoint, id);
	}
	
	public void delete(SocializeSession session, String endpoint, String id) throws SocializeException {
		provider.delete(session, endpoint, id);
	}
	
	public ListResult<T> put(SocializeSession session, String endpoint, T object) throws SocializeException {
		return provider.put(session, endpoint, object);
	}

	public ListResult<T> put(SocializeSession session, String endpoint, List<T> objects) throws SocializeException {
		return provider.put(session, endpoint, objects);
	}

	public ListResult<T> post(SocializeSession session, String endpoint, T object) throws SocializeException {
		return provider.post(session, endpoint, object);
	}
	
	public ListResult<T> post(SocializeSession session, String endpoint, List<T> objects) throws SocializeException {
		return provider.post(session, endpoint, objects);
	}
	

	public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.LIST, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setKey(key);
		request.setIds(ids);
		request.setStartIndex(startIndex);
		request.setEndIndex(endIndex);
		getter.execute(request);
	}
	
	public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
		listAsync(session, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS, listener);
	}

	public void getAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.GET, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setIds(id);
		getter.execute(request);
	}
	
	public void deleteAsync(SocializeSession session, String endpoint, String id, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.DELETE, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setIds(id);
		getter.execute(request);
	}

	@SuppressWarnings("unchecked")
	public void putAsync(SocializeSession session, String endpoint, T object, SocializeActionListener listener) {
		AsyncPutter poster = new AsyncPutter(RequestType.PUT, session, listener);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObject(object);
		poster.execute(request);
	}
	
	@SuppressWarnings("unchecked")
	public void putAsync(SocializeSession session, String endpoint, List<T> objects, SocializeActionListener listener) {
		AsyncPutter poster = new AsyncPutter(RequestType.PUT, session, listener);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObjects(objects);
		poster.execute(request);
	}
	
	@SuppressWarnings("unchecked")
	public void postAsync(SocializeSession session, String endpoint, T object, SocializeActionListener listener) {
		AsyncPutter poster = new AsyncPutter(RequestType.POST, session, listener);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObject(object);
		poster.execute(request);
	}

	@SuppressWarnings("unchecked")
	public void postAsync(SocializeSession session, String endpoint, List<T> objects, SocializeActionListener listener) {
		AsyncPutter poster = new AsyncPutter(RequestType.POST, session, listener);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObjects(objects);
		poster.execute(request);
	}

	public void authenticateAsync(String key, String secret, String uuid, final SocializeAuthListener listener, final SocializeSessionConsumer sessionConsumer) {
		authenticateAsync(key, secret, uuid, new AuthProviderData(), listener, sessionConsumer, false);
	}
	
	public void authenticateAsync(
			String key, 
			String secret, 
			String uuid, 
			final AuthProviderData data,
			final SocializeAuthListener listener, 
			final SocializeSessionConsumer sessionConsumer, 
			boolean do3rdPartyAuth) {
	

		SocializeActionListener wrapper = null;
		
		if(listener != null) {
			wrapper = new SocializeActionListener() {
				
				@Override
				public void onResult(RequestType type, SocializeResponse response) {
					SocializeAuthResponse authResponse = (SocializeAuthResponse) response;
					if(sessionConsumer != null) {
						sessionConsumer.setSession(authResponse.getSession());
					}
					listener.onAuthSuccess(authResponse.getSession());
				}
				
				@Override
				public void onError(SocializeException error) {
					if(httpUtils != null && httpUtils.isAuthError(error)) {
						listener.onAuthFail(error);
					}
					else {
						listener.onError(error);
					}
				}
			};
		}

		final SocializeAuthRequest request = new SocializeAuthRequest();
		
		request.setEndpoint("/authenticate/");
		request.setConsumerKey(key);
		request.setConsumerSecret(secret);
		request.setUdid(uuid);
		request.setAuthProviderData(data);
		
		AuthProviderType authProviderType = data.getAuthProviderType();
		
		if(do3rdPartyAuth && !authProviderType.equals(AuthProviderType.SOCIALIZE)) {
			handle3rdPartyAuth(request, wrapper, listener, key, secret);
		}
		else {
			// Do normal auth
			handleRegularAuth(request, wrapper);
		}
	}
	
	
	@Deprecated
	public void authenticateAsync(
			String key, 
			String secret, 
			String uuid, 
			String authUserId3rdParty, 
			String authToken3rdParty, 
			final AuthProviderType authProviderType, 
			final String appId3rdParty,
			final SocializeAuthListener listener, 
			final SocializeSessionConsumer sessionConsumer, 
			boolean do3rdPartyAuth) {
		
		
		AuthProviderData data = authProviderDataFactory.getBean();
		data.setAppId3rdParty(appId3rdParty);
		data.setUserId3rdParty(authUserId3rdParty);
		data.setToken3rdParty(authToken3rdParty);
		data.setAuthProviderType(authProviderType);
		
		authenticateAsync(key, secret, uuid, data, listener, sessionConsumer, do3rdPartyAuth);
	}
	
	protected void handleRegularAuth(SocializeAuthRequest request, SocializeActionListener wrapper) {
		AsyncAuthenicator authenicator = new AsyncAuthenicator(RequestType.AUTH, null, wrapper);
		authenicator.execute(request);
	}
	
	protected void handle3rdPartyAuth(
			final SocializeAuthRequest request,
			final SocializeActionListener fWrapper,
			final SocializeAuthListener listener, 
			String key, String secret)  {

		final AuthProviderData authProviderData = request.getAuthProviderData();
		
		// Try loading the session first
		SocializeSession session = null;
		
		AuthProviderType authProviderType = authProviderData.getAuthProviderType();
		String appId3rdParty = authProviderData.getAppId3rdParty();
		
		// TODO: externalize this into a validator
		if(authProviderType != null && 
				authProviderType.equals(AuthProviderType.FACEBOOK) && 
				StringUtils.isEmpty(appId3rdParty)) {
			if(listener != null) {
				listener.onError(new SocializeException("No app ID found for auth type FACEBOOK"));
			}
			
			return;
		}
		
		try {
			session = loadSession(request.getEndpoint(), key, secret, authProviderData);
		}
		catch (SocializeException e) {
			// No need to throw this, just log it
			logger.warn("Failed to load saved session data", e);
		}
					
		if(session == null) {
			// Get the provider for the type
			AuthProvider authProvider = authProviders.getProvider(authProviderType);
			
			if(authProvider != null) {
				authProvider.authenticate(request, appId3rdParty, new AuthProviderListener() {
					
					@Override
					public void onError(SocializeException error) {
						if(listener != null) {
							listener.onError(error);
						}
					}
					
					@Override
					public void onAuthSuccess(AuthProviderResponse response) {
						
						authProviderData.setUserId3rdParty(response.getUserId());
						authProviderData.setToken3rdParty(response.getToken());
						authProviderData.setUserFirstName(response.getFirstName());
						authProviderData.setUserLastName(response.getLastName());
						authProviderData.setUserProfilePicData(response.getImageData());
						
						// Do normal auth
						handleRegularAuth(request, fWrapper);
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						if(listener != null) {
							listener.onAuthFail(error);
						}
					}
				});
			}
			else {
				logger.error("No provider found for auth type [" +
						authProviderType.getName() +
						"]");
			}
		}
		else {
			// We already have a session, just call the listener
			if(listener != null) {
				listener.onAuthSuccess(session);
			}
		}
	}

	@Deprecated
	protected void handle3rdPartyAuth(
			final SocializeAuthRequest request,
			final String authUserId3rdParty, 
			final String authToken3rdParty,
			final String appId3rdParty,
			final AuthProviderType authProviderType,
			final SocializeActionListener fWrapper,
			final SocializeAuthListener listener, 
			String key, String secret)  {
		
		AuthProviderData data = authProviderDataFactory.getBean();
		
		data.setAppId3rdParty(appId3rdParty);
		data.setUserId3rdParty(authUserId3rdParty);
		data.setToken3rdParty(authToken3rdParty);
		data.setAuthProviderType(authProviderType);
		
		request.setAuthProviderData(data);
		
		handle3rdPartyAuth(request, fWrapper, listener, key, secret);
	}
	
	public void setResponseFactory(SocializeResponseFactory<T> responseFactory) {
		this.responseFactory = responseFactory;
	}
	
	public SocializeResponseFactory<T> getResponseFactory() {
		return responseFactory;
	}
	
	public SocializeConfig getConfig() {
		return config;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public AuthProviders getAuthProviders() {
		return authProviders;
	}

	public void setAuthProviders(AuthProviders authProviders) {
		this.authProviders = authProviders;
	}
	
	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public IBeanFactory<AuthProviderData> getAuthProviderDataFactory() {
		return authProviderDataFactory;
	}

	public void setAuthProviderDataFactory(IBeanFactory<AuthProviderData> authProviderDataFactory) {
		this.authProviderDataFactory = authProviderDataFactory;
	}

	public HttpUtils getHttpUtils() {
		return httpUtils;
	}

	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	abstract class AbstractAsyncProcess<Params, Progress, Result extends SocializeResponse> extends AsyncTask<Params, Progress, Result> {

		RequestType requestType;
		SocializeSession session;
		SocializeException error = null;
		SocializeActionListener listener = null;
		
		public AbstractAsyncProcess(RequestType requestType, SocializeSession session, SocializeActionListener listener) {
			super();
			this.requestType = requestType;
			this.session = session;
			this.listener = listener;
		}

		@Override
		protected final Result doInBackground(Params... params) {
			Params request = params[0];
			Result result = null;
			try {
				result = doInBackground(request);
			}
			catch (SocializeException error) {
				this.error = error;
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Result result) {
			if(listener != null) {
				if(error != null) {
					listener.onError(error);
				}
				else {
					listener.onResult(requestType, result);
				}
			}
		}

		protected abstract Result doInBackground(Params param) throws SocializeException;
	}
	
	class AsyncAuthenicator extends AbstractAsyncProcess<SocializeAuthRequest, Void, SocializeAuthResponse> {

		public AsyncAuthenicator(RequestType requestType, SocializeSession session, SocializeActionListener listener) {
			super(requestType, session, listener);
		}

		@Override
		protected SocializeAuthResponse doInBackground(final SocializeAuthRequest request) throws SocializeException {
			SocializeAuthResponse response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newAuthResponse();
			}
			else {
				response = new SocializeAuthResponse();
			}
			
			SocializeSession session = null;
			
			AuthProviderData authProviderData = request.getAuthProviderData();
			AuthProviderType authProviderType = authProviderData.getAuthProviderType();
			
			if(authProviderType == null || authProviderType.equals(AuthProviderType.SOCIALIZE)) {
				session = SocializeApi.this.authenticate(request.getEndpoint(), request.getConsumerKey(), request.getConsumerSecret(), request.getUdid());
			}
			else {
				session = SocializeApi.this.authenticate(request.getEndpoint(), request.getConsumerKey(), request.getConsumerSecret(), authProviderData, request.getUdid());
//				session = SocializeApi.this.authenticate(
//						request.getEndpoint(), 
//						request.getConsumerKey(), 
//						request.getConsumerSecret(),
//						request.getAuthUserId3rdParty(), 
//						request.getAuthToken3rdParty(), 
//						request.getAppId3rdParty(),
//						request.getAuthProviderType(), 
//						request.getUdid());
			}
			
			response.setSession(session);
			return response;
		}
	}

	class AsyncPutter extends AbstractAsyncProcess<SocializePutRequest<T>, Void, SocializeEntityResponse<T>> {

		public AsyncPutter(RequestType requestType, SocializeSession session, SocializeActionListener listener) {
			super(requestType, session, listener);
		}

		@Override
		protected SocializeEntityResponse<T> doInBackground(SocializePutRequest<T> request) throws SocializeException {

			SocializeEntityResponse<T> response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newEntityResponse();
			}
			else {
				response = new SocializeEntityResponse<T>();
			}

			ListResult<T> results = null;

			switch (requestType) {
			
			case POST:
				
				if(request.getObjects() != null) {
					results = SocializeApi.this.post(session, request.getEndpoint(), request.getObjects());
				}
				else if(request.getObject() != null) {
					results = SocializeApi.this.post(session, request.getEndpoint(), request.getObject());
				}
				
				break;

			case PUT:
				
				if(request.getObjects() != null) {
					results = SocializeApi.this.put(session, request.getEndpoint(), request.getObjects());
				}
				else if(request.getObject() != null) {
					results = SocializeApi.this.put(session, request.getEndpoint(), request.getObject());
				}
				
				break;
			}

			response.setResults(results);

			return response;
		}

		@Override
		protected void onPostExecute(SocializeEntityResponse<T> result) {
			if(listener != null) {
				if(error != null) {
					listener.onError(error);
				}
				else {
					ListResult<T> results = result.getResults();
					
					if(results != null) {
						List<T> items = results.getItems();
						List<ActionError> errors = results.getErrors();
						
						if(items == null || items.size() == 0){
							if(errors != null && errors.size() > 0) {
								listener.onError(new SocializeException(errors.get(0).getMessage()));
							}
							else {
								listener.onError(new SocializeException("Unknown Error"));
							}
						}
						else {
							listener.onResult(requestType, result);
						}
					}
					else {
						listener.onError(new SocializeException("No results found in response"));
					}
				}
			}
		}
	}

	class AsyncGetter extends AbstractAsyncProcess<SocializeGetRequest, Void, SocializeEntityResponse<T>> {

		public AsyncGetter(RequestType requestType, SocializeSession session, SocializeActionListener listener) {
			super(requestType, session, listener);
		}

		@Override
		protected SocializeEntityResponse<T> doInBackground(SocializeGetRequest request) throws SocializeException {

			SocializeEntityResponse<T> response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newEntityResponse();
			}
			else {
				response = new SocializeEntityResponse<T>();
			}
			
			switch (requestType) {
			
			case GET:
				T result = SocializeApi.this.get(session, request.getEndpoint(), request.getIds()[0]);
				response.addResult(result);
				break;

			case LIST:
				ListResult<T> results = SocializeApi.this.list(session, request.getEndpoint(), request.getKey(), request.getIds(), request.getStartIndex(), request.getEndIndex());
				response.setResults(results);
				break;
			
			case DELETE:
				SocializeApi.this.delete(session, request.getEndpoint(), request.getIds()[0]);
				break;
			}


			return response;
		}
	}
}
