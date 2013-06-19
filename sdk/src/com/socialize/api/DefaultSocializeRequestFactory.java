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
import com.socialize.auth.AuthProviderInfo;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.UserProviderCredentialsMap;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.SocializeObject;
import com.socialize.entity.SocializeObjectFactory;
import com.socialize.error.SocializeException;
import com.socialize.oauth.OAuthRequestSigner;
import com.socialize.oauth.OAuthSignListener;
import com.socialize.util.AppUtils;
import com.socialize.util.StringUtils;
import com.socialize.util.UrlBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Jason Polites
 *
 */
public class DefaultSocializeRequestFactory<T extends SocializeObject> implements SocializeRequestFactory<T> {

	private OAuthRequestSigner oauthSigner;
	private SocializeObjectFactory<T> objectFactory;
	private OAuthSignListener signListener;
	private AppUtils appUtils;
	
	public DefaultSocializeRequestFactory(OAuthRequestSigner signer, SocializeObjectFactory<T> objectFactory) {
		super();
		this.oauthSigner = signer;
		this.objectFactory = objectFactory;
	}
	
	public DefaultSocializeRequestFactory() {
		super();
	}
	
	public void setOauthSigner(OAuthRequestSigner signer) {
		this.oauthSigner = signer;
	}

	public void setObjectFactory(SocializeObjectFactory<T> objectFactory) {
		this.objectFactory = objectFactory;
	}

	public void setSignListener(OAuthSignListener signListener) {
		this.signListener = signListener;
	}
	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	@Override
	public HttpUriRequest getAuthRequestWith3rdParty(SocializeSession session, String endpoint, String udid, String advertiserId,  UserProviderCredentials userProviderCredentials) throws SocializeException {
		HttpPost post = new HttpPost(endpoint);
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>(1);
			
			JSONObject json = new JSONObject();
			json.put("udid", udid);
			json.put("a", advertiserId);
			
			AuthProviderInfo authProviderInfo = userProviderCredentials.getAuthProviderInfo();

			if(authProviderInfo != null) {
				json.put("auth_type", authProviderInfo.getType().getId());
				
				if(!StringUtils.isEmpty(userProviderCredentials.getAccessToken())) {
					json.put("auth_token", userProviderCredentials.getAccessToken());
				}
				
				if(!StringUtils.isEmpty(userProviderCredentials.getTokenSecret())) {
					json.put("auth_token_secret", userProviderCredentials.getTokenSecret());
				}
				
				if(!StringUtils.isEmpty(userProviderCredentials.getUserId())) {
					json.put("auth_id", userProviderCredentials.getUserId());
				}
			}

			data.add(new BasicNameValuePair("payload", json.toString()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
			
			sign(session,post);
		}
		catch (UnsupportedEncodingException e) {
			throw new SocializeException(e);
		}
		catch (JSONException e) {
			throw new SocializeException(e);
		}
		return post;
	}

	@Override
	public HttpUriRequest getAuthRequest(SocializeSession session, String endpoint, String udid, String advertiserId, AuthProviderData data) throws SocializeException {
		
		UserProviderCredentialsMap userProviderCredentialsMap = session.getUserProviderCredentials();
		
		if(userProviderCredentialsMap != null) {
			AuthProviderInfo authProviderInfo = data.getAuthProviderInfo();
			if(authProviderInfo != null) {
				UserProviderCredentials userProviderCredentials = userProviderCredentialsMap.get(authProviderInfo.getType());
				return getAuthRequestWith3rdParty(session, endpoint, udid, advertiserId, userProviderCredentials);
			}
			else {
				throw new SocializeException("No provider info found in stored session");
			}
		}
		else {
			throw new SocializeException("No provider credentials found in stored session");
		}
	}

	@Override
	public HttpUriRequest getGetRequest(SocializeSession session, String endpoint, String id) throws SocializeException {
		endpoint += URLEncoder.encode(id) + "/";
		return getGetRequest(session, endpoint);
	}
	
	public HttpUriRequest getGetRequest(SocializeSession session, String endpoint) throws SocializeException {
		HttpGet get = sign(session, new HttpGet(endpoint));
		return get;
	}

	@Override
	public HttpUriRequest getDeleteRequest(SocializeSession session, String endpoint, String id) throws SocializeException {
		endpoint += URLEncoder.encode(id) + "/";
		HttpDelete del = sign(session, new HttpDelete(endpoint));
		return del;
	}

	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint) throws SocializeException {
		return getListRequest(session, endpoint, null, null, 0, SocializeConfig.MAX_LIST_RESULTS);
	}

	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, int startIndex, int endIndex) throws SocializeException {
		return getListRequest(session, endpoint, null, null, startIndex, endIndex);
	}

	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, String key, String[] ids, String idKey) throws SocializeException {
		return getListRequest(session, endpoint, key, ids, idKey, null, 0, SocializeConfig.MAX_LIST_RESULTS);
	}

	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, String key, String[] ids, String idKey, Map<String, String> extraParams, int startIndex, int endIndex) throws SocializeException {

		// A List is a GET request with params
		// See: http://en.wikipedia.org/wiki/Representational_State_Transfer
		UrlBuilder builder = new UrlBuilder();
		builder.start(endpoint);
		
		if(!StringUtils.isEmpty(key)) {
			builder.addParam("entity_key", key);
		}
		
		if(ids != null) {
			for (String id : ids) {
				builder.addParam(idKey, id);
			}
		}
		
		if(endIndex <= 0) {
			endIndex = SocializeConfig.MAX_LIST_RESULTS;
		}
		
		builder.addParam("first", String.valueOf(startIndex));
		builder.addParam("last", String.valueOf(endIndex));
		
		if(extraParams != null) {
			Set<Entry<String, String>> entrySet = extraParams.entrySet();
			for (Entry<String, String> entry : entrySet) {
				builder.addParam(entry.getKey(), entry.getValue());
			}
		}
		
		HttpGet get = new HttpGet(builder.toString());

		sign(session, get);
	
		return get;
	}

	/**
	 * @see #getListRequest(SocializeSession, String, String, String[], int, int)
	 */
	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {
		return getListRequest(session, endpoint, key, ids, 0, SocializeConfig.MAX_LIST_RESULTS);
	}

	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, String key, String[] ids, int startIndex, int endIndex) throws SocializeException {
		return getListRequest(session, endpoint, key, ids, "id", null, startIndex, endIndex);
	}
	
	@Override
	public HttpUriRequest getPutRequest(SocializeSession session, String endpoint, T entity) throws SocializeException {
		HttpPut put = new HttpPut(endpoint); 
		populatePutPost(session, put, entity);
		return put;
	}

	@Override
	public HttpUriRequest getPostRequest(SocializeSession session, String endpoint, T entity) throws SocializeException {
		HttpPost post = new HttpPost(endpoint);
		populatePutPost(session, post, entity);
		return post;
	}

	@Override
	public HttpUriRequest getPutRequest(SocializeSession session, String endpoint, Collection<T> objects) throws SocializeException {
		HttpPut put = new HttpPut(endpoint);
		populatePutPost(session, put, objects);
		return put;
	}

	@Override
	public HttpUriRequest getPostRequest(SocializeSession session, String endpoint, Collection<T> objects) throws SocializeException {
		HttpPost post = new HttpPost(endpoint);
		populatePutPost(session, post, objects);
		return post;
	}
	
	private void populatePutPost(SocializeSession session, HttpEntityEnclosingRequestBase request, Collection<T> objects) throws SocializeException {
		try {
			String payload = objectFactory.toJSON(objects).toString();
			populatePutPost(session, request, payload);
		}
		catch (JSONException e) {
			throw new SocializeException(e);
		}
	}
	
	private void populatePutPost(SocializeSession session, HttpEntityEnclosingRequestBase request, T object) throws SocializeException {
		try {
			String payload = objectFactory.toJSON(object).toString();
			populatePutPost(session, request, payload);
		}
		catch (JSONException e) {
			throw new SocializeException(e);
		}
	}
	
	private void populatePutPost(SocializeSession session, HttpEntityEnclosingRequestBase request, String payload) throws SocializeException {
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("payload", payload));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, "UTF-8");
			request.setEntity(entity);
			sign(session, request);
		}
		catch (UnsupportedEncodingException e) {
			throw new SocializeException(e);
		}
	}

	protected <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
		
		// Add socialize headers
		if(appUtils != null) {
			request.addHeader("User-Agent", appUtils.getUserAgentString());
		}
		
		return oauthSigner.sign(session, request, signListener);
	}

}
