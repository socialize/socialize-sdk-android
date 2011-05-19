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

import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeApiError;
import com.socialize.listener.SocializeListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 *
 * @param <T>
 * @param <P>
 */
public class SocializeService<T extends SocializeObject, P extends SocializeProvider<T>> {

	private SocializeListener<T> listener;
	private P provider;
	private SocializeResponseFactory<T> responseFactory;

	public static enum RequestType {AUTH,PUT,POST,GET,LIST};
	
	public SocializeService(P provider) {
		super();
		this.provider = provider;
	}
	
	public SocializeService(P provider, SocializeResponseFactory<T> responseFactory) {
		this(provider);
		this.responseFactory = responseFactory;
	}

	public SocializeSession authenticate(String key, String secret, String uuid) throws SocializeApiError {
		return provider.authenticate(key, secret, uuid);
	}

	public List<T> list(SocializeSession session, String endpoint, String key) throws SocializeApiError {
		return provider.list(session, endpoint, key);
	}
	
	public T get(SocializeSession session, String endpoint, int[] ids) throws SocializeApiError {
		return provider.get(session, endpoint, ids);
	}
	
	public List<T> put(SocializeSession session, String endpoint, T object) throws SocializeApiError {
		return provider.put(session, endpoint, object);
	}

	public List<T> post(SocializeSession session, String endpoint, T object) throws SocializeApiError {
		return provider.post(session, endpoint, object);
	}

	public void listAsync(SocializeSession session, String endpoint, String key) {
		AsyncGetter getter = new AsyncGetter(RequestType.LIST, session);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setKey(key);
		getter.execute(request);
	}

	public void getAsync(SocializeSession session, String endpoint, int[] ids) {
		AsyncGetter getter = new AsyncGetter(RequestType.GET, session);
		SocializeGetRequest request = new SocializeGetRequest();
		request.setEndpoint(endpoint);
		request.setIds(ids);
		getter.execute(request);
	}

	@SuppressWarnings("unchecked")
	public void putAsync(SocializeSession session, String endpoint, T object) {
		AsyncPutter poster = new AsyncPutter(RequestType.PUT, session);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObject(object);
		poster.execute(request);
	}

	@SuppressWarnings("unchecked")
	public void postAsync(SocializeSession session, String endpoint, T object) {
		AsyncPutter poster = new AsyncPutter(RequestType.POST, session);
		SocializePutRequest<T> request = new SocializePutRequest<T>();
		request.setEndpoint(endpoint);
		request.setObject(object);
		poster.execute(request);
	}
	
	public void authenticateAsync(String key, String secret, String uuid) {
		AsyncAuthenicator authenicator = new AsyncAuthenicator(RequestType.AUTH, null);
		SocializeAuthRequest request = new SocializeAuthRequest();
		request.setConsumerKey(key);
		request.setConsumerSecret(secret);
		request.setUuid(uuid);
		authenicator.execute(request);
	}

	public void setListener(SocializeListener<T> listener) {
		this.listener = listener;
	}
	
	public void setResponseFactory(SocializeResponseFactory<T> responseFactory) {
		this.responseFactory = responseFactory;
	}

	abstract class AbstractAsyncProcess<Params, Progress, Result extends SocializeResponse> extends AsyncTask<Params, Progress, Result> {

		RequestType requestType;
		SocializeSession session;
		SocializeApiError error = null;
		
		public AbstractAsyncProcess(RequestType requestType, SocializeSession session) {
			super();
			this.requestType = requestType;
			this.session = session;
		}

		@Override
		protected final Result doInBackground(Params... params) {
			Params request = params[0];
			Result result = null;
			try {
				result = doInBackground(request);
			}
			catch (SocializeApiError error) {
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

		protected abstract Result doInBackground(Params param) throws SocializeApiError;
	}
	
	class AsyncAuthenicator extends AbstractAsyncProcess<SocializeAuthRequest, Void, SocializeAuthResponse> {

		public AsyncAuthenicator(RequestType requestType, SocializeSession session) {
			super(requestType, session);
		}

		@Override
		protected SocializeAuthResponse doInBackground(SocializeAuthRequest request) throws SocializeApiError {
			SocializeAuthResponse response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newAuthResponse();
			}
			else {
				response = new SocializeAuthResponse();
			}
			
			SocializeSession session = SocializeService.this.authenticate(request.getConsumerKey(), request.getConsumerSecret(), request.getUuid());
			response.setSession(session);
			return response;
		}
	}

	class AsyncPutter extends AbstractAsyncProcess<SocializePutRequest<T>, Void, SocializeEntityResponse<T>> {

		public AsyncPutter(RequestType requestType, SocializeSession session) {
			super(requestType, session);
		}

		@Override
		protected SocializeEntityResponse<T> doInBackground(SocializePutRequest<T> request) throws SocializeApiError {

			SocializeEntityResponse<T> response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newEntityResponse();
			}
			else {
				response = new SocializeEntityResponse<T>();
			}

			List<T> results = null;

			switch (requestType) {
			
			case POST:
				results = SocializeService.this.post(session, request.getEndpoint(), request.getObject());
				break;

			case PUT:
				results = SocializeService.this.put(session, request.getEndpoint(), request.getObject());
				break;
			}

			response.setResults(results);

			return response;
		}
	}

	class AsyncGetter extends AbstractAsyncProcess<SocializeGetRequest, Void, SocializeEntityResponse<T>> {

		
		public AsyncGetter(RequestType requestType, SocializeSession session) {
			super(requestType, session);
		}

		@Override
		protected SocializeEntityResponse<T> doInBackground(SocializeGetRequest request) throws SocializeApiError {

			SocializeEntityResponse<T> response = null;
			
			if(responseFactory != null) {
				response = responseFactory.newEntityResponse();
			}
			else {
				response = new SocializeEntityResponse<T>();
			}
			
			switch (requestType) {
			
			case GET:
				T result = SocializeService.this.get(session, request.getEndpoint(), request.getIds());
				response.addResult(result);
				break;

			case LIST:
				List<T> results = SocializeService.this.list(session, request.getEndpoint(), request.getKey());
				response.setResults(results);
				break;
			}

			return response;
		}
	}
}
