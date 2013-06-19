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
package com.socialize.api;

import com.socialize.auth.AuthProviderData;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.entity.SocializeObject;
import com.socialize.error.SocializeException;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jason Polites
 *
 */
public interface SocializeRequestFactory<T extends SocializeObject> {
	
	public HttpUriRequest getAuthRequest(SocializeSession session, String endpoint, String udid, String advertiserId, AuthProviderData data) throws SocializeException;
	
	public HttpUriRequest getAuthRequestWith3rdParty(SocializeSession session, String endpoint, String udid, String advertiserId, UserProviderCredentials userProviderCredentials) throws SocializeException;
	
	public HttpUriRequest getGetRequest(SocializeSession session, String endpoint, String id) throws SocializeException;

	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, String key, String[] ids, String idKey) throws SocializeException;
	
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint,String key, String[] ids, String idKey, Map<String, String> extraParams, int startIndex, int endIndex) throws SocializeException;
	
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint,String key, String[] ids) throws SocializeException;

	public HttpUriRequest getListRequest(SocializeSession session, String endpoint,String key, String[] ids, int startIndex, int endIndex) throws SocializeException;
	
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint) throws SocializeException;
	
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, int startIndex, int endIndex) throws SocializeException;
	
	public HttpUriRequest getPutRequest(SocializeSession session, String endpoint, Collection<T> entities) throws SocializeException;

	public HttpUriRequest getPostRequest(SocializeSession session, String endpoint, Collection<T> entities) throws SocializeException;

	public HttpUriRequest getPutRequest(SocializeSession session, String endpoint, T entity) throws SocializeException;

	public HttpUriRequest getPostRequest(SocializeSession session, String endpoint, T entity) throws SocializeException;

	public HttpUriRequest getDeleteRequest(SocializeSession session, String endpoint, String id) throws SocializeException;
}
