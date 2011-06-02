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

import org.apache.http.client.methods.HttpUriRequest;

import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;

/**
 * @author Jason Polites
 *
 */
public interface SocializeRequestFactory<T extends SocializeObject> {

	/**
	 * 
	 * @param key
	 * @param secret
	 * @param uuid
	 * @return
	 * @throws SocializeException 
	 */
	public HttpUriRequest getAuthRequest(SocializeSession session, String endpoint, String uuid) throws SocializeException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws SocializeException 
	 */
	public HttpUriRequest getGetRequest(SocializeSession session, String endpoint,String id) throws SocializeException;
	
	/**
	 * 
	 * @param key
	 * @param ids
	 * @return
	 * @throws SocializeException 
	 */
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint,String key, String[] ids) throws SocializeException;
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public HttpUriRequest getPutRequest(SocializeSession session, String endpoint,T entity);
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public HttpUriRequest getPostRequest(SocializeSession session, String endpoint,T entity);
}
