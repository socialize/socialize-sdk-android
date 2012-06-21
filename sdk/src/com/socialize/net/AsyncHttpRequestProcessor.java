/*
 * Copyright (c) 2012 Socialize Inc.
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
package com.socialize.net;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import android.os.AsyncTask;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.log.SocializeLogger;
import com.socialize.util.HttpUtils;
import com.socialize.util.IOUtils;


/**
 * @author Jason Polites
 *
 */
public class AsyncHttpRequestProcessor extends AsyncTask<AsyncHttpRequest, Void, AsyncHttpResponse> {

	private HttpClientFactory clientFactory;
	private HttpUtils httpUtils;
	private IOUtils ioUtils;
	private SocializeLogger logger;
	
	@Override
	protected AsyncHttpResponse doInBackground(AsyncHttpRequest... params) {
		
		AsyncHttpRequest request = params[0];
		AsyncHttpResponse response = new AsyncHttpResponse();
		response.setRequest(request);
		
		try {
			HttpUriRequest httpRequest = request.getRequest();
			
			if(!clientFactory.isDestroyed()) {	
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Request: " + httpRequest.getMethod() + " " + httpRequest.getRequestLine().getUri());
					
					StringBuilder builder = new StringBuilder();
					Header[] allHeaders = httpRequest.getAllHeaders();
					
					for (Header header : allHeaders) {
						builder.append(header.getName());
						builder.append(":");
						builder.append(header.getValue());
						builder.append("\n");
					}
					
					logger.debug("Executing request \nurl:[" +
							httpRequest.getURI().toString() +
							"] \nheaders:\n" +
							builder.toString() +
							"");					
				}
				
				HttpClient client = clientFactory.getClient();
				
				HttpResponse httpResponse = client.execute(httpRequest);
				
				response.setResponse(httpResponse);
				
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Response: " + httpResponse.getStatusLine().getStatusCode());
				}
				
				HttpEntity entity = null;
				
				try {
					entity = httpResponse.getEntity();
					
					if(httpUtils.isHttpError(httpResponse)) {
						String msg = ioUtils.readSafe(entity.getContent());
						throw new SocializeApiError(httpUtils, httpResponse.getStatusLine().getStatusCode(), msg);
					}
					else {
						String responseData = ioUtils.readSafe(entity.getContent());
						response.setResponseData(responseData);
					}					
				}
				finally {
					closeEntity(entity);
				}
			}
			else {
				throw new SocializeException("Cannot execute http request.  HttpClient factory was destroyed.");
			}		
		}
		catch (Exception e) {
			response.setError(e);
		}
		
		return response;
		
	}
	
	@Override
	protected void onPostExecute(AsyncHttpResponse result) {
		AsyncHttpRequest request = result.getRequest();
		HttpRequestListener listener = request.getListener();

		if(listener != null) {
			HttpResponse response = result.getResponse();
			String responseData = result.getResponseData();
			if(result.getError() != null) {
				listener.onError(result.getError(), response, response.getStatusLine().getStatusCode(), responseData);
			}
			else {
				listener.onSuccess(response, responseData);
			}
		}
			
		if(result.getError() != null && logger != null) {
			logger.error("Error during http request", result.getError());
		}
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
	
	public void setClientFactory(HttpClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public void setHttpUtils(HttpUtils httpUtils) {
		this.httpUtils = httpUtils;
	}
	
	public void setIoUtils(IOUtils ioUtils) {
		this.ioUtils = ioUtils;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
