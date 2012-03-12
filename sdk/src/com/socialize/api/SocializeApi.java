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

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.socialize.Socialize;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProvider;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.AuthProviders;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.ActionError;
import com.socialize.entity.ListResult;
import com.socialize.entity.Propagation;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.location.SocializeLocationProvider;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.notifications.NotificationChecker;
import com.socialize.provider.SocializeProvider;
import com.socialize.util.AppUtils;
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
	private AuthProviders authProviders;
	private SocializeLogger logger;
	private HttpUtils httpUtils;
	private SocializeLocationProvider locationProvider;
	private NotificationChecker notificationChecker;
	private AppUtils appUtils;
	
	public static enum RequestType {AUTH,PUT,POST,PUT_AS_POST,GET,LIST,LIST_AS_GET,LIST_WITHOUT_ENTITY,DELETE};
	
	public SocializeApi(P provider) {
		super();
		this.provider = provider;
	}
	
	public void clearSession() {
		provider.clearSession();
	}
	
	public void clearSession(AuthProviderType type) {
		provider.clearSession(type);
	}
	
	public SocializeSession loadSession(String endpoint, String key, String secret) throws SocializeException {
		return provider.loadSession(endpoint, key, secret);
	}
	
	public SocializeSession authenticate(Context context, String endpoint, String key, String secret, String uuid) throws SocializeException {
		SocializeSession session = provider.authenticate(endpoint, key, secret, uuid);
		checkNotifications(context, session);
		return session;
	}
	
	public SocializeSession authenticate(Context context, String endpoint, String key, String secret, AuthProviderData data, String udid) throws SocializeException {
		SocializeSession session = provider.authenticate(endpoint, key, secret, data, udid);
		checkNotifications(context, session);
		return session;
	}
	
	@Deprecated
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {
		return provider.authenticate(endpoint, key, secret, uuid);
	}
	
	@Deprecated
	public SocializeSession authenticate(String endpoint, String key, String secret, AuthProviderData data, String uuid) throws SocializeException {
		return provider.authenticate(endpoint, key, secret, data, uuid);
	}
	
	protected void checkNotifications(Context context, SocializeSession session) {
		if(notificationChecker != null) {
			notificationChecker.checkRegistrations(context, session);
		}
	}
	
	protected void addPropagationData(SocializeAction action, ShareOptions shareOptions) {
		if(shareOptions != null) {
			SocialNetwork[] shareTo = shareOptions.getShareTo();
			if(shareTo != null) {
				Propagation propagation = newPropagation();
				
				for (SocialNetwork socialNetwork : shareTo) {
					propagation.addThirdParty(socialNetwork);
				}
				
				SocializeConfig config = Socialize.getSocialize().getConfig();
				String appStore = config.getProperty(SocializeConfig.REDIRECT_APP_STORE);
				if(!StringUtils.isEmpty(appStore)) {
					propagation.addExtraParam("f", appUtils.getAppStoreAbbreviation(appStore));
				}				
				
				action.setPropagation(propagation);
			}
		}
	}
	
	// Mockable
	protected Propagation newPropagation() {
		return new Propagation();
	}

	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {
		return provider.list(session, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS);
	}
	
	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, String idKey, int startIndex, int endIndex) throws SocializeException {
		return provider.list(session, endpoint, key, ids, idKey, startIndex, endIndex);
	}
	
	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex) throws SocializeException {
		return provider.list(session, endpoint, key, ids, startIndex, endIndex);
	}
	
	public ListResult<T> list(SocializeSession session, String endpoint) throws SocializeException {
		return provider.list(session, endpoint, 0, SocializeConfig.MAX_LIST_RESULTS);
	}
	
	public ListResult<T> list(SocializeSession session, String endpoint, int startIndex, int endIndex) throws SocializeException {
		return provider.list(session, endpoint, startIndex, endIndex);
	}
	
	public T get(SocializeSession session, String endpoint, String id) throws SocializeException {
		return provider.get(session, endpoint, id);
	}
	
	public T get(SocializeSession session, String endpoint, String id, ActionType type) throws SocializeException {
		return provider.get(session, endpoint, id, type);
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
	
	public T putAsPost(SocializeSession session, String endpoint, T object) throws SocializeException {
		return provider.putAsPost(session, endpoint, object);
	}
	
	public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex, SocializeActionListener listener) {
		listAsync(session, endpoint, key, ids, "id", startIndex, endIndex, listener);
	}
	
	public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, String idKey, int startIndex, int endIndex, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.LIST, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setKey(key);
		request.setIds(ids);
		request.setIdKey(idKey);
		request.setStartIndex(startIndex);
		request.setEndIndex(endIndex);
		getter.execute(request);
	}
	
	public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
		listAsync(session, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS, listener);
	}
	
	public void listAsync(SocializeSession session, String endpoint, int startIndex, int endIndex, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.LIST_WITHOUT_ENTITY, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setStartIndex(startIndex);
		request.setEndIndex(endIndex);
		getter.execute(request);
	}
	
	public void listAsync(SocializeSession session, String endpoint, SocializeActionListener listener) {
		listAsync(session, endpoint, 0, SocializeConfig.MAX_LIST_RESULTS, listener);
	}

	public void getByEntityAsync(SocializeSession session, String endpoint, String key, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.LIST_AS_GET, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setKey(key);
		getter.execute(request);
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

	/**
	 * Does a POST, but expects a single object in return.
	 * @param session
	 * @param endpoint
	 * @param object
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	public void putAsPostAsync(SocializeSession session, String endpoint, T object, SocializeActionListener listener) {
		AsyncPutter poster = new AsyncPutter(RequestType.PUT_AS_POST, session, listener);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObject(object);
		poster.execute(request);
	}

	public void authenticateAsync(
			Context context,
			String key, 
			String secret, 
			String uuid, 
			final AuthProviderData data,
			final SocializeAuthListener listener, 
			final SocializeSessionConsumer sessionConsumer, 
			boolean do3rdPartyAuth) {

		SocializeActionListener wrapper = null;
		
		final SocializeAuthListener localListener = new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				if(listener != null) {
					listener.onError(error);
				}
				else {
					if(logger != null) {
						logger.error("Failure during auth", error);
					}
				}
			}
			
			@Override
			public void onCancel() {
				if(listener != null) {
					listener.onCancel();
				}
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				if(sessionConsumer != null) {
					sessionConsumer.setSession(session);
				}
				if(listener != null) {
					listener.onAuthSuccess(session);
				}				
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				if(listener != null) {
					listener.onAuthFail(error);
				}	
				else {
					if(logger != null) {
						logger.error("Failure during auth", error);
					}
				}
			}
		};		
		
		wrapper = new SocializeActionListener() {
			
			@Override
			public void onResult(RequestType type, SocializeResponse response) {
				SocializeAuthResponse authResponse = (SocializeAuthResponse) response;
				localListener.onAuthSuccess(authResponse.getSession());
			}
			
			@Override
			public void onError(SocializeException error) {
				if(httpUtils != null && httpUtils.isAuthError(error)) {
					localListener.onAuthFail(error);
				}
				else {
					localListener.onError(error);
				}
			}
		};

		final SocializeAuthRequest request = new SocializeAuthRequest();
		
		request.setEndpoint("/authenticate/");
		request.setConsumerKey(key);
		request.setConsumerSecret(secret);
		request.setUdid(uuid);
		request.setAuthProviderData(data);
		
		AuthProviderType authProviderType = getAuthProviderType(data);
		
		if(do3rdPartyAuth && !authProviderType.equals(AuthProviderType.SOCIALIZE)) {
			handle3rdPartyAuth(context, request, wrapper, localListener, key, secret);
		}
		else {
			// Do normal auth
			handleRegularAuth(context, request, wrapper);
		}
	}
	
	protected AuthProviderType getAuthProviderType(AuthProviderData data) {
		if(data == null) {
			return AuthProviderType.SOCIALIZE;
		}
		
		@SuppressWarnings("deprecation")
		AuthProviderType authProviderType = data.getAuthProviderType();
		
		AuthProviderInfo authProviderInfo = data.getAuthProviderInfo();
		
		if(authProviderInfo != null) {
			authProviderType = authProviderInfo.getType();
		}
		
		return authProviderType;
	}
	
	@SuppressWarnings("deprecation")
	protected void validate(AuthProviderData data) throws SocializeException{
		AuthProviderInfo authProviderInfo = data.getAuthProviderInfo();
		
		if(authProviderInfo != null) {
			authProviderInfo.validate();
		}
		else {
			// Legacy
			validateLegacy(data);
		}
	}
	
	@Deprecated
	protected void validateLegacy(AuthProviderData data) throws SocializeException {
		AuthProviderType authProviderType = getAuthProviderType(data);
		
		String appId3rdParty = data.getAppId3rdParty();
		if(authProviderType != null && 
				authProviderType.equals(AuthProviderType.FACEBOOK) && 
				StringUtils.isEmpty(appId3rdParty)) {
			throw new SocializeException("No app ID found for auth type FACEBOOK");
		}
	}
	
	protected void handleRegularAuth(Context context, SocializeAuthRequest request, SocializeActionListener wrapper) {
		AsyncAuthenicator authenicator = new AsyncAuthenicator(context, RequestType.AUTH, null, wrapper);
		authenicator.execute(request);
	}
	
	protected void handle3rdPartyAuth(
			final Context context,
			final SocializeAuthRequest request,
			final SocializeActionListener fWrapper,
			final SocializeAuthListener listener, 
			String key, String secret)  {

		final AuthProviderData authProviderData = request.getAuthProviderData();
		
		// Try loading the session first
		SocializeSession session = null;
		
		AuthProviderType authProviderType = getAuthProviderType(authProviderData);
		
		try {
			validate(authProviderData);
		} 
		catch (SocializeException error) {
			if(listener != null) {
				listener.onError(error);
			}
			else if(logger != null) {
				logger.error("Error validating auth provider data", error);
			}
		}
		
		try {
			session = loadSession(request.getEndpoint(), key, secret);
		}
		catch (SocializeException e) {
			// No need to throw this, just log it
			logger.warn("Failed to load saved session data", e);
		}
					
		if(session == null || !provider.validateSession(session, authProviderData)) {
			// Get the provider for the type
			AuthProvider<AuthProviderInfo> authProvider = authProviders.getProvider(authProviderType);
			
			if(authProvider != null) {
				
				AuthProviderListener authProviderListener = new AuthProviderListener() {
					
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
						authProviderData.setSecret3rdParty(response.getSecret());
						
						// Do normal auth
						handleRegularAuth(context, request, fWrapper);
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						if(listener != null) {
							listener.onAuthFail(error);
						}
					}

					@Override
					public void onCancel() {
						if(listener != null) {
							listener.onCancel();
						}
					}
				};				
				
				AuthProviderInfo authProviderInfo = authProviderData.getAuthProviderInfo();
				
				if(authProviderInfo != null) {
					authProvider.authenticate(authProviderInfo, authProviderListener);
				}
				else {
					// Legacy
					authenticateLegacy(authProviderData, authProvider, request, authProviderListener);
				}
			}
			else {
				logger.error("No provider found for auth type [" +
						authProviderType.getName() +
						"]");
			}
		}
		else {
			// We already have a session
			// then just call the listener
			if(listener != null) {
				listener.onAuthSuccess(session);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void authenticateLegacy(AuthProviderData authProviderData, AuthProvider<AuthProviderInfo> authProvider, SocializeAuthRequest request, AuthProviderListener authProviderListener) {
		String appId3rdParty = authProviderData.getAppId3rdParty();
		authProvider.authenticate(request, appId3rdParty, authProviderListener);
	}
	
	public void setResponseFactory(SocializeResponseFactory<T> responseFactory) {
		this.responseFactory = responseFactory;
	}
	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setNotificationChecker(NotificationChecker notificationChecker) {
		this.notificationChecker = notificationChecker;
	}
	
	public void setAuthProviders(AuthProviders authProviders) {
		this.authProviders = authProviders;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}

	public void setLocationProvider(SocializeLocationProvider locationProvider) {
		this.locationProvider = locationProvider;
	}

	abstract class AbstractAsyncProcess<Params, Progress, Result extends SocializeResponse> extends AsyncTask<Params, Progress, Result> {

		RequestType requestType;
		SocializeSession session;
		Exception error = null;
		SocializeActionListener listener = null;
		Context context;
		
		public AbstractAsyncProcess(Context context, RequestType requestType, SocializeSession session, SocializeActionListener listener) {
			this(requestType, session, listener);
			this.context = context;
		}		
		
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
			catch (Exception error) {
				this.error = error;
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Result result) {
			if(listener != null) {
				if(error != null) {
					listener.onError(SocializeException.wrap(error));
				}
				else {
					listener.onResult(requestType, result);
				}
			}
		}

		protected abstract Result doInBackground(Params param) throws SocializeException;
	}
	
	class AsyncAuthenicator extends AbstractAsyncProcess<SocializeAuthRequest, Void, SocializeAuthResponse> {

		public AsyncAuthenicator(Context context, RequestType requestType, SocializeSession session, SocializeActionListener listener) {
			super(context, requestType, session, listener);
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
			AuthProviderType authProviderType = getAuthProviderType(authProviderData);
			
			if(authProviderType == null || authProviderType.equals(AuthProviderType.SOCIALIZE)) {
				session = SocializeApi.this.authenticate(context, request.getEndpoint(), request.getConsumerKey(), request.getConsumerSecret(), request.getUdid());
			}
			else {
				session = SocializeApi.this.authenticate(context, request.getEndpoint(), request.getConsumerKey(), request.getConsumerSecret(), authProviderData, request.getUdid());
			}
			
			response.setSession(session);
			
			return response;
		}
	}
	
	protected void setLocation(SocializeAction action, Location location) {
		if(location == null && locationProvider != null) {
			location = locationProvider.getLocation();
		}
		if(location != null) {
			action.setLon(location.getLongitude());
			action.setLat(location.getLatitude());
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
				
				response.setResults(results);
				
				break;

			case PUT:
				
				if(request.getObjects() != null) {
					results = SocializeApi.this.put(session, request.getEndpoint(), request.getObjects());
				}
				else if(request.getObject() != null) {
					results = SocializeApi.this.put(session, request.getEndpoint(), request.getObject());
				}
				
				response.setResults(results);
				
				break;
				
			case PUT_AS_POST:
				
				if(request.getObject() != null) {
					T result = SocializeApi.this.putAsPost(session, request.getEndpoint(), request.getObject());
					response.addResult(result);
				}
			}

			return response;
		}

		@Override
		protected void onPostExecute(SocializeEntityResponse<T> result) {
			if(listener != null) {
				if(error != null) {
					listener.onError(SocializeException.wrap(error));
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
								listener.onError(new SocializeException("No results found in response"));
							}
						}
						else {
							if(errors != null && errors.size() > 0) {
								for (ActionError actionError : errors) {
									if(logger != null) {
										if(!StringUtils.isEmpty(actionError.getMessage())) {
											logger.warn(actionError.getMessage());
										}
									}
								}
							}
							
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
			
			ListResult<T> results = null;
			
			switch (requestType) {
			
			case GET:
				T result = SocializeApi.this.get(session, request.getEndpoint(), request.getIds()[0]);
				response.addResult(result);
				break;

			case LIST:
				results = SocializeApi.this.list(session, request.getEndpoint(), request.getKey(), request.getIds(), request.getIdKey(), request.getStartIndex(), request.getEndIndex());
				response.setResults(results);
				break;
				
			case LIST_AS_GET:
				results = SocializeApi.this.list(session, request.getEndpoint(), request.getKey(), request.getIds(), request.getIdKey(), request.getStartIndex(), request.getEndIndex());
				response.setResults(results);
				break;				
				
			case LIST_WITHOUT_ENTITY:
				results = SocializeApi.this.list(session, request.getEndpoint(), request.getStartIndex(), request.getEndIndex());
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
