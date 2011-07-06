/*
 * Copyright (c) 2011 Socialize Inc* Permission is hereby granted, free of charge, to any person obtaining a copy
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

import com.socialize.config.SocializeConfig;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeActionListener;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 *
 * @param <T>
 * @param <P>
 */
public class SocializeApi<T extends SocializeObject, P extends SocializeProvider<T>> {

	private P provider;
	private SocializeResponseFactory<T> responseFactory;
	private SocializeConfig config;
	
	public static enum RequestType {AUTH,PUT,POST,GET,LIST,DELETE};
	
	public SocializeApi(P provider) {
		super();
		this.provider = provider;
	}
	
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid) throws SocializeException {
		return provider.authenticate(endpoint, key, secret, uuid);
	}

	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {
		return provider.list(session, endpoint, key, ids);
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
	
	public void listAsync(SocializeSession session, String endpoint, String key, String[] ids, SocializeActionListener listener) {
		AsyncGetter getter = new AsyncGetter(RequestType.LIST, session, listener);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setKey(key);
		request.setIds(ids);
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
	
	public void authenticateAsync(String key, String secret, String uuid, final SocializeAuthListener listener, final SocializeSessionConsumer sessionConsumer) {
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
					listener.onError(error);
				}
			};
		}
		
		AsyncAuthenicator authenicator = new AsyncAuthenicator(RequestType.AUTH, null, wrapper);
		SocializeAuthRequest request = new SocializeAuthRequest();
		request.setEndpoint("/authenticate/");
		request.setConsumerKey(key);
		request.setConsumerSecret(secret);
		request.setUuid(uuid);
		authenicator.execute(request);
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
		protected SocializeAuthResponse doInBackground(SocializeAuthRequest request) throws SocializeException {
			SocializeAuthResponse response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newAuthResponse();
			}
			else {
				response = new SocializeAuthResponse();
			}
			
			SocializeSession session = SocializeApi.this.authenticate(request.getEndpoint(), request.getConsumerKey(), request.getConsumerSecret(), request.getUuid());
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
				ListResult<T> results = SocializeApi.this.list(session, request.getEndpoint(), request.getKey(), request.getIds());
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
