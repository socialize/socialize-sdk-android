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
package com.socialize.provider;

import android.content.Context;
import com.socialize.api.SocializeSession;
import com.socialize.api.WritableSession;
import com.socialize.api.action.ActionType;
import com.socialize.auth.AuthProviderData;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jason Polites
 *
 * @param <T>
 */
public interface SocializeProvider<T> {

	public void init(Context context);
	
	public WritableSession loadSession(String endpoint, String key, String secret) throws SocializeException;
	
	public boolean validateSession(SocializeSession loaded, AuthProviderData data);
	
	public void clearSession();
	
	public void clearSession(AuthProviderType type);
	
	public SocializeSession authenticate(String endpoint, String key, String secret, String uuid, String advertiserId) throws SocializeException;

	public SocializeSession authenticate(String endpoint, String key, String secret, AuthProviderData data, String udid, String advertiserId) throws SocializeException;

	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, String idKey, Map<String, String> extraParams, int startIndex, int endIndex) throws SocializeException;
	
	public ListResult<T> list(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex) throws SocializeException;
	
	public ListResult<T> list(SocializeSession session, String endpoint, int startIndex, int endIndex) throws SocializeException;
	
	public T get(SocializeSession session, String endpoint, String id) throws SocializeException;
	
	public T get(SocializeSession session, String endpoint, String id, ActionType type) throws SocializeException;
	
	public ListResult<T> put(SocializeSession session, String endpoint, T object) throws SocializeException;

	public T putAsPost(SocializeSession session, String endpoint, T object) throws SocializeException;
	
	public ListResult<T> put(SocializeSession session, String endpoint, Collection<T> object) throws SocializeException;

	public ListResult<T> post(SocializeSession session, String endpoint, T object, boolean isJSONResponse) throws SocializeException;
	
	public ListResult<T> post(SocializeSession session, String endpoint, Collection<T> object, boolean isJSONResponse) throws SocializeException;
	
	public void delete(SocializeSession session, String endpoint, String id) throws SocializeException;
}
