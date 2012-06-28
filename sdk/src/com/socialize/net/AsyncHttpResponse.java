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

import org.apache.http.HttpResponse;


/**
 * @author Jason Polites
 *
 */
public class AsyncHttpResponse {

	private AsyncHttpRequest request;
	private Exception error;
	private HttpResponse response;
	private String responseData;
	
	public AsyncHttpRequest getRequest() {
		return request;
	}
	
	public void setRequest(AsyncHttpRequest request) {
		this.request = request;
	}
	
	public Exception getError() {
		return error;
	}
	
	public void setError(Exception error) {
		this.error = error;
	}
	
	public HttpResponse getResponse() {
		return response;
	}
	
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
}
