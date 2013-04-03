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

import com.socialize.android.ioc.IBeanFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;


/**
 * @author Jason Polites
 *
 */
public class AsyncHttpRequestProvider implements HttpRequestProvider {

	private IBeanFactory<AsyncHttpRequestProcessor> requestProcessorFactory;
	
	/* (non-Javadoc)
	 * @see com.socialize.net.HttpRequestProvider#post(org.apache.http.client.methods.HttpPost, com.socialize.net.HttpRequestListener)
	 */
	@Override
	public void post(HttpPost request, HttpRequestListener listener) {
		doRequest(request, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.net.HttpRequestProvider#get(org.apache.http.client.methods.HttpGet, com.socialize.net.HttpRequestListener)
	 */
	@Override
	public void get(HttpGet request, HttpRequestListener listener) {
		doRequest(request, listener);
	}

	protected void doRequest(HttpUriRequest request, HttpRequestListener listener) {
		AsyncHttpRequestProcessor processor = requestProcessorFactory.getBean();
		AsyncHttpRequest arequest = new AsyncHttpRequest();
		arequest.setListener(listener);
		arequest.setRequest(request);
		processor.execute(arequest);
	}
	
	public void setRequestProcessorFactory(IBeanFactory<AsyncHttpRequestProcessor> requestProcessorFactory) {
		this.requestProcessorFactory = requestProcessorFactory;
	}
	
}
