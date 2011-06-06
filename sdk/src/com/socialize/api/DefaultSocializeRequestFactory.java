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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.socialize.entity.SocializeObject;
import com.socialize.entity.factory.SocializeObjectFactory;
import com.socialize.error.SocializeException;
import com.socialize.oauth.OAuthRequestSigner;

/**
 * @author Jason Polites
 *
 */
public class DefaultSocializeRequestFactory<T extends SocializeObject> implements SocializeRequestFactory<T> {

	private OAuthRequestSigner signer;
	private SocializeObjectFactory<T> objectFactory;
	
	public DefaultSocializeRequestFactory(OAuthRequestSigner signer, SocializeObjectFactory<T> objectFactory) {
		super();
		this.signer = signer;
		this.objectFactory = objectFactory;
	}

	@Override
	public HttpUriRequest getAuthRequest(SocializeSession session, String endpoint, String uuid) throws SocializeException {
		HttpPost post = signer.sign(session, new HttpPost(endpoint));
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>(2);
			data.add(new BasicNameValuePair("payload", "{'udid':" + uuid + "}"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
		}
		catch (UnsupportedEncodingException e) {
			throw new SocializeException(e);
		}
		return post;
	}

	@Override
	public HttpUriRequest getGetRequest(SocializeSession session, String endpoint, String id) throws SocializeException {
		endpoint += id;
		HttpGet get = signer.sign(session, new HttpGet(endpoint));
		return get;
	}

	@Override
	public HttpUriRequest getListRequest(SocializeSession session, String endpoint, String key, String[] ids) throws SocializeException {
		
		HttpPost post = signer.sign(session, new HttpPost(endpoint));
		
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			
			JSONArray array = new JSONArray(Arrays.asList(ids));
			
			JSONObject json = new JSONObject();
			json.put("ids", array);
			json.put("key", key);
			
			data.add(new BasicNameValuePair("payload", json.toString()));
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
		}
		catch (JSONException e) {
			throw new SocializeException(e);
		}
		catch (UnsupportedEncodingException e) {
			throw new SocializeException(e);
		}
		
		return post;
	}

	@Override
	public HttpUriRequest getPutRequest(SocializeSession session, String endpoint, T object) throws SocializeException {
		HttpPut put = signer.sign(session, new HttpPut(endpoint));
		try {
			JSONObject json = objectFactory.toJSON(object);
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("payload", json.toString()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			put.setEntity(entity);
		}
		catch (JSONException e) {
			throw new SocializeException(e);
		}
		catch (UnsupportedEncodingException e) {
			throw new SocializeException(e);
		}
		return put;
	}

	@Override
	public HttpUriRequest getPostRequest(SocializeSession session, String endpoint, T object) throws SocializeException {
		HttpPost post = signer.sign(session, new HttpPost(endpoint));
		try {
			JSONObject json = objectFactory.toJSON(object);
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("payload", json.toString()));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data);
			post.setEntity(entity);
		}
		catch (JSONException e) {
			throw new SocializeException(e);
		}
		catch (UnsupportedEncodingException e) {
			throw new SocializeException(e);
		}
		return post;
	}

}
